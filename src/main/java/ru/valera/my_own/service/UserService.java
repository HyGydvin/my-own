package ru.valera.my_own.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.valera.my_own.dto.UserRequest;
import ru.valera.my_own.dto.UserResponse;
import ru.valera.my_own.entity.User;
import ru.valera.my_own.exception.UserNotFoundException;
import ru.valera.my_own.mapper.UserMapper;
import ru.valera.my_own.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse create(UserRequest request) {
        User user = new User(request.getName(), request.getEmail());
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User " + id + " not found"));
        return userMapper.toResponse(user);
    }

    public User getEntityById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User " + id + " not found"));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User " + id + " not found"));

        userMapper.updateEntity(user, request);

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User " + id + " not found"));
        userRepository.delete(user);
    }


}
