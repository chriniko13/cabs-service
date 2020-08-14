package com.booking.producer.service;

import com.booking.common.infra.dto.CreateBooking;
import com.booking.common.infra.dto.DeleteBooking;
import com.booking.common.infra.dto.EditBooking;
import com.booking.common.infra.error.InfrastructureException;
import com.booking.common.infra.rabbitmq.Exchanges;
import com.booking.common.infra.rabbitmq.Headers;
import com.booking.common.infra.rabbitmq.Queues;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BookingsService {

	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;

	public BookingsService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
		this.rabbitTemplate = rabbitTemplate;
		this.objectMapper = objectMapper;
	}

	public UUID addBooking(CreateBooking cmd) {

		UUID id = UUID.randomUUID();
		byte[] data = getBytes(cmd);

		rabbitTemplate.convertAndSend(
				Exchanges.MESSAGE_EXCHANGE.getValue(),
				Queues.BOOKING_ADD_QUEUE.getRoutingKey(),
				data,
				new MessagePostProcessor() {

					@Override public Message postProcessMessage(Message message) throws AmqpException {
						message.getMessageProperties().getHeaders().put(Headers.BOOKING_ID, id.toString());
						return message;
					}
				}
		);

		return id;
	}

	public void deleteBooking(DeleteBooking cmd) {
		byte[] data = getBytes(cmd);

		rabbitTemplate.convertAndSend(
				Exchanges.MESSAGE_EXCHANGE.getValue(),
				Queues.BOOKING_DELETE_QUEUE.getRoutingKey(),
				data
		);
	}

	public void editBooking(EditBooking cmd, String bookingId) {

		byte[] data = getBytes(cmd);

		rabbitTemplate.convertAndSend(
				Exchanges.MESSAGE_EXCHANGE.getValue(),
				Queues.BOOKING_EDIT_QUEUE.getRoutingKey(),
				data,
				new MessagePostProcessor() {

					@Override public Message postProcessMessage(Message message) throws AmqpException {
						message.getMessageProperties().getHeaders().put(Headers.BOOKING_ID, bookingId);
						return message;
					}
				}
		);
	}

	private byte[] getBytes(Object input) {
		try {
			return objectMapper.writeValueAsBytes(input);
		} catch (JsonProcessingException e) {
			throw new InfrastructureException(e);
		}
	}
}
