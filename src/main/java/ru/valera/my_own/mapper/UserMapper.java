package ru.valera.my_own.mapper;

import org.springframework.stereotype.Component;
import ru.valera.my_own.dto.UserRequest;
import ru.valera.my_own.dto.UserResponse;
import ru.valera.my_own.entity.User;

@Component
public class UserMapper {

    public UserResponse toResponse(User user){
       return new UserResponse(
               user.getId(),
               user.getName(),
               user.getEmail()
       );
    }

    public void updateEntity(User user, UserRequest request){
        user.setName(request.getName());
        user.setEmail(request.getEmail());
    }

}
