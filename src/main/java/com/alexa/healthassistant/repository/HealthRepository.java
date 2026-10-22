package com.alexa.healthassistant.repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alexa.healthassistant.entity.HealthRecord;

@Repository 
public interface HealthRepository extends  JpaRepository<HealthRecord, UUID>{

    Optional<HealthRecord> findByRecordType(Record record);
    List<HealthRecord> findByRecordedAtBetweenOrderByRecordedAtAsc(Instant from, Instant to);

    List<HealthRecord> findByRecordedAtGreaterThanEqualOrderByRecordedAtAsc(Instant from);

    List<HealthRecord> findByRecordedAtLessThanEqualOrderByRecordedAtAsc(Instant to);

    List<HealthRecord> findAllByOrderByRecordedAtAsc();

}
