package com.booking.common.infra.rabbitmq;

import lombok.Getter;

public enum Queues {

	MESSAGE_AUDIT_QUEUE("message-audit-queue", "message-audit-queue"),

	BOOKING_ADD_QUEUE("booking-add-queue", "booking-add"),
	BOOKING_ADD_WAIT_QUEUE("booking-add-wait-queue", "booking-add-wait-queue"),


	BOOKING_EDIT_QUEUE("booking-edit-queue", "booking-edit"),
	BOOKING_EDIT_WAIT_QUEUE("booking-edit-wait-queue", "booking-edit-wait-queue"),


	BOOKING_DELETE_QUEUE("booking-delete-queue", "booking-delete"),
	BOOKING_DELETE_WAIT_QUEUE("booking-delete-wait-queue", "booking-delete-wait-queue"),


	PARKING_LOT_QUEUE("parking-lot-queue", "parking-lot-queue");

	@Getter
	private final String value;

	@Getter
	private final String routingKey;

	Queues(String value, String routingKey) {
		this.value = value;
		this.routingKey = routingKey;
	}

}
