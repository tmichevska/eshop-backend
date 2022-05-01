package com.management.project.eshopbackend.web.rest;

import com.management.project.eshopbackend.models.users.AuthToken;
import com.management.project.eshopbackend.models.users.DTO.JwtResponseDTO;
import com.management.project.eshopbackend.models.users.DTO.SignInDTO;
import com.management.project.eshopbackend.models.users.DTO.UserDTO;
import com.management.project.eshopbackend.models.users.User;
import com.management.project.eshopbackend.security.JwtUtils;
import com.management.project.eshopbackend.service.impl.UserDetailsImpl;
import com.management.project.eshopbackend.service.intef.AuthTokenService;
import com.management.project.eshopbackend.service.intef.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthTokenService authTokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInDTO signInDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInDto.getUsername(), signInDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String role = roles.get(0);

        return ResponseEntity.ok(new JwtResponseDTO(userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getName(),
                userDetails.getSurname(),
                userDetails.getEmail(),
                jwt,
                role));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserDTO userDTO) {
        User user;
        try {
            user = userService.signUp(userDTO);
        } catch (RuntimeException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/authenticateToken/{token}")
    public ResponseEntity<?> authenticateToken(@PathVariable("token") String token) {
        boolean validateToken = authTokenService.validateToken(token);

        if (validateToken) {
            AuthToken authToken = authTokenService.findByToken(token);
            Long userId = authToken.getUser().getId();

            return new ResponseEntity<>(userId, HttpStatus.OK);
        }

        return new ResponseEntity<>("Invalid token", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
