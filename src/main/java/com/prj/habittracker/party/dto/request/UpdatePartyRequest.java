package com.prj.habittracker.party.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePartyRequest(@NotBlank @Size(max = 80) String name) {}
