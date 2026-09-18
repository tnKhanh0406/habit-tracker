package com.prj.habittracker.user.dto.response;

import com.prj.habittracker.common.enums.SystemRole;
import com.prj.habittracker.user.entity.UserEntity;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        String avatarUrl,
        String timezone,
        SystemRole systemRole
) {
    public static UserResponse from(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getTimezone(),
                user.getSystemRole()
        );
    }
}
