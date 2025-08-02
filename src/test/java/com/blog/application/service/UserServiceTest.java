package com.blog.application.service;

import com.blog.application.dto.AuthResponse;
import com.blog.application.dto.LoginRequest;
import com.blog.application.dto.RegisterRequest;
import com.blog.application.entity.User;
import com.blog.application.exception.BadRequestException;
import com.blog.application.exception.ResourceNotFoundException;
import com.blog.application.repository.UserRepository;
import com.blog.application.util.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Register new user - success")
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "password");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");
        when(jwtUtil.generateToken("john")).thenReturn("mockToken");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("john");
        savedUser.setEmail("john@example.com");
        savedUser.setPassword("hashedPassword");
        savedUser.setRole(User.Role.USER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals("john", response.getUsername());
        assertEquals("john@example.com", response.getEmail());
    }

    @Test
    @DisplayName("Register user - username already taken")
    void testRegister_UsernameTaken() {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "password");

        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    @Test
    @DisplayName("Register user - email already taken")
    void testRegister_EmailTaken() {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "password");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    @Test
    @DisplayName("Login user - success")
    void testLogin_Success() {
        LoginRequest request = new LoginRequest("john", "password");

        User user = new User();
        user.setUsername("john");
        user.setPassword("hashedPassword");
        user.setEmail("john@example.com");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("john")).thenReturn("mockToken");

        AuthResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("john", response.getUsername());
        assertEquals("mockToken", response.getToken());
    }

    @Test
    @DisplayName("Login user - user not found")
    void testLogin_UserNotFound() {
        LoginRequest request = new LoginRequest("john", "password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.login(request));
    }

    @Test
    @DisplayName("Find user by username - success")
    void testFindByUsername_Success() {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("john");

        assertEquals("john", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Find user by username - not found")
    void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findByUsername("john"));
    }
}
