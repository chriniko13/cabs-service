package com.booking.consumer.infra;

import com.booking.common.infra.rabbitmq.Queues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class EntityMessageConsumer<M> implements ChannelAwareMessageListener {

	private static final int MAX_RETRIES = 3;

	protected final ObjectMapper objectMapper;
	protected final Class<M> messageType;
	protected final RabbitTemplate rabbitTemplate;

	private final Consumer<Message> messageConsumer = getMessageConsumer();

	protected EntityMessageConsumer(ObjectMapper objectMapper, Class<M> messageType, RabbitTemplate rabbitTemplate) {
		this.objectMapper = objectMapper;
		this.messageType = messageType;
		this.rabbitTemplate = rabbitTemplate;
	}

	protected abstract Logger getLogger();

	@Override public void onMessage(Message message, Channel channel) throws Exception {
		getLogger().debug("message received: {}", message);

		long deliveryTag = message.getMessageProperties().getDeliveryTag();

		try {
			if (hasExceededRetryCount(message)) {
				putIntoParkingLot(message);
			} else {
				messageConsumer.accept(message);
			}

			channel.basicAck(deliveryTag, false);

		} catch (Exception e) {
			getLogger().info("exception occurred during message consumption: " + e.getMessage(), e);
			channel.basicReject(deliveryTag, false /* Note: error handling with wait-queues (ttl) */);
		}

	}

	protected abstract Consumer<Message> getMessageConsumer();

	protected boolean hasExceededRetryCount(Message in) {
		List<Map<String, ?>> xDeathHeader = in.getMessageProperties().getXDeathHeader();
		if (xDeathHeader != null && xDeathHeader.size() >= 1) {
			Long count = (Long) xDeathHeader.get(0).get("count");
			return count >= MAX_RETRIES;
		}

		return false;
	}

	protected void putIntoParkingLot(Message failedMessage) {
		getLogger().info("Retries exCeeded putting into parking lot");
		this.rabbitTemplate.send(Queues.PARKING_LOT_QUEUE.getRoutingKey(), failedMessage);
	}

	protected M read(byte[] data) {
		try {
			return objectMapper.readValue(data, messageType);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
