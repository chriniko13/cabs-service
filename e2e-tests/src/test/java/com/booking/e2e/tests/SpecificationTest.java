package com.booking.e2e.tests;

import com.booking.common.infra.dto.BookingCreated;
import com.booking.common.infra.dto.CreateBooking;
import com.booking.common.infra.dto.EditBooking;
import com.booking.data.injector.CreateBookingClient;
import com.booking.data.injector.DeleteBookingClient;
import com.booking.data.injector.EditBookingClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Awaitility;
import org.javatuples.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpecificationTest {

	private static RestTemplate restTemplate;
	private static SecureRandom secureRandom;
	private static ObjectMapper objectMapper;
	private static Connection connection;
	private static Statement statement;

	@BeforeAll
	static void setup() {
		restTemplate = new RestTemplate();
		secureRandom = new SecureRandom();
		objectMapper = new ObjectMapper();

		try {
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ecabs", "postgres", "admin");
			statement = connection.createStatement();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	static void clear() {
		try {
			statement.close();
			connection.close();
		} catch (SQLException ignored) {
		}
	}

	@Test
	public void spec_works_as_expected() throws Exception {

		// given
		statement.execute("delete from audit_entry");
		statement.execute("delete from trip_waypoint");
		statement.execute("delete from booking");

		// when (create a booking)
		Pair<ResponseEntity<String>, CreateBooking> createdBookingOperation = CreateBookingClient.createBooking(secureRandom, restTemplate);

		// then
		ResponseEntity<String> stringifiedResponseEntity = createdBookingOperation.getValue0();
		String stringifiedResponse = stringifiedResponseEntity.getBody();

		BookingCreated bookingCreated = objectMapper.readValue(stringifiedResponse, BookingCreated.class);
		CreateBooking createBookingRequest = createdBookingOperation.getValue1();

		assertEquals(201, stringifiedResponseEntity.getStatusCodeValue());
		assertNotNull(stringifiedResponse);

		Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
			ResultSet resultSet = statement.executeQuery("select * from booking where booking_id = '" + bookingCreated.getId() + "'");
			assertTrue(resultSet.next());
			assertEquals(resultSet.getString("passenger_name"), createBookingRequest.getPassengerName());
		});

		Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
			ResultSet rs = statement.executeQuery("select * from audit_entry");
			rs.next();
			String className = rs.getString("class_name");
			assertEquals("com.booking.common.infra.dto.CreateBooking", className);
		});

		// when (editing the just created booking)
		Pair<ResponseEntity<String>, EditBooking> editBookingOperation = EditBookingClient.editBooking(bookingCreated.getId(), createBookingRequest, secureRandom, restTemplate);
		ResponseEntity<String> editBookingResponseEntity = editBookingOperation.getValue0();

		// then
		assertEquals(200, editBookingResponseEntity.getStatusCodeValue());
		assertEquals(createBookingRequest.getPickupTime().plusMinutes(10), editBookingOperation.getValue1().getPickupTime());

		Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
			ResultSet rs1 = statement.executeQuery("select count(*) from trip_waypoint where booking_booking_id = '" + bookingCreated.getId() + "'");
			rs1.next();
			assertEquals(
					createBookingRequest.getTripWayPoints().size() + 1,
					rs1.getLong(1)
			);

			ResultSet rs2 = statement.executeQuery("select * from trip_waypoint where booking_booking_id = '" + bookingCreated.getId() + "' and locality = 'locality 9999'");
			assertTrue(rs2.next());
		});

		Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
			ResultSet rs = statement.executeQuery("select * from audit_entry order by created_on desc");
			rs.next();
			String className = rs.getString("class_name");
			assertEquals("com.booking.common.infra.dto.EditBooking", className);
		});

		// when (deleting the just edited booking)
		ResponseEntity<String> deleteBookingOperation = DeleteBookingClient.deleteBooking(bookingCreated.getId(), restTemplate);

		// then
		assertEquals(200, deleteBookingOperation.getStatusCodeValue());
		Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {

			ResultSet rs1 = statement.executeQuery("select count(*) from booking");
			rs1.next();
			long entries = rs1.getLong(1);
			assertEquals(0, entries);

			// trip_waypoint zero entries
			ResultSet rs2 = statement.executeQuery("select count(*) from trip_waypoint");
			rs2.next();
			entries = rs2.getLong(1);
			assertEquals(0, entries);

		});

		Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
			ResultSet rs = statement.executeQuery("select * from audit_entry order by created_on desc");
			rs.next();
			String className = rs.getString("class_name");
			assertEquals("com.booking.common.infra.dto.DeleteBooking", className);
		});

	}

}
