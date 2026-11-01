package com.prj.habittracker.rpg.skill.service;

import com.prj.habittracker.rpg.skill.dto.request.CastSkillRequest;
import com.prj.habittracker.rpg.skill.dto.response.SkillCastResponse;
import com.prj.habittracker.rpg.skill.dto.response.SkillResponse;

import java.util.List;
import java.util.UUID;

public interface SkillService {
    List<SkillResponse> getSkills();
    SkillCastResponse cast(UUID skillId, CastSkillRequest request);
}
