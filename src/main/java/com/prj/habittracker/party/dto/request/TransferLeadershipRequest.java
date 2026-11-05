package com.prj.habittracker.party.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TransferLeadershipRequest(@NotNull UUID userId) {}
