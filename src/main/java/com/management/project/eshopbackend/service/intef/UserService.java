package com.management.project.eshopbackend.service.intef;

import com.management.project.eshopbackend.models.users.DTO.UserDTO;
import com.management.project.eshopbackend.models.users.User;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {

    List<User> getUsers();

    User getUserById(Long userId);

    User getUserByEmail(String email);

    User createUser(UserDTO newUser) throws MessagingException;

    User signUp(UserDTO newUser);

    User updateUser(UserDTO userDTO);

    User changeUserPassword (User User, String newPassword);

    void deleteUserById(Long userId);

    void resetUserPassword(User user);

    void createPostman(User user, String city);
}
