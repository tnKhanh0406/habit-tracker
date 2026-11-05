package com.prj.habittracker.party.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JoinPartyRequest(@NotBlank @Size(max = 12) String inviteCode) {}
