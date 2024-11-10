package com.foodics.task.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProblemDetailDTO {
    private String error;
    private String path;
    private LocalDateTime timestamp;

}
