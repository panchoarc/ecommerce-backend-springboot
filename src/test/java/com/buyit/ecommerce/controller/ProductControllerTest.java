package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.config.TestContainersConfig;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.dto.request.category.CategoryRequest;
import com.buyit.ecommerce.dto.request.product.CreateProductRequest;
import com.buyit.ecommerce.dto.request.product.ProductRequest;
import com.buyit.ecommerce.dto.response.category.CategoryResponse;
import com.buyit.ecommerce.dto.response.product.CreateProductResponse;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.repository.UsersRepository;
import com.buyit.ecommerce.service.AuthService;
import com.buyit.ecommerce.service.KeycloakService;
import com.buyit.ecommerce.util.TokenExtractor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Slf4j
class ProductControllerTest extends TestContainersConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TokenExtractor tokenExtractor;

    public String token;

    @BeforeEach()
    void setUp() throws JsonProcessingException {
        UserRegisterDTO adminUserDTO = new UserRegisterDTO();
        adminUserDTO.setFirstName("Test");
        adminUserDTO.setLastName("User");
        adminUserDTO.setRole("user");
        adminUserDTO.setEmail("testuser@example.com");
        adminUserDTO.setUserName("testuser");
        adminUserDTO.setPassword("SecurePass123!");
        adminUserDTO.setRole("admin");

        authService.createUser(adminUserDTO);
        token = tokenExtractor.extractTokenFromUser(adminUserDTO.getUserName(), adminUserDTO.getPassword());

    }

    @AfterEach
    void tearDown() {

        List<User> users = usersRepository.findAll();
        for (User user : users) {
            keycloakService.deleteUserFromKeycloak(user.getKeycloakUserId());
        }
    }

    @Test
    @Transactional
    @Rollback
    void givenNoToken_whenGetProducts_thenUnauthorized() throws Exception {

        mockMvc.perform(post("/products/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @Rollback
    void givenValidCredentials_whenGetProducts_thenReturnCategories() throws Exception {

        ProductRequest productRequest = new ProductRequest();

        String productJson = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(post("/products/search")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Products Found"));
    }


    @Test
    @Transactional
    @Rollback
    void givenTokenAndInvalidRequest_whenCreateProduct_thenReturnValidationError() throws Exception {

        CreateProductRequest productRequest = new CreateProductRequest();
        productRequest.setDescription(null);
        productRequest.setName("Product 1");
        productRequest.setPrice(null);
        productRequest.setQuantity(null);

        String productJson = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.quantity").value("quantity cannot be null"))
                .andExpect(jsonPath("$.errors.description").value("description cannot be blank"))
                .andExpect(jsonPath("$.errors.price").value("price cannot be null"))
                .andExpect(jsonPath("$.errors.category_ids").value("You need to add categories"));
    }

    @Test
    @Transactional
    @Rollback
    void givenTokenAndValidRequest_whenCreateProduct_thenReturnCreatedProduct() throws Exception {

        CategoryRequest categoryRequest = new CategoryRequest();

        String categoryJson = objectMapper.writeValueAsString(categoryRequest);

        MvcResult categoryResult = mockMvc.perform(post("/category/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson)
                        .header("Authorization", "Bearer " + token))
                .andReturn();

        JsonNode rootNode = objectMapper.readTree(categoryResult.getResponse().getContentAsString());
        JsonNode dataNode = rootNode.path("data");

        List<CategoryResponse> response = objectMapper.readValue(
                dataNode.traverse(),
                new TypeReference<>() {
                }
        );

        List<Long> categoryIds = response.stream().mapToLong(CategoryResponse::getId).boxed().toList();
        log.info("Category Result {}", response);

        CreateProductRequest productRequest = new CreateProductRequest();
        productRequest.setDescription("Description 1");
        productRequest.setName("Product 1");
        productRequest.setPrice(BigDecimal.valueOf(10.95));
        productRequest.setQuantity(5);
        productRequest.setCategoryIds(categoryIds);

        String productJson = objectMapper.writeValueAsString(productRequest);

        MvcResult productCreated = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(productCreated.getResponse().getContentAsString());
        JsonNode dataX = jsonNode.path("data");

        CreateProductResponse productResponse = objectMapper.readValue(dataX.traverse(), CreateProductResponse.class);

        File img = new File("src/test/resources/foto1.jpg");
        MockMultipartFile file = new MockMultipartFile("images", img.getName(), MediaType.IMAGE_JPEG_VALUE, new FileInputStream(img));

        mockMvc.perform(multipart("/products/{id}/images", productResponse.getId())
                        .file(file)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Images uploaded successfully"));
    }

}
