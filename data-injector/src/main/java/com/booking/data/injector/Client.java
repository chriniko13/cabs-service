package com.booking.data.injector;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;

public abstract class Client {

	protected final RestTemplate restTemplate;
	protected final SecureRandom secureRandom;
	protected final ObjectMapper objectMapper;

	protected Client(RestTemplate restTemplate, SecureRandom secureRandom, ObjectMapper objectMapper) {
		this.restTemplate = restTemplate;
		this.secureRandom = secureRandom;
		this.objectMapper = objectMapper;
	}

	public abstract Scenario getScenario();

}
