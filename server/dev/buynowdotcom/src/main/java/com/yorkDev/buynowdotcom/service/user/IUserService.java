package com.yorkDev.buynowdotcom.service.user;

import com.yorkDev.buynowdotcom.dtos.UserDto;
import com.yorkDev.buynowdotcom.model.User;
import com.yorkDev.buynowdotcom.request.CreateUserRequest;
import com.yorkDev.buynowdotcom.request.UserUpdateRequest;

public interface IUserService {
    User createUser(CreateUserRequest request);
    User updateUser(UserUpdateRequest request, Long userId);
    User getUserById(Long userId);
    void deleteUser(Long userId);
    UserDto convertUserToDto(User user);
}
