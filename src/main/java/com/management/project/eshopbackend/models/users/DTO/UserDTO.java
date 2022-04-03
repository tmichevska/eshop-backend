package com.management.project.eshopbackend.models.users.DTO;


import com.management.project.eshopbackend.models.enumerations.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class UserDTO {

    private final Long id;

    @NotNull
    @NotBlank
    private final String email;

    @NotNull
    @NotBlank
    private final String username;

    private final String password;

    @NotNull
    @NotBlank
    private final String name;

    @NotNull
    @NotBlank
    private final String surname;

    private final Role role;

    private final String city;

    private final LocalDateTime dateCreated;
}
