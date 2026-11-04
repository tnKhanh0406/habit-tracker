package com.prj.habittracker.user.service;

import com.prj.habittracker.user.dto.request.UpdateProfileRequest;
import com.prj.habittracker.user.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    UserResponse getProfile();
    UserResponse updateProfile(UpdateProfileRequest request);
    UserResponse uploadAvatar(MultipartFile file);
}
