package com.booking.producer.resource.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter class ValidationErrorMessages {

	private List<ValidationErrorMessage> messages;

}
