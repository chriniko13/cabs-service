package com.booking.common.infra.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class BookingOnlyOneTripWaypointLastStopValidator
		implements ConstraintValidator<BookingOnlyOneTripWaypointLastStopConstraint, List<? extends TripWaypointInfoHolder>> {

	@Override public boolean isValid(List<? extends TripWaypointInfoHolder> tripWaypoints, ConstraintValidatorContext context) {

		// Note: this should never happen, but we handle it for any case.
		if (tripWaypoints == null || tripWaypoints.isEmpty()) {
			return false;
		}

		// Note: we do not check for updates during submission if we have only one stop - we check during save - we rely on ui validation check.
		if (tripWaypoints.get(0).getClass().equals(EditTripWaypoint.class)) {
			return true;
		}

		return TripWaypointInfoHolder.checkOnlyOneStop(tripWaypoints);
	}
}
