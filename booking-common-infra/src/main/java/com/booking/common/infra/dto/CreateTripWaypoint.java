package com.booking.common.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateTripWaypoint implements TripWaypointInfoHolder {

	@NotNull(message = "last stop is mandatory")
	private Boolean lastStop;

	@NotNull(message = "locality is mandatory")
	private String locality;

	@NotNull(message = "latitude is mandatory")
	private Double lat;

	@NotNull(message = "longitude is mandatory")
	private Double lng;

	@Override public boolean lastStop() {
		return lastStop;
	}
}
