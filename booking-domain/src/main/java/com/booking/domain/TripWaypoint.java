package com.booking.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(of = {"tripWayPointId"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


@Entity
public class TripWaypoint {

	@Id
	private UUID tripWayPointId;

	private Boolean lastStop;

	private String locality;

	private Double lat;
	private Double lng;

	@ManyToOne(fetch = FetchType.LAZY)
	private Booking booking;

	@CreationTimestamp
	private Instant tripWayPointTimestamp;

	@UpdateTimestamp
	private Instant lastModifiedOn;
}
