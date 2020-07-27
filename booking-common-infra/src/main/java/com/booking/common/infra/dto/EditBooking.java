package com.booking.common.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/*
	Note: when an attribute has the null value, we keep the existing value of the attribute (PATCH).
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EditBooking extends BookingOperationMessage {

	@NotNull(message = "Pickup time is mandatory")
	private OffsetDateTime pickupTime;

	private Boolean asap = true;

	@NotNull(message = "Waiting time is mandatory")
	private Integer waitingTime;

	@NotNull(message = "Number of passengers is mandatory")
	@Range(min = 1)
	private Integer noOfPassengers;

	private BigDecimal price;

	private Integer rating;

	@NotEmpty(message = "At least one trip way point is necessary")
	@BookingOnlyOneTripWaypointLastStopConstraint
	private List<@Valid EditTripWaypoint> tripWayPoints;

}
