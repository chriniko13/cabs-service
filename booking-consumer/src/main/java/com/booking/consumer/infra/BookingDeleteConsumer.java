package com.booking.consumer.infra;

import com.booking.common.infra.dto.DeleteBooking;
import com.booking.consumer.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class BookingDeleteConsumer extends EntityMessageConsumer<DeleteBooking> {

	private static final Logger LOG = LoggerFactory.getLogger(BookingDeleteConsumer.class);

	private final BookingService bookingService;

	protected BookingDeleteConsumer(ObjectMapper objectMapper, BookingService bookingService, RabbitTemplate rabbitTemplate) {
		super(objectMapper, DeleteBooking.class, rabbitTemplate);
		this.bookingService = bookingService;
	}

	@Override protected Logger getLogger() {
		return LOG;
	}

	@Override protected Consumer<Message> getMessageConsumer() {
		return message -> {
			byte[] body = message.getBody();
			DeleteBooking deleteBooking = read(body);
			LOG.debug("will try to delete a booking, message: " + deleteBooking);

			bookingService.delete(deleteBooking, message.getMessageProperties().isRedelivered());
		};
	}
}
