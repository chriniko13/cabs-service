package com.booking.common.infra.dto;

import java.util.List;
import java.util.stream.Collectors;

public interface TripWaypointInfoHolder {

	static boolean checkOnlyOneStop(List<? extends TripWaypointInfoHolder> tripWaypoints) {

		List<Boolean> lastStops = tripWaypoints.stream()
				.map(TripWaypointInfoHolder::lastStop)
				.filter(lastStop -> lastStop)
				.collect(Collectors.toList());

		return lastStops.size() == 1;

	}

	boolean lastStop();

}
