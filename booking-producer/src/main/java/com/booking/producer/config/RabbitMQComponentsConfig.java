package com.booking.producer.config;

import com.booking.common.infra.rabbitmq.Exchanges;
import com.booking.common.infra.rabbitmq.Queues;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableRabbit
public class RabbitMQComponentsConfig {

	// --- queues ---

	@Bean Queue messageAuditQueue() {
		return QueueBuilder.durable(Queues.MESSAGE_AUDIT_QUEUE.getValue()).build();
	}


	@Bean Queue bookingAddQueue() {
		return QueueBuilder.durable(Queues.BOOKING_ADD_QUEUE.getValue())
				.deadLetterExchange(Exchanges.BOOKING_EXCHANGE.getValue())
				.deadLetterRoutingKey(Queues.BOOKING_ADD_WAIT_QUEUE.getValue())
				.build();
	}
	@Bean Queue bookingAddWaitQueue() {
		return QueueBuilder.durable(Queues.BOOKING_ADD_WAIT_QUEUE.getValue())
				.deadLetterExchange(Exchanges.BOOKING_EXCHANGE.getValue())
				.deadLetterRoutingKey(Queues.BOOKING_ADD_QUEUE.getRoutingKey())
				.ttl(10_000)
				.build();
	}



	@Bean Queue bookingEditQueue() {
		return QueueBuilder.durable(Queues.BOOKING_EDIT_QUEUE.getValue())
				.deadLetterExchange(Exchanges.BOOKING_EXCHANGE.getValue())
				.deadLetterRoutingKey(Queues.BOOKING_EDIT_WAIT_QUEUE.getValue())
				.build();
	}
	@Bean Queue bookingEditWaitQueue() {
		return QueueBuilder.durable(Queues.BOOKING_EDIT_WAIT_QUEUE.getValue())
				.deadLetterExchange(Exchanges.BOOKING_EXCHANGE.getValue())
				.deadLetterRoutingKey(Queues.BOOKING_EDIT_QUEUE.getRoutingKey())
				.ttl(10_000)
				.build();
	}



	@Bean Queue bookingDeleteQueue() {
		return QueueBuilder.durable(Queues.BOOKING_DELETE_QUEUE.getValue())
				.deadLetterExchange(Exchanges.BOOKING_EXCHANGE.getValue())
				.deadLetterRoutingKey(Queues.BOOKING_DELETE_WAIT_QUEUE.getValue())
				.build();
	}
	@Bean Queue bookingDeleteWaitQueue() {
		return QueueBuilder.durable(Queues.BOOKING_DELETE_WAIT_QUEUE.getValue())
				.deadLetterExchange(Exchanges.BOOKING_EXCHANGE.getValue())
				.deadLetterRoutingKey(Queues.BOOKING_DELETE_QUEUE.getRoutingKey())
				.ttl(10_000)
				.build();
	}


	@Bean
	Queue parkingLotQueue() {
		return new Queue(Queues.PARKING_LOT_QUEUE.getValue(), true);
	}


	// --- exchanges ---

	@Bean FanoutExchange messageExchange() {
		return new FanoutExchange(Exchanges.MESSAGE_EXCHANGE.getValue(), true, false);
	}

	@Bean TopicExchange bookingExchange() {
		return new TopicExchange(Exchanges.BOOKING_EXCHANGE.getValue(), true, false);
	}


	@Bean
	DirectExchange parkingLotExchange() {
		return new DirectExchange(Exchanges.PARKING_LOT_EXCHANGE.getValue(), true, false);
	}

	// --- bindings ---

	// Diagram: [msg] ---> messageExchange ---> bookingExchange
	@Bean
	Binding messageExchangeToBookingExchange(FanoutExchange messageExchange, TopicExchange bookingExchange) {
		return BindingBuilder
				.bind(bookingExchange)
				.to(messageExchange);
	}

	// Diagram: [msg] ---> messageExchange ---> messageAuditQueue
	@Bean
	Binding messageExchangeToMessageAuditQueue(FanoutExchange messageExchange, Queue messageAuditQueue) {
		return BindingBuilder
				.bind(messageAuditQueue)
				.to(messageExchange);
	}



	// Diagram: [msg] ---> bookingExchange --routing_key:{booking-add}--> bookingAddQueue
	@Bean
	Binding bookingExchangeToBookingAddQueue(TopicExchange bookingExchange, Queue bookingAddQueue) {
		return BindingBuilder.bind(bookingAddQueue)
				.to(bookingExchange)
				.with(Queues.BOOKING_ADD_QUEUE.getRoutingKey());
	}
	@Bean
	Binding bookingExchangeToBookingAddWaitQueue(TopicExchange bookingExchange, Queue bookingAddWaitQueue) {
		return BindingBuilder.bind(bookingAddWaitQueue)
				.to(bookingExchange)
				.with(Queues.BOOKING_ADD_WAIT_QUEUE.getRoutingKey());
	}


	// Diagram: [msg] ---> bookingExchange --routing_key:{booking-edit}--> bookingEditQueue
	@Bean
	Binding bookingExchangeToBookingEditQueue(TopicExchange bookingExchange, Queue bookingEditQueue) {
		return BindingBuilder.bind(bookingEditQueue)
				.to(bookingExchange)
				.with(Queues.BOOKING_EDIT_QUEUE.getRoutingKey());
	}
	@Bean
	Binding bookingExchangeToBookingEditWaitQueue(TopicExchange bookingExchange, Queue bookingEditWaitQueue) {
		return BindingBuilder.bind(bookingEditWaitQueue)
				.to(bookingExchange)
				.with(Queues.BOOKING_EDIT_WAIT_QUEUE.getRoutingKey());
	}



	// Diagram: [msg] ---> bookingExchange --routing_key:{booking-delete}--> bookingDeleteQueue
	@Bean
	Binding bookingExchangeToBookingDeleteQueue(TopicExchange bookingExchange, Queue bookingDeleteQueue) {
		return BindingBuilder.bind(bookingDeleteQueue)
				.to(bookingExchange)
				.with(Queues.BOOKING_DELETE_QUEUE.getRoutingKey());
	}
	@Bean
	Binding bookingExchangeToBookingDeleteWaitQueue(TopicExchange bookingExchange, Queue bookingDeleteWaitQueue) {
		return BindingBuilder.bind(bookingDeleteWaitQueue)
				.to(bookingExchange)
				.with(Queues.BOOKING_DELETE_WAIT_QUEUE.getRoutingKey());
	}



	// Diagram: [msg] ---> parkingLotExchange --routing_key:{parking-lot-queue}--> parkingLotQueue
	@Bean
	Binding parkingLotExchangeToParkingLotQueue(Queue parkingLotQueue, DirectExchange parkingLotExchange) {
		return BindingBuilder.bind(parkingLotQueue)
				.to(parkingLotExchange)
				.with(Queues.PARKING_LOT_QUEUE.getRoutingKey());
	}

}
