package ru.valera.my_own.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "categories")
@Setter
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "category")
    private List<Task> tasks;

    public Category() {
    }

    public Category(String title) {
        this.title = title;
    }

}
