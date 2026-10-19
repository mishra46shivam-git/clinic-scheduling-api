package com.clinic.scheduling_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthenticationResponse(
        @Schema(description = "JWT Access Token")
        String token
) {}
