package com.user.management.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import static com.user.management.constants.Constants.CONTENT_IS_EMPTY_MESSAGE;

@Data
public class KeepRequest {
    @NotEmpty(message = CONTENT_IS_EMPTY_MESSAGE)
    private String content;
}
