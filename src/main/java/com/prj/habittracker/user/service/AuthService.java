package com.prj.habittracker.user.service;

import com.prj.habittracker.common.enums.SystemRole;
import com.prj.habittracker.common.enums.UserStatus;
import com.prj.habittracker.user.dto.request.LoginRequest;
import com.prj.habittracker.user.dto.request.RegisterRequest;
import com.prj.habittracker.user.dto.response.TokenResponse;
import com.prj.habittracker.user.dto.response.UserResponse;
import com.prj.habittracker.user.entity.UserEntity;
import com.prj.habittracker.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String dummyPasswordHash;

    public AuthService(UserRepository users,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {

        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;

        this.dummyPasswordHash = passwordEncoder.encode(
                UUID.randomUUID().toString());
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        checkPasswordLength(request.password());

        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String username = request.username().toLowerCase(Locale.ROOT);

        if (users.existsByEmailIgnoreCase(email) || users.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email hoặc username đã được sử dụng");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setSystemRole(SystemRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setTimezone("Asia/Ho_Chi_Minh");

        return UserResponse.from(users.saveAndFlush(user));
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        checkPasswordLength(request.password());

        String email = request.email().trim().toLowerCase(Locale.ROOT);
        UserEntity user = users.findByEmailIgnoreCase(email).orElse(null);

        boolean hasPassword = user != null
                && user.getPasswordHash() != null
                && !user.getPasswordHash().isBlank();

        String hash = hasPassword
                ? user.getPasswordHash()
                : dummyPasswordHash;

        boolean matches = passwordEncoder.matches(request.password(), hash);

        if (!hasPassword || !matches || !user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Email hoặc mật khẩu không hợp lệ");
        }

        return jwtService.issue(user);
    }

    @Transactional(readOnly = true)
    public UserResponse me(UUID userId) {
        UserEntity user = users.findById(userId)
                .filter(u -> u.getStatus() == UserStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Tài khoản không khả dụng"));

        return UserResponse.from(user);
    }

    private void checkPasswordLength(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Mật khẩu không được vượt quá 72 byte UTF-8");
        }
    }
}