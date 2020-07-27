package com.booking.producer.resource;

import com.booking.common.infra.dto.BookingCreated;
import com.booking.common.infra.dto.CreateBooking;
import com.booking.common.infra.dto.DeleteBooking;
import com.booking.common.infra.dto.EditBooking;
import com.booking.producer.resource.error.InvalidBookingIdException;
import com.booking.producer.service.BookingsService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("bookings")
public class BookingsResource {

	private final BookingsService bookingsService;

	public BookingsResource(BookingsService bookingsService) {
		this.bookingsService = bookingsService;
	}

	@RequestMapping(
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public HttpEntity<BookingCreated> create(@Valid @RequestBody CreateBooking command) {
		UUID id = bookingsService.addBooking(command);
		BookingCreated result = new BookingCreated(id.toString());
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@RequestMapping(
			path = "/{id}",
			method = RequestMethod.DELETE
	)
	public HttpEntity<Void> delete(@PathVariable(name = "id") String id) {

		UUID validatedId = extractBookingId(id);

		DeleteBooking command = new DeleteBooking(validatedId);
		bookingsService.deleteBooking(command);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@RequestMapping(
			path = "/{id}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public HttpEntity<Void> edit(@PathVariable(name = "id") String id, @Valid @RequestBody EditBooking command) {

		extractBookingId(id);

		bookingsService.editBooking(command, id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	// --- infra ---

	private UUID extractBookingId(String id) {
		UUID validatedId;
		try {
			validatedId = UUID.fromString(id);
		} catch (IllegalArgumentException e) {
			throw new InvalidBookingIdException();
		}
		return validatedId;
	}

}
