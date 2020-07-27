package com.booking.common.infra.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// Note: ADT
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = CreateBooking.class, name = "create-booking"),
		@JsonSubTypes.Type(value = EditBooking.class, name = "edit-booking"),
		@JsonSubTypes.Type(value = DeleteBooking.class, name = "delete-booking"),
})
public abstract class BookingOperationMessage {

}
