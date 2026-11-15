package ru.valera.my_own.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        var category = new Category(request.getTitle());
        var savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("category " + id + " is not found"));
        return categoryMapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Category getEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("category " + id + " is not found"));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("category " + id + " is not found"));

        categoryMapper.updateEntity(category, request);

        var updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);
    }

    @Transactional
    public void delete(Long id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("category " + id + " is not found"));

        categoryRepository.delete(category);
    }

}
