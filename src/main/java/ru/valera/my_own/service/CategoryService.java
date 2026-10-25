package ru.valera.my_own.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.valera.my_own.dto.CategoryRequest;
import ru.valera.my_own.dto.CategoryResponse;
import ru.valera.my_own.entity.Category;
import ru.valera.my_own.exception.CategoryNotFoundException;
import ru.valera.my_own.mapper.CategoryMapper;
import ru.valera.my_own.repository.CategoryRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category(request.getTitle());
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("category " + id + " is not found"));
        return categoryMapper.toResponse(category);
    }

    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public Category getEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("category " + id + " is not found"));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("category " + id + " is not found"));

        categoryMapper.updateEntity(category, request);

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("category " + id + " is not found"));

        categoryRepository.delete(category);
    }

}
