package com.prj.habittracker.common.security;

import java.util.UUID;

public interface CurrentUserProvider {
    UUID getCurrentUserId();
}
