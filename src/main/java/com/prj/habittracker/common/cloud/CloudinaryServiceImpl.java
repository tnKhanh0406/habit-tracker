package com.prj.habittracker.common.cloud;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private static final long MAX_AVATAR_SIZE = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Override
    public String uploadAvatar(MultipartFile file, UUID userId) {
        validate(file);
        if (cloudName == null || cloudName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Cloudinary is not configured");
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "habit-tracker/avatars",
                    "public_id", "user-" + userId,
                    "overwrite", true,
                    "invalidate", true,
                    "resource_type", "image",
                    "transformation", new Transformation<>().width(400).height(400).crop("fill").gravity("auto")
            ));
            Object secureUrl = result.get("secure_url");
            if (secureUrl == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cloudinary did not return an image URL");
            }
            return secureUrl.toString();
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not upload avatar", exception);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Avatar file is required");
        }
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Avatar must be 5 MB or smaller");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Avatar must be a JPEG, PNG, or WebP image");
        }
    }
}
