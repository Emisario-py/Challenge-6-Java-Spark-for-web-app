package org.digitalnao.model.error;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String error;
    private final boolean success = false;
}