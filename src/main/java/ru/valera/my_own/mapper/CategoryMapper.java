package ru.valera.my_own.mapper;

import org.springframework.stereotype.Component;
import ru.valera.my_own.dto.CategoryRequest;
import ru.valera.my_own.dto.CategoryResponse;
import ru.valera.my_own.entity.Category;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getTitle()
        );
    }

    public void updateEntity (Category category, CategoryRequest request){
        category.setTitle(request.getTitle());
    }
}
