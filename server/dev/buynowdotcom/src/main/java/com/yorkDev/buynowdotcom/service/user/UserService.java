package com.yorkDev.buynowdotcom.service.user;

import com.yorkDev.buynowdotcom.dtos.UserDto;
import com.yorkDev.buynowdotcom.model.Role;
import com.yorkDev.buynowdotcom.model.User;
import com.yorkDev.buynowdotcom.repository.UserRepository;
import com.yorkDev.buynowdotcom.request.CreateUserRequest;
import com.yorkDev.buynowdotcom.request.UserUpdateRequest;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(CreateUserRequest request) {
        String email = request.getEmail();
        boolean userAlreadyExists = userRepository.existsByEmail(email);
        if (userAlreadyExists) {
            throw new EntityExistsException("Opps! User with email address of " + email + " already exists!");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(UserUpdateRequest request, Long userId) {
        return userRepository.findById(userId).map(existingUser -> {
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new EntityNotFoundException("User is not found"));
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("User is not found"));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).ifPresentOrElse(userRepository :: delete, () -> {
            throw new EntityNotFoundException("User is not found");
        });
    }

    @Override
    public UserDto convertUserToDto(User user) {
        UserDto dto = modelMapper.map(user, UserDto.class);
        String role = user.getRoles().stream()
                .findFirst()
                .map(Role::getName)
                .orElse(null);
        dto.setRole(role);
        return dto;
    }

    @Override
    public User getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new EntityNotFoundException("Log in required!"));
    }
}
