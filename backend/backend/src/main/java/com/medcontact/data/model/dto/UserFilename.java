package com.medcontact.data.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class UserFilename {
	private long userId;
	private String filename;
}
