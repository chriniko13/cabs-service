package com.booking.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(of = { "bookingId" })

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Booking {

	@Id
	private UUID bookingId;

	private String passengerName;

	private String passengerContactNumber;

	private OffsetDateTime pickupTime;

	private Boolean asap = true;

	/**
	 * Represents the time we envisage it will take a cab to come pick you up.
	 */
	private Integer waitingTime;

	private Integer noOfPassengers;

	private BigDecimal price;

	private Integer rating;

	@CreationTimestamp
	private Instant createdOn;

	@UpdateTimestamp
	private Instant lastModifiedOn;

	@OneToMany(
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			mappedBy = "booking"
	)
	private Set<TripWaypoint> tripWayPoints = new HashSet<>();

	public void addTripWayPoint(TripWaypoint tripWaypoint) {
		tripWayPoints.add(tripWaypoint);
		tripWaypoint.setBooking(this);
	}

	public void addTripWayPoint(List<TripWaypoint> tripWaypoints) {
		for (TripWaypoint tripWaypoint : tripWaypoints) {
			addTripWayPoint(tripWaypoint);
		}
	}

	public void removeAllTripWaypoints() {
		tripWayPoints.forEach(tripWaypoint -> tripWaypoint.setBooking(null));
		tripWayPoints.clear();
	}
}
