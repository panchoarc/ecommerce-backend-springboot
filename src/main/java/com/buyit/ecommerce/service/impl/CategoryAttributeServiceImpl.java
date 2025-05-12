package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.categoryAttributes.CreateCategoryAttributesRequest;
import com.buyit.ecommerce.dto.response.categoryAttributes.CategoryAttributeDTO;
import com.buyit.ecommerce.entity.AttributeOption;
import com.buyit.ecommerce.entity.Category;
import com.buyit.ecommerce.entity.CategoryAttribute;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.repository.AttributeOptionRepository;
import com.buyit.ecommerce.repository.CategoryAttributeRepository;
import com.buyit.ecommerce.service.CategoryAttributeService;
import com.buyit.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryAttributeServiceImpl implements CategoryAttributeService {

    private final CategoryService categoryService;
    private final CategoryAttributeRepository categoryAttributeRepository;
    private final AttributeOptionRepository attributeOptionRepository;

    @Override
    public CategoryAttribute getCategoryAttributeById(Long id) {
        return categoryAttributeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Attribute not found"));
    }

    @Override
    @Transactional
    public void createCategoryAttributes(Long categoryId, List<CreateCategoryAttributesRequest> categoryAttributeList) {
        Category category = categoryService.getCategory(categoryId);

        for (CreateCategoryAttributesRequest request : categoryAttributeList) {
            CategoryAttribute categoryAttr = new CategoryAttribute();
            categoryAttr.setName(request.getName());
            categoryAttr.setInputType(request.getType());
            categoryAttr.setCategory(category);
            categoryAttr.setRequired(request.isRequired());

            // Guarda primero el atributo
            CategoryAttribute savedAttribute = categoryAttributeRepository.save(categoryAttr);

            // Si hay opciones, guárdalas asociadas al atributo
            if (request.getOptions() != null && !request.getOptions().isEmpty()) {
                List<AttributeOption> options = request.getOptions().stream()
                        .map(optValue -> {
                            AttributeOption option = new AttributeOption();
                            option.setValue(optValue);
                            option.setAttribute(savedAttribute);
                            return option;
                        })
                        .toList();

                attributeOptionRepository.saveAll(options);
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryAttributeDTO> getCategoryAttributes(Long categoryId) {
        List<CategoryAttribute> attributes = categoryAttributeRepository.findByCategory_CategoryId(categoryId);

        return attributes.stream().map(attribute -> {
            CategoryAttributeDTO dto = new CategoryAttributeDTO();
            dto.setId(attribute.getId());
            dto.setName(attribute.getName());
            dto.setInputType(attribute.getInputType());
            dto.setRequired(attribute.getRequired());

            // Aquí Hibernate todavía está dentro del contexto de sesión
            if (List.of("select", "checkbox", "radio").contains(attribute.getInputType().toLowerCase())
                    && attribute.getOptions() != null) {
                dto.setOptions(
                        attribute.getOptions().stream()
                                .map(AttributeOption::getValue)
                                .toList()
                );
            }

            return dto;
        }).toList();
    }

    @Override
    @Transactional
    public void updateCategoryAttributes(Long categoryId, List<CreateCategoryAttributesRequest> requestList) {
        Category category = categoryService.getCategory(categoryId);
        Map<Long, CategoryAttribute> existingById = categoryAttributeRepository.findByCategory_CategoryId(categoryId)
                .stream().collect(Collectors.toMap(CategoryAttribute::getId, Function.identity()));

        List<CategoryAttribute> attributesToSave = new ArrayList<>();

        for (CreateCategoryAttributesRequest req : requestList) {
            CategoryAttribute attribute = (req.getId() != null && existingById.containsKey(req.getId()))
                    ? existingById.remove(req.getId())
                    : new CategoryAttribute();

            attribute.setCategory(category);
            attribute.setName(req.getName());
            attribute.setInputType(req.getType());
            attribute.setRequired(req.isRequired());
            if (attribute.getOptions() == null) {
                attribute.setOptions(new ArrayList<>());
            } else {
                attribute.getOptions().clear();
            }

            if (req.getOptions() != null && !req.getOptions().isEmpty()
                    && List.of("select", "checkbox", "radio").contains(req.getType().toLowerCase())) {
                req.getOptions().forEach(value -> {
                    AttributeOption option = new AttributeOption();
                    option.setValue(value);
                    option.setAttribute(attribute);
                    attribute.getOptions().add(option);
                });
            }

            attributesToSave.add(attribute);
        }

        categoryAttributeRepository.deleteAll(existingById.values());

        categoryAttributeRepository.saveAll(attributesToSave);
    }
}

