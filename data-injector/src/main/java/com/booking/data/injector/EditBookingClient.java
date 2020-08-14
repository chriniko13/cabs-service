package com.booking.data.injector;

import com.booking.common.infra.dto.CreateBooking;
import com.booking.common.infra.dto.EditBooking;
import com.booking.common.infra.dto.EditTripWaypoint;
import com.booking.common.infra.error.InfrastructureException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Objects;

public class EditBookingClient extends Client implements Scenario {

	private static final Logger LOG = LoggerFactory.getLogger(EditBookingClient.class);

	protected EditBookingClient(RestTemplate restTemplate, SecureRandom secureRandom, ObjectMapper objectMapper) {
		super(restTemplate, secureRandom, objectMapper);
	}

	@Override public Scenario getScenario() {
		return this;
	}

	@Override public void execute() {

		Pair<ResponseEntity<String>, CreateBooking> addOperationResult = CreateBookingClient.createBooking(secureRandom, restTemplate);

		ResponseEntity<String> s = addOperationResult.getValue0();
		if (s.getStatusCode() != HttpStatus.CREATED) {
			LOG.error("create booking client operation was not successful");
		}

		// extract id section
		final String recordIdToUpdate;
		try {
			JsonNode jsonNode = objectMapper.readTree(Objects.requireNonNull(s.getBody()));
			recordIdToUpdate = jsonNode.get("id").asText();
		} catch (JsonProcessingException e) {
			throw new InfrastructureException(e);
		}

		// update section
		CreateBooking createBooking = addOperationResult.getValue1();

		ResponseEntity<String> result = editBooking(recordIdToUpdate, createBooking, secureRandom, restTemplate).getValue0();

		if (result.getStatusCode() != HttpStatus.OK) {
			LOG.error("create booking client operation was not successful");
		}

	}

	public static Pair<ResponseEntity<String>, EditBooking> editBooking(
			String recordIdToUpdate, CreateBooking createBooking,
			SecureRandom secureRandom, RestTemplate restTemplate) {

		EditBooking editBooking = new EditBooking();
		editBooking.setPickupTime(createBooking.getPickupTime().plusMinutes(10));
		editBooking.setAsap(true);
		editBooking.setWaitingTime(createBooking.getWaitingTime() + 10);
		editBooking.setNoOfPassengers(createBooking.getNoOfPassengers() + 5);
		editBooking.setPrice(BigDecimal.valueOf(34.50D));
		editBooking.setRating(10);

		EditTripWaypoint editTripWaypoint = new EditTripWaypoint();
		editTripWaypoint.setId(null); // force creation.
		editTripWaypoint.setLocality("locality " + 9999);
		editTripWaypoint.setLat(secureRandom.nextDouble());
		editTripWaypoint.setLng(secureRandom.nextDouble());
		editTripWaypoint.setLastStop(false);

		editBooking.setTripWayPoints(Collections.singletonList(editTripWaypoint));

		HttpEntity<EditBooking> httpEntity = new HttpEntity<>(editBooking);

		return Pair.with(restTemplate.exchange("http://localhost:8081/bookings/" + recordIdToUpdate, HttpMethod.PUT, httpEntity, String.class), editBooking);
	}
}
