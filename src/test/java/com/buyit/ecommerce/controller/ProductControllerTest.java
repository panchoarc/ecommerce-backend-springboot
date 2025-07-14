package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.dto.request.category.CategoryRequest;
import com.buyit.ecommerce.dto.request.product.CreateProductRequest;
import com.buyit.ecommerce.dto.request.product.ProductRequest;
import com.buyit.ecommerce.dto.response.category.CategoryResponse;
import com.buyit.ecommerce.dto.response.product.CreateProductResponse;
import com.buyit.ecommerce.util.UserTestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String adminToken;
    private static String userToken;

    @Autowired
    private UserTestUtils userTestUtils;

    @BeforeAll
    void setUp() throws JsonProcessingException {
        UserRegisterDTO userRegisterDTO = userTestUtils.getUserCredentials();
        UserRegisterDTO adminRegisterDTO = userTestUtils.getAdminCredentials();

        adminToken = userTestUtils.getToken(adminRegisterDTO);
        userToken = userTestUtils.getToken(userRegisterDTO);
    }

    @AfterAll
    void tearDown() {
        userTestUtils.cleanUsers();
    }

    @Test
    void givenNoToken_whenGetProducts_thenUnauthorized() throws Exception {

        mockMvc.perform(post("/products/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void givenValidCredentials_whenGetProducts_thenReturnCategories() throws Exception {

        ProductRequest productRequest = new ProductRequest();

        String productJson = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(post("/products/search")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Products Found"));
    }


    @Test
    void givenTokenAndInvalidRequest_whenCreateProduct_thenReturnValidationError() throws Exception {

        CreateProductRequest productRequest = new CreateProductRequest();
        productRequest.setDescription(null);
        productRequest.setName("Product 1");
        productRequest.setPrice(null);
        productRequest.setQuantity(null);

        String productJson = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
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
    void givenTokenAndValidRequest_whenCreateProduct_thenReturnCreatedProduct() throws Exception {

        CategoryRequest categoryRequest = new CategoryRequest();

        String categoryJson = objectMapper.writeValueAsString(categoryRequest);

        MvcResult categoryResult = mockMvc.perform(post("/category/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson)
                        .header("Authorization", "Bearer " + adminToken))
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
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(productCreated.getResponse().getContentAsString());
        JsonNode dataX = jsonNode.path("data");

        CreateProductResponse productResponse = objectMapper.readValue(dataX.traverse(), CreateProductResponse.class);

        Resource resource = new ClassPathResource("foto1.jpg");
        File img = resource.getFile(); // Cargar archivo correctamente
        MockMultipartFile imageFile = new MockMultipartFile(
                "images[0].image", // Correcto para MultipartFile
                img.getName(),
                MediaType.IMAGE_JPEG_VALUE,
                new FileInputStream(img)
        );

        MockPart isMainPart = new MockPart(
                "images[0].isMain", // Esto es un form field (no archivo)
                "true".getBytes()
        );
        isMainPart.getHeaders().setContentType(MediaType.TEXT_PLAIN); // Importante

        mockMvc.perform(multipart("/products/{id}/images", productResponse.getId())
                        .file(imageFile) // archivo
                        .part(isMainPart) // form field booleano
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Images uploaded successfully"));
    }
}
