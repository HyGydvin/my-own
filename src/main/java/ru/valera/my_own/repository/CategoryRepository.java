package ru.valera.my_own.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.valera.my_own.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
