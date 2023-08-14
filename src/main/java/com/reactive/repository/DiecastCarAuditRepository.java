package com.reactive.repository;

import com.reactive.entities.DiecastCarAudit;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author u2cc
 */
public interface DiecastCarAuditRepository extends ReactiveCrudRepository<DiecastCarAudit, Long> {
}
