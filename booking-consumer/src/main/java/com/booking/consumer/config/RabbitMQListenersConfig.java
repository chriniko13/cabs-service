package com.booking.consumer.config;

import com.booking.common.infra.rabbitmq.Queues;
import com.booking.consumer.infra.BookingAddConsumer;
import com.booking.consumer.infra.BookingDeleteConsumer;
import com.booking.consumer.infra.BookingEditConsumer;
import com.booking.consumer.infra.MessageAuditConsumer;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQListenersConfig {

	// --- message listeners ---

	@Bean
	MessageListenerContainer bookingAddConsumerRegistration(ConnectionFactory connectionFactory, BookingAddConsumer consumer) {
		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();

		simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
		simpleMessageListenerContainer.setQueueNames(Queues.BOOKING_ADD_QUEUE.getValue());
		simpleMessageListenerContainer.setMessageListener(consumer);

		simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);

		simpleMessageListenerContainer.setConcurrentConsumers(10);
		simpleMessageListenerContainer.setMaxConcurrentConsumers(21);

		return simpleMessageListenerContainer;
	}

	@Bean
	MessageListenerContainer bookingDeleteConsumerRegistration(ConnectionFactory connectionFactory, BookingDeleteConsumer consumer) {
		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();

		simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
		simpleMessageListenerContainer.setQueueNames(Queues.BOOKING_DELETE_QUEUE.getValue());
		simpleMessageListenerContainer.setMessageListener(consumer);

		simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);

		simpleMessageListenerContainer.setConcurrentConsumers(10);
		simpleMessageListenerContainer.setMaxConcurrentConsumers(21);

		return simpleMessageListenerContainer;
	}

	@Bean
	MessageListenerContainer bookingEditConsumerRegistration(ConnectionFactory connectionFactory, BookingEditConsumer consumer) {
		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();

		simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
		simpleMessageListenerContainer.setQueueNames(Queues.BOOKING_EDIT_QUEUE.getValue());
		simpleMessageListenerContainer.setMessageListener(consumer);

		simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);

		simpleMessageListenerContainer.setConcurrentConsumers(10);
		simpleMessageListenerContainer.setMaxConcurrentConsumers(21);

		return simpleMessageListenerContainer;
	}

	@Bean
	MessageListenerContainer messageAuditConsumerRegistration(ConnectionFactory connectionFactory, MessageAuditConsumer consumer) {
		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();

		simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
		simpleMessageListenerContainer.setQueueNames(Queues.MESSAGE_AUDIT_QUEUE.getValue());
		simpleMessageListenerContainer.setMessageListener(consumer);

		simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		simpleMessageListenerContainer.setConcurrency("1"); // Note: we want only one to maintain order - so help us for debugging purposes.

		return simpleMessageListenerContainer;
	}

}
