package ru.valera.my_own.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.valera.my_own.dto.CategoryRequest;
import ru.valera.my_own.dto.CategoryResponse;
import ru.valera.my_own.service.CategoryService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {
        return categoryService.create(request);
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategory(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping
    public List<CategoryResponse> getCategories() {
        return categoryService.getCategories();
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }

}
