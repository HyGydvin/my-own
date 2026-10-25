package ru.valera.my_own.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.valera.my_own.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
