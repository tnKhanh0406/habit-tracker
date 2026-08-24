package com.prj.habittracker.common.security;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HardcodedCurrentUserProvider implements CurrentUserProvider {
    private static final UUID CURRENT_USER_ID = UUID.fromString("927b1ae6-62fd-4298-8265-1ded965cb274");

    @Override
    public UUID getCurrentUserId() {
        return CURRENT_USER_ID;
    }
}
