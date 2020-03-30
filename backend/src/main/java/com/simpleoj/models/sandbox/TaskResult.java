package com.simpleoj.models.sandbox;

import lombok.Data;

@Data
public class TaskResult {
    private Long runtime;
    private Long memory;
    private Integer status;
    private String error;
    private String input;
    private String output;
    private String expected;
}
