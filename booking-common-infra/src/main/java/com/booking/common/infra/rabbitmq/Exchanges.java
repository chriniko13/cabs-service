package com.booking.common.infra.rabbitmq;

import lombok.Getter;

public enum Exchanges {

	MESSAGE_EXCHANGE("message-exchange"),
	BOOKING_EXCHANGE("booking-exchange"),

	PARKING_LOT_EXCHANGE("parking-lot-exchange");

	@Getter
	private final String value;

	Exchanges(String value) {
		this.value = value;
	}
}
