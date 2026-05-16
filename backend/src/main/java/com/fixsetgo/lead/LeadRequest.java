package com.fixsetgo.lead;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LeadRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        String company,
        @NotBlank String service,
        @NotBlank @Size(max = 2000) String message
) {
}
