package com.booking.consumer.infra;

import com.booking.common.infra.dto.EditBooking;
import com.booking.common.infra.rabbitmq.Headers;
import com.booking.consumer.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class BookingEditConsumer extends EntityMessageConsumer<EditBooking> {

	private static final Logger LOG = LoggerFactory.getLogger(BookingEditConsumer.class);

	private final BookingService bookingService;

	protected BookingEditConsumer(ObjectMapper objectMapper, BookingService bookingService, RabbitTemplate rabbitTemplate) {
		super(objectMapper, EditBooking.class, rabbitTemplate);
		this.bookingService = bookingService;
	}

	@Override protected Logger getLogger() {
		return LOG;
	}

	@Override protected Consumer<Message> getMessageConsumer() {
		return message -> {
			byte[] body = message.getBody();

			String id = message.getMessageProperties().getHeader(Headers.BOOKING_ID);
			EditBooking editBooking = read(body);

			LOG.debug("will try to edit a booking, message: " + editBooking);
			bookingService.edit(editBooking, id, message.getMessageProperties().isRedelivered());
		};
	}
}
