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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/*
	Important Note: as persistence storage for this consumer, would be better to use a write-intensive database, such as: Cassandra.

 */
@Component
public class MessageAuditConsumer extends EntityMessageConsumer<BookingOperationMessage> {

	private static final Logger LOG = LoggerFactory.getLogger(MessageAuditConsumer.class);

	private final AuditService auditService;

	private final int batchSize = 25;
	private final List<Pair<BookingOperationMessage, Map<String, Object>>> entries = new LinkedList<>();

	protected MessageAuditConsumer(ObjectMapper objectMapper, AuditService auditService, RabbitTemplate rabbitTemplate) {
		super(objectMapper, BookingOperationMessage.class, rabbitTemplate);

		this.auditService = auditService;
	}

	@Override protected Logger getLogger() {
		return LOG;
	}

	@Override protected Consumer<Message> getMessageConsumer() {
		return message -> {

			byte[] body = message.getBody();
			BookingOperationMessage bookingOperationMessage = read(body);
			LOG.debug("will try to capture business operation");

			if (entries.size() % batchSize == 0) {
				auditService.save(entries);
				entries.clear();
			}

			entries.add(Pair.with(bookingOperationMessage, message.getMessageProperties().getHeaders()));

			if (entries.size() % batchSize == 0) {
				auditService.save(entries);
				entries.clear();
			}
		};
	}

}
