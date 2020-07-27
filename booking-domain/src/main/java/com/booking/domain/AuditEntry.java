package com.booking.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(of = { "id" })

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class AuditEntry {

	@Id
	private UUID id;

	@Column(columnDefinition = "TEXT")
	private String headersAsJson;

	@Column(columnDefinition = "TEXT")
	private String payloadAsJson;

	private String className;

	@CreationTimestamp
	private Instant createdOn;

}
