package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.config.TestContainersConfig;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.dto.request.category.CategoryRequest;
import com.buyit.ecommerce.dto.request.category.CreateCategoryRequest;
import com.buyit.ecommerce.dto.request.category.UpdateCategoryRequest;
import com.buyit.ecommerce.dto.response.category.CreateCategoryResponse;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.repository.UsersRepository;
import com.buyit.ecommerce.service.AuthService;
import com.buyit.ecommerce.service.KeycloakService;
import com.buyit.ecommerce.util.TokenExtractor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Slf4j
class CategoryControllerTest extends TestContainersConfig {

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
    void givenNoToken_whenGetCategories_thenUnauthorized() throws Exception {

        mockMvc.perform(post("/category/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @Rollback
    void givenValidCredentials_whenGetCategories_thenReturnCategories() throws Exception {

        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setCategoryName("");
        categoryRequest.setIsActive(null);

        String categoryJson = objectMapper.writeValueAsString(categoryRequest);

        mockMvc.perform(post("/category/search")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Categories Found"));
    }

    @Test
    @Transactional
    @Rollback
    void givenNoToken_whenGetOneCategory_thenUnauthorized() throws Exception {

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setCategoryName("CATEGORY 1");
        categoryRequest.setDescription("Description 1");

        CreateCategoryResponse createdCategory = createCategory(categoryRequest);

        mockMvc.perform(get("/category/{id}", createdCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @Rollback
    void givenValidCredentials_whenGetOneCategory_thenReturnCalledCategory() throws Exception {

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setCategoryName("CATEGORY 1");
        categoryRequest.setDescription("Description 1");

        CreateCategoryResponse createdCategory = createCategory(categoryRequest);

        mockMvc.perform(get("/category/{id}", createdCategory.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category Found"))
                .andExpect(jsonPath("$.data.description").value(createdCategory.getDescription()))
                .andExpect(jsonPath("$.data.name").value(createdCategory.getName()))
                .andExpect(jsonPath("$.data.isActive").value(createdCategory.getIsActive()));
    }


    @Test
    @Transactional
    @Rollback
    void givenInvalidId_whenGetOneCategory_thenReturnNotFound() throws Exception {

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setCategoryName("CATEGORY 1");
        categoryRequest.setDescription("Description 1");

        CreateCategoryResponse createdCategory = createCategory(categoryRequest);

        mockMvc.perform(get("/category/{id}", createdCategory.getId() + 1)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found."))
                .andExpect(jsonPath("$.errors.message").value(String.format("No category found with ID: %d", createdCategory.getId() + 1)));
    }

    @Test
    @Transactional
    @Rollback
    void givenNormalUser_whenCreateCategory_thenReturnForbidden() throws Exception {

        UserRegisterDTO normalUserDTO = new UserRegisterDTO();
        normalUserDTO.setFirstName("Test");
        normalUserDTO.setLastName("User");
        normalUserDTO.setRole("user");
        normalUserDTO.setEmail("usertest@example.com");
        normalUserDTO.setUserName("usertest");
        normalUserDTO.setPassword("SecurePass123!");

        authService.createUser(normalUserDTO);
        String normalUserToken = tokenExtractor.extractTokenFromUser(normalUserDTO.getUserName(), normalUserDTO.getPassword());

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setCategoryName("CATEGORY 1");
        categoryRequest.setDescription("Description 1");

        String jsonCreateCategory = objectMapper.writeValueAsString(categoryRequest);

        mockMvc.perform(post("/category")
                        .header("Authorization", "Bearer " + normalUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCreateCategory))
                .andExpect(status().isForbidden());
    }


    @Test
    @Transactional
    @Rollback
    void givenAdminAndInvalidData_whenCreateCategory_thenReturnBadRequest() throws Exception {

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setCategoryName(null);
        categoryRequest.setDescription(null);

        String jsonCreateCategory = objectMapper.writeValueAsString(categoryRequest);

        mockMvc.perform(post("/category")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCreateCategory))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.name").value("name cannot be blank"))
                .andExpect(jsonPath("$.errors.description").value("description cannot be blank"))
                .andDo(print());
    }

    @Test
    @Transactional
    @Rollback
    void givenAdminUser_whenCreateCategory_thenReturnCreated() throws Exception {

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setCategoryName("CATEGORY 1");
        categoryRequest.setDescription("Description 1");

        String jsonCreateCategory = objectMapper.writeValueAsString(categoryRequest);

        mockMvc.perform(post("/category")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCreateCategory))
                .andExpect(status().isCreated());
    }


    @Test
    @Transactional
    @Rollback
    void givenUserAndValidParams_whenUpdateCategory_thenReturnForbidden() throws Exception {

        UserRegisterDTO normalUserDTO = new UserRegisterDTO();
        normalUserDTO.setFirstName("Test");
        normalUserDTO.setLastName("User");
        normalUserDTO.setUserName("usertest");
        normalUserDTO.setEmail("usertest@example.com");
        normalUserDTO.setPassword("SecurePass123!");

        authService.createUser(normalUserDTO);
        String userToken = tokenExtractor.extractTokenFromUser(normalUserDTO.getUserName(), normalUserDTO.getPassword());

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setCategoryName("CATEGORY 1");
        categoryRequest.setDescription("Description 1");

        CreateCategoryResponse createdResponse = createCategory(categoryRequest);

        UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
        updateCategoryRequest.setCategoryName("CATEGORY 2");
        updateCategoryRequest.setDescription("Description 2");
        updateCategoryRequest.setIsActive(true);
        String jsonUpdateCategory = objectMapper.writeValueAsString(updateCategoryRequest);

        mockMvc.perform(put("/category/{id}", createdResponse.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdateCategory))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @Transactional
    @Rollback
    void givenAdminAndInvalidParams_whenUpdateCategory_thenReturnBadRequest() throws Exception {

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setCategoryName("CATEGORY 1");
        categoryRequest.setDescription("Description 1");

        CreateCategoryResponse createdResponse = createCategory(categoryRequest);

        UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
        updateCategoryRequest.setCategoryName("CATEGORY 2");
        updateCategoryRequest.setDescription("Description 2");
        String jsonUpdateCategory = objectMapper.writeValueAsString(updateCategoryRequest);

        mockMvc.perform(put("/category/{id}", createdResponse.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdateCategory))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.is_active").value("is_active needs to be a boolean value"));
    }

    @Test
    @Transactional
    @Rollback
    void givenAdminAndValidParams_whenUpdateCategory_thenReturnOK() throws Exception {

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setCategoryName("CATEGORY 1");
        categoryRequest.setDescription("Description 1");

        CreateCategoryResponse createdResponse = createCategory(categoryRequest);

        UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
        updateCategoryRequest.setCategoryName("CATEGORY 2");
        updateCategoryRequest.setDescription("Description 2");
        updateCategoryRequest.setIsActive(true);
        String jsonUpdateCategory = objectMapper.writeValueAsString(updateCategoryRequest);

        mockMvc.perform(put("/category/{id}", createdResponse.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdateCategory))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category updated successfully"))
                .andDo(print());
    }


    private @NotNull CreateCategoryResponse createCategory(CreateCategoryRequest request) throws Exception {
        CreateCategoryRequest createdCategory = new CreateCategoryRequest();
        createdCategory.setCategoryName(request.getCategoryName());
        createdCategory.setDescription(request.getDescription());

        String categoryJson = objectMapper.writeValueAsString(createdCategory);

        MvcResult createdResult = mockMvc.perform(post("/category")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(createdResult.getResponse().getContentAsString());
        return objectMapper.treeToValue(root.get("data"), CreateCategoryResponse.class);


    }

}
