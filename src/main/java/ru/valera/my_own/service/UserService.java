package ru.valera.my_own.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public UserResponse create(UserRequest request) {
        var user = new User(request.getName(), request.getEmail());
        var savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User " + id + " not found"));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public User getEntityById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User " + id + " not found"));

        userMapper.updateEntity(user, request);

        var updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void delete(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User " + id + " not found"));
        userRepository.delete(user);
    }


}
