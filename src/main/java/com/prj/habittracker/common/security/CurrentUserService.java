package com.prj.habittracker.common.security;

import com.prj.habittracker.user.entity.UserEntity;
import com.prj.habittracker.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CurrentUserService {

    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;


    public UserEntity getCurrentUser() {
        UUID userId = currentUserProvider.getCurrentUserId();
        return userRepository.findById(userId)
            .filter(user -> user.getStatus() == com.prj.habittracker.common.enums.UserStatus.ACTIVE)
            .orElseThrow(EntityNotFoundException::new);
    }

    public UUID getCurrentUserId() {
        return currentUserProvider.getCurrentUserId();
    }
}
