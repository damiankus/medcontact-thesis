package com.medcontact.data.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class ConnectionData {
	
	@NonNull
	private String endpoint;
	
	@NonNull
	private String username;
	
	@NonNull
	private String domain;
	
	@NonNull
	private String application;

	@NonNull
	private String secret;
	
	@NonNull
	private String roomId;
	
	private final int secure = 1;
	
	public ConnectionData() {
		this.endpoint = "";
		this.username = "";
		this.domain = "";
		this.application = "";
		this.secret = "";
		this.roomId = "";
	}
}
