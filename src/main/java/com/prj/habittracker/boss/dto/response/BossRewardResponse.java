package com.prj.habittracker.boss.dto.response;

import com.prj.habittracker.common.enums.BossRewardType;

public record BossRewardResponse(BossRewardType type, long amount) {}
