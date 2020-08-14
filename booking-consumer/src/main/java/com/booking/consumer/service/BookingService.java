package com.booking.consumer.service;

import com.booking.common.infra.dto.CreateBooking;
import com.booking.common.infra.dto.DeleteBooking;
import com.booking.common.infra.dto.EditBooking;
import com.booking.consumer.doc.IdempotentOperation;
import com.booking.consumer.error.RecordNotExistsException;
import com.booking.domain.Booking;
import com.booking.domain.TripWaypoint;
import com.booking.domain.repository.BookingRepository;
import com.booking.domain.repository.TripWaypointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.booking.consumer.config.AppConfig.RETRY_MAX_ATTEMPTS;

@Service
public class BookingService {

	private static final Logger LOG = LoggerFactory.getLogger(BookingService.class);

	private final BookingRepository bookingRepository;
	private final TransactionTemplate transactionTemplate;
	private final TripWaypointRepository tripWaypointRepository;
	private final RetryTemplate retryTemplate;

	public BookingService(
			BookingRepository bookingRepository,
			TransactionTemplate transactionTemplate,
			TripWaypointRepository tripWaypointRepository,
			RetryTemplate retryTemplate) {
		this.bookingRepository = bookingRepository;
		this.transactionTemplate = transactionTemplate;
		this.tripWaypointRepository = tripWaypointRepository;
		this.retryTemplate = retryTemplate;
	}

	@IdempotentOperation
	public void save(CreateBooking createBooking, String id, boolean redelivered) {

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {

				// Note: handle redelivery (best effort 1PC)
				boolean proceedWithInsert = true;
				if (redelivered && bookingRepository.findById(UUID.fromString(id)).isPresent()) {
					proceedWithInsert = false;
				}

				if (!proceedWithInsert) {
					return;
				}

				Booking booking = new Booking();
				booking.setBookingId(UUID.fromString(id));
				booking.setPassengerName(createBooking.getPassengerName());
				booking.setPassengerContactNumber(createBooking.getPassengerContactNumber());
				booking.setPickupTime(createBooking.getPickupTime());
				booking.setAsap(createBooking.getAsap());
				booking.setWaitingTime(createBooking.getWaitingTime());
				booking.setNoOfPassengers(createBooking.getNoOfPassengers());

				final Booking managedBooking = bookingRepository.save(booking);

				List<TripWaypoint> tripWaypoints = getTripWaypoints(createBooking);

				managedBooking.addTripWayPoint(tripWaypoints);
			}
		});

	}

	@IdempotentOperation
	public void delete(DeleteBooking deleteBooking, boolean redelivered) {

		retryTemplate.execute((RetryCallback<Void, RecordNotExistsException>) context -> {

			try {

				transactionTemplate.execute(new TransactionCallbackWithoutResult() {

					@Override protected void doInTransactionWithoutResult(TransactionStatus status) {
						final UUID id = deleteBooking.getId();
						Booking booking = bookingRepository.findById(id).orElseThrow(() -> new RecordNotExistsException(id.toString(), Booking.class.getName()));
						bookingRepository.delete(booking);
					}
				});
			} catch (RecordNotExistsException e) {
				LOG.debug(" >>> " + context.getRetryCount() + " --- " + context.isExhaustedOnly());

				if (context.getRetryCount() == RETRY_MAX_ATTEMPTS - 1 && redelivered) {
					/*
						Note: if the message has been redelivered, and we exhausted retries,
						it means that we have already deleted that record (best effort 1PC approach).

						The probability of this to happen is very small, but we should log it and save it somewhere in the future.
					*/
					LOG.warn("delete booking operation was unsuccessful: " + deleteBooking);
				} else {
					throw e;
				}
			}

			return null;
		});

	}

	@IdempotentOperation
	public void edit(EditBooking editBooking, String id, boolean redelivered) {

		retryTemplate.execute((RetryCallback<Object, RecordNotExistsException>) context -> {

			try {
				transactionTemplate.execute(new TransactionCallbackWithoutResult() {

					@Override protected void doInTransactionWithoutResult(TransactionStatus status) {

						UUID uuid = UUID.fromString(id);

						Booking booking = bookingRepository.findById(uuid)
								.orElseThrow(() -> new RecordNotExistsException(id, Booking.class.getName()));

						booking.setPickupTime(editBooking.getPickupTime());
						booking.setAsap(editBooking.getAsap());
						booking.setWaitingTime(editBooking.getWaitingTime());
						booking.setNoOfPassengers(editBooking.getNoOfPassengers());
						booking.setPrice(editBooking.getPrice());
						booking.setRating(editBooking.getRating());

						List<TripWaypoint> tripWaypoints = getTripWaypoints(editBooking);

						booking.addTripWayPoint(tripWaypoints);

					}
				});
			} catch (RecordNotExistsException e) {

				LOG.debug(" >>> " + context.getRetryCount() + " " + context.isExhaustedOnly());

				if (context.getRetryCount() == RETRY_MAX_ATTEMPTS - 1 && redelivered) {
					/*
						Note: if the message has been redelivered, and we exhausted retries,
						it means that we might have already deleted the record (hence the reason it is failing - cannot fetch it in order to update it),
						or we are dealing with an infrastructure-database connectivity issue.

						The probability of this to happen is very small, but we should log it and save it somewhere in the future.
					*/
					LOG.warn("edit booking operation was unsuccessful: " + editBooking);
				} else {
					throw e;
				}
			}

			return null;
		});

	}

	private List<TripWaypoint> getTripWaypoints(CreateBooking createBooking) {
		return createBooking.getTripWayPoints().stream().map(rec -> {

			TripWaypoint tripWaypoint = new TripWaypoint();
			tripWaypoint.setTripWayPointId(UUID.randomUUID());
			tripWaypoint.setLastStop(rec.getLastStop());
			tripWaypoint.setLocality(rec.getLocality());
			tripWaypoint.setLat(rec.getLat());
			tripWaypoint.setLng(rec.getLng());

			return tripWaypoint;

		}).collect(Collectors.toList());
	}

	private List<TripWaypoint> getTripWaypoints(EditBooking editBooking) {
		return editBooking.getTripWayPoints().stream().map(rec -> {

			String tripWaypointId = rec.getId();
			if (tripWaypointId == null) {
				// Note: create new.
				final TripWaypoint tripWaypoint = new TripWaypoint();
				tripWaypoint.setTripWayPointId(UUID.randomUUID());
				tripWaypoint.setLastStop(rec.getLastStop());
				tripWaypoint.setLocality(rec.getLocality());
				tripWaypoint.setLat(rec.getLat());
				tripWaypoint.setLng(rec.getLng());
				return tripWaypoint;

			} else {
				// Note: update.
				TripWaypoint tripWaypoint = tripWaypointRepository.findById(UUID.fromString(tripWaypointId))
						.orElseThrow(() -> new RecordNotExistsException(tripWaypointId, TripWaypoint.class.getName()));
				tripWaypoint.setLastStop(rec.getLastStop());
				tripWaypoint.setLocality(rec.getLocality());
				tripWaypoint.setLat(rec.getLat());
				tripWaypoint.setLng(rec.getLng());
				return tripWaypoint;
			}

		}).collect(Collectors.toList());
	}
}
