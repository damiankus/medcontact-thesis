package com.medcontact.data.model.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class ConnectionData {
	
	@NonNull
	private String iceEndpoint;
	
	@NonNull
	private String ident;
	
	@NonNull
	private String domain;
	
	@NonNull
	private String application;

	@NonNull
	private String secret;
	
	@NonNull
	private String room;
	
	private final int secure = 1;
	
	public ConnectionData() {
		this.iceEndpoint = "";
		this.ident = "";
		this.domain = "";
		this.application = "";
		this.secret = "";
		this.room = "";
	}
}
