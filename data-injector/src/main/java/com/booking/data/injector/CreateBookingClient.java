package com.booking.data.injector;

import com.booking.common.infra.dto.CreateBooking;
import com.booking.common.infra.dto.CreateTripWaypoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CreateBookingClient extends Client implements Scenario {

	private static final Logger LOG = LoggerFactory.getLogger(CreateBookingClient.class);

	protected CreateBookingClient(RestTemplate restTemplate, SecureRandom secureRandom, ObjectMapper objectMapper) {
		super(restTemplate, secureRandom, objectMapper);
	}

	public static Pair<ResponseEntity<String>, CreateBooking> createBooking(SecureRandom secureRandom, RestTemplate restTemplate) {

		CreateBooking createBooking = new CreateBooking();
		createBooking.setPassengerName("passenger " + UUID.randomUUID());
		createBooking.setPassengerContactNumber("passengerContactNumber " + UUID.randomUUID());
		createBooking.setPickupTime(OffsetDateTime.now().plusMinutes(5));
		createBooking.setWaitingTime(secureRandom.nextInt(30) + 1);
		createBooking.setNoOfPassengers(secureRandom.nextInt(4) + 1);

		int noOfTripWayPoints = secureRandom.nextInt(4) + 1;

		List<CreateTripWaypoint> createTripWaypoints = IntStream.rangeClosed(1, noOfTripWayPoints)
				.boxed()
				.map(idx -> {
					CreateTripWaypoint createTripWaypoint = new CreateTripWaypoint();

					createTripWaypoint.setLocality("locality " + idx);
					createTripWaypoint.setLat(secureRandom.nextDouble());
					createTripWaypoint.setLng(secureRandom.nextDouble());
					createTripWaypoint.setLastStop(false);

					return createTripWaypoint;
				})
				.collect(Collectors.toList());

		// make the last record, always the last stop
		createTripWaypoints.get(createTripWaypoints.size() - 1).setLastStop(true);

		createBooking.setTripWayPoints(createTripWaypoints);

		return Pair.with(
				restTemplate.postForEntity("http://localhost:8081/bookings", createBooking, String.class),
				createBooking
		);
	}

	@Override public Scenario getScenario() {
		return this;
	}

	@Override public void execute() {
		ResponseEntity<String> s = createBooking(secureRandom, restTemplate).getValue0();

		if (s.getStatusCode() != HttpStatus.CREATED) {
			LOG.warn("create booking client operation was not successful");
		}

	}

}
