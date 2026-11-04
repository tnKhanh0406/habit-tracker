package com.prj.habittracker.user.service.impl;

import com.prj.habittracker.common.security.CurrentUserService;
import com.prj.habittracker.common.cloud.CloudinaryService;
import com.prj.habittracker.user.dto.request.UpdateProfileRequest;
import com.prj.habittracker.user.dto.response.UserResponse;
import com.prj.habittracker.user.repository.UserRepository;
import com.prj.habittracker.user.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DateTimeException;
import java.time.ZoneId;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile() {
        return UserResponse.from(currentUserService.getCurrentUser());
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        var user = currentUserService.getCurrentUser();
        String username = request.username().trim();
        if (!username.equalsIgnoreCase(user.getUsername())
                && userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }

        try {
            ZoneId.of(request.timezone());
        } catch (DateTimeException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid timezone");
        }

        String avatar = request.avatarUrl() == null ? "" : request.avatarUrl().trim();
        if (!avatar.isEmpty() && !avatar.startsWith("https://")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Avatar URL must use HTTPS");
        }

        user.setUsername(username);
        user.setAvatarUrl(avatar.isEmpty() ? null : avatar);
        user.setTimezone(request.timezone());
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse uploadAvatar(MultipartFile file) {
        var user = currentUserService.getCurrentUser();
        user.setAvatarUrl(cloudinaryService.uploadAvatar(file, user.getId()));
        return UserResponse.from(user);
    }
}
