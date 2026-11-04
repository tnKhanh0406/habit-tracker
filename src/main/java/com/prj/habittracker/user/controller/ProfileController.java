package com.prj.habittracker.user.controller;

import com.prj.habittracker.user.dto.request.UpdateProfileRequest;
import com.prj.habittracker.user.dto.response.UserResponse;
import com.prj.habittracker.user.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public UserResponse get() {
        return profileService.getProfile();
    }

    @PutMapping
    public UserResponse update(@RequestBody @Valid UpdateProfileRequest request) {
        return profileService.updateProfile(request);
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse uploadAvatar(@RequestPart("file") MultipartFile file) {
        return profileService.uploadAvatar(file);
    }
}
