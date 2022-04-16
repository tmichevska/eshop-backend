package com.management.project.eshopbackend.service.impl;

import com.management.project.eshopbackend.models.exceptions.EntityNotFoundException;
import com.management.project.eshopbackend.models.users.DTO.UserDTO;
import com.management.project.eshopbackend.models.users.User;
import com.management.project.eshopbackend.repository.PostmanJPARepository;
import com.management.project.eshopbackend.repository.UserJPARepository;
import com.management.project.eshopbackend.service.intef.AuthTokenService;
import com.management.project.eshopbackend.service.intef.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.intef.EmailService;

import javax.mail.MessagingException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserJPARepository userRepository;

    @Value("${wineShop.mail.url}")
    private String url;

    public static final String CREATE_USER_SUBJECT = "User created";
    public static final String CREATE_USER_CONTENT =
            "Dear " + "%s,\n\n"
                    + "Your account has been created. Please click on the link to change your password: \n\n"
                    + "%s/%s,\n\n\n"
                    + "Best regards,\n"
                    + "WineShop mk";

    public static final String RESET_PASSWORD_SUBJECT = "Password reset";
    public static final String RESET_PASSWORD_CONTENT =
            "Dear " + "%s,\n\n"
                    + "A password reset has benn requested. Please click on the link to reset your password: \n\n"
                    + "%s/%s,\n\n\n"
                    + "Best regards,\n"
                    + "WineShop mk";

    @Override
    public List<User> getUsers() {
        List<User> users = userRepository.findAll();

        return users;
    }

    @Override
    public User getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id: %s not found", userId)));

        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with email: %s not found", email)));
        return user;
    }

    @Override
    public User createUser(UserDTO newUser) throws MessagingException {
        return null;
    }

    @Override
    public User signUp(UserDTO newUser) {
        return null;
    }

    @Override
    public User updateUser(UserDTO userDTO) {
        return null;
    }

    @Override
    public User changeUserPassword(User User, String newPassword) {
        return null;
    }
}
