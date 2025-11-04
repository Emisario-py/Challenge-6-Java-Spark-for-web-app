package org.digitalnao.model.error;

import lombok.Data;

@Data
public class SuccessResponse {
    private final String message;
    private final boolean success = true;
}