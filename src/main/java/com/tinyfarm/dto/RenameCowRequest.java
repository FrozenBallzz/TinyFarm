package com.tinyfarm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RenameCowRequest(
    @NotBlank @Size(max = 40) String name
) {
}
