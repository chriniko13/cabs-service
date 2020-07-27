package com.booking.consumer.infra;

import com.booking.common.infra.dto.CreateBooking;
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
public class BookingAddConsumer extends EntityMessageConsumer<CreateBooking> {

	private static final Logger LOG = LoggerFactory.getLogger(BookingAddConsumer.class);

	private final BookingService bookingService;

	public BookingAddConsumer(ObjectMapper objectMapper, BookingService bookingService, RabbitTemplate rabbitTemplate) {
		super(objectMapper, CreateBooking.class, rabbitTemplate);
		this.bookingService = bookingService;
	}

	@Override protected Logger getLogger() {
		return LOG;
	}

	@Override protected Consumer<Message> getMessageConsumer() {
		return message -> {
			byte[] body = message.getBody();

			String id = message.getMessageProperties().getHeader(Headers.BOOKING_ID);
			CreateBooking createBooking = read(body);

			LOG.debug("will try to create a booking, message: " + createBooking);
			bookingService.save(createBooking, id, message.getMessageProperties().isRedelivered());
		};
	}

}
