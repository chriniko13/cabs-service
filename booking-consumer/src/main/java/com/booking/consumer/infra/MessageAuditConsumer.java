package com.booking.consumer.infra;

import com.booking.common.infra.dto.BookingOperationMessage;
import com.booking.consumer.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/*
	Important Note: as persistence storage for this consumer, would be better to use a write-intensive database, such as: Cassandra.

 */
@Component
public class MessageAuditConsumer extends EntityMessageConsumer<BookingOperationMessage> {

	private static final Logger LOG = LoggerFactory.getLogger(MessageAuditConsumer.class);

	private final AuditService auditService;

	private final int batchSize = 25;
	private final ConcurrentLinkedQueue<Pair<BookingOperationMessage, Map<String, Object>>> entries = new ConcurrentLinkedQueue<>();

	protected MessageAuditConsumer(ObjectMapper objectMapper, AuditService auditService, RabbitTemplate rabbitTemplate) {
		super(objectMapper, BookingOperationMessage.class, rabbitTemplate);

		this.auditService = auditService;

		ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleWithFixedDelay(
				() -> {
					Pair<BookingOperationMessage, Map<String, Object>> entry = entries.poll();
					if (entry != null) {
						this.auditService.save(entry);
					}
				}, 20, 5, TimeUnit.MILLISECONDS
		);

		Runtime.getRuntime().addShutdownHook(new Thread(scheduledExecutorService::shutdown));
	}

	@Override protected Logger getLogger() {
		return LOG;
	}

	@Override protected Consumer<Message> getMessageConsumer() {
		return message -> {

			byte[] body = message.getBody();
			BookingOperationMessage bookingOperationMessage = read(body);
			LOG.debug("will try to capture business operation");

			entries.add(Pair.with(bookingOperationMessage, message.getMessageProperties().getHeaders()));
		};
	}

}
