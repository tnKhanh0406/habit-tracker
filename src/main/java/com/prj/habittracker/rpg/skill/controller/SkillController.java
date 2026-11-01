package com.prj.habittracker.rpg.skill.controller;

import com.prj.habittracker.rpg.skill.dto.request.CastSkillRequest;
import com.prj.habittracker.rpg.skill.dto.response.SkillCastResponse;
import com.prj.habittracker.rpg.skill.dto.response.SkillResponse;
import com.prj.habittracker.rpg.skill.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @GetMapping
    public List<SkillResponse> getSkills() {
        return skillService.getSkills();
    }

    @PostMapping("/{skillId}/cast")
    public SkillCastResponse cast(@PathVariable UUID skillId, @RequestBody @Valid CastSkillRequest request) {
        return skillService.cast(skillId, request);
    }
}
