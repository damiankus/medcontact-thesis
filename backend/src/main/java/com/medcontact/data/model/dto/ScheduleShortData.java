package com.medcontact.data.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleShortData {
    private LocalDateTime start;
    private LocalDateTime end;
}
