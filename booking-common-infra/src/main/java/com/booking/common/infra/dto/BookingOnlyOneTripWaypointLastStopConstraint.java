package com.booking.common.infra.dto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = BookingOnlyOneTripWaypointLastStopValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface BookingOnlyOneTripWaypointLastStopConstraint {

	String message() default "The booking's trip waypoints should have only one trip waypoint marked as last stop.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
