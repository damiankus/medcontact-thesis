package com.medcontact.data.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.medcontact.data.model.BasicUser;

public class BasicUserSerializer extends JsonSerializer<BasicUser> {

	@Override
	public void serialize(BasicUser value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		
		gen.writeStartObject();
		gen.writeStringField("email", value.getEmail());
		gen.writeStringField("firstName", value.getFirstName());
		gen.writeStringField("lastName", value.getLastName());
		gen.writeStringField("role", value.getRole().toString());
		gen.writeEndObject();
	}

}
