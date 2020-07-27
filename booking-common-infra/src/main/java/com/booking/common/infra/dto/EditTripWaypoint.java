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
public class EditTripWaypoint implements TripWaypointInfoHolder {

	/**
	 * If id is null, create a new record, otherwise update.
	 */
	private String id;

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
