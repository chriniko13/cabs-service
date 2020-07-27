package com.booking.domain.repository;

import com.booking.domain.AuditEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditEntryRepository extends JpaRepository<AuditEntry, UUID> {

}
