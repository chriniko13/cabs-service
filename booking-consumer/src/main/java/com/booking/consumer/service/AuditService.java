package com.booking.consumer.service;

import com.booking.common.infra.dto.BookingOperationMessage;
import com.booking.common.infra.dto.CreateBooking;
import com.booking.common.infra.dto.DeleteBooking;
import com.booking.common.infra.dto.EditBooking;
import com.booking.common.infra.error.InfrastructureException;
import com.booking.domain.AuditEntry;
import com.booking.domain.repository.AuditEntryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javatuples.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuditService {

	private final AuditEntryRepository auditEntryRepository;
	private final TransactionTemplate transactionTemplate;
	private final ObjectMapper objectMapper;

	public AuditService(AuditEntryRepository auditEntryRepository,
			TransactionTemplate transactionTemplate,
			ObjectMapper objectMapper) {
		this.auditEntryRepository = auditEntryRepository;
		this.transactionTemplate = transactionTemplate;
		this.objectMapper = objectMapper;
	}

	public void save(Collection<Pair<BookingOperationMessage, Map<String, Object>>> entries) {

		List<AuditEntry> entriesToSave = entries.stream().map(entry -> getAuditEntry(entry.getValue0(), entry.getValue1())).collect(Collectors.toList());

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override protected void doInTransactionWithoutResult(TransactionStatus status) {
				auditEntryRepository.saveAll(entriesToSave);
			}
		});

	}

	public void save(Pair<BookingOperationMessage, Map<String, Object>> entry) {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override protected void doInTransactionWithoutResult(TransactionStatus status) {
				auditEntryRepository.save(getAuditEntry(entry.getValue0(), entry.getValue1()));
			}
		});
	}

	private AuditEntry getAuditEntry(BookingOperationMessage bookingOperationMessage, Map<String, Object> headers) {
		AuditEntry auditEntry = new AuditEntry();
		auditEntry.setId(UUID.randomUUID());

		auditEntry.setHeadersAsJson(serialize(headers));
		auditEntry.setPayloadAsJson(serialize(bookingOperationMessage));

		if (bookingOperationMessage instanceof CreateBooking) {

			auditEntry.setClassName(CreateBooking.class.getName());

		} else if (bookingOperationMessage instanceof DeleteBooking) {

			auditEntry.setClassName(DeleteBooking.class.getName());

		} else if (bookingOperationMessage instanceof EditBooking) {

			auditEntry.setClassName(EditBooking.class.getName());

		} else {
			throw new InfrastructureException(); // Note: this should not happen.
		}
		return auditEntry;
	}

	private String serialize(Object input) {
		try {
			return objectMapper.writeValueAsString(input);
		} catch (JsonProcessingException e) {
			throw new InfrastructureException(e);
		}
	}

}
