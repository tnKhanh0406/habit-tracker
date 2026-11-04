package com.prj.habittracker.common.cloud;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface CloudinaryService {
    String uploadAvatar(MultipartFile file, UUID userId);
}
