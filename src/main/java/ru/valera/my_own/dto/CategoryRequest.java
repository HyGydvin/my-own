package ru.valera.my_own.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class CategoryRequest {

    @NotBlank(message = "title is required")
    private String title;

    public CategoryRequest (){}

}
