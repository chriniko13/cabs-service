package com.booking.data.injector;

import com.booking.common.infra.error.InfrastructureException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.Objects;

public class DeleteBookingClient extends Client implements Scenario {

	private static final Logger LOG = LoggerFactory.getLogger(DeleteBookingClient.class);

	protected DeleteBookingClient(RestTemplate restTemplate, SecureRandom secureRandom, ObjectMapper objectMapper) {
		super(restTemplate, secureRandom, objectMapper);
	}

	@Override public Scenario getScenario() {
		return this;
	}

	@Override public void execute() {

		ResponseEntity<String> s = CreateBookingClient.createBooking(secureRandom, restTemplate).getValue0();
		if (s.getStatusCode() != HttpStatus.CREATED) {
			LOG.error("create booking client operation was not successful");
		}

		// extract id section
		final String recordIdToDelete;
		try {
			JsonNode jsonNode = objectMapper.readTree(Objects.requireNonNull(s.getBody()));
			recordIdToDelete = jsonNode.get("id").asText();
		} catch (JsonProcessingException e) {
			throw new InfrastructureException(e);
		}

		// delete section
		ResponseEntity<String> result = deleteBooking(recordIdToDelete, restTemplate);

		if (result.getStatusCode() != HttpStatus.OK) {
			LOG.error("delete booking client operation was not successful");
		}

	}

	public static ResponseEntity<String> deleteBooking(String recordIdToDelete, RestTemplate restTemplate) {
		return restTemplate.exchange("http://localhost:8081/bookings/" + recordIdToDelete, HttpMethod.DELETE, null, String.class);
	}
}
