package com.Samuel.email_microservice.infrastructure.exception.helper;

import jakarta.validation.constraints.NotNull;

public class TestValidationDTO {
    @NotNull
    public String field;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
