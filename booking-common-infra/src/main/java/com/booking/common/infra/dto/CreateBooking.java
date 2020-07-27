package com.booking.common.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateBooking extends BookingOperationMessage {

	@NotBlank(message = "Passenger name is mandatory")
	private String passengerName;

	@NotBlank(message = "Passenger contact number is mandatory")
	private String passengerContactNumber;

	@NotNull(message = "Pickup time is mandatory")
	private OffsetDateTime pickupTime;

	private Boolean asap = true;

	@NotNull(message = "Waiting time is mandatory")
	private Integer waitingTime;

	@NotNull(message = "Number of passengers is mandatory")
	@Range(min = 1)
	private Integer noOfPassengers;

	@NotEmpty(message = "At least one trip way point is necessary")
	@BookingOnlyOneTripWaypointLastStopConstraint
	private List<@Valid CreateTripWaypoint> tripWayPoints;

}
