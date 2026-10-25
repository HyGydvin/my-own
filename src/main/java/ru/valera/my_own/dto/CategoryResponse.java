package ru.valera.my_own.dto;

import lombok.Getter;

@Getter
public class CategoryResponse {

    private Long id;
    private String title;

    public CategoryResponse(Long id, String title){
        this.id = id;
        this.title = title;
    }
}
