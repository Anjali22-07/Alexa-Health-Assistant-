package com.alexa.healthassistant.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alexa.healthassistant.entity.HealthRecord;

@Repository 
public interface HealthRepository extends  JpaRepository<HealthRecord, UUID>{

    Optional<HealthRecord> findByRecordeType(Record record);
     List<HealthRecord> findByTimestampBetweenOrderByTimestampAsc(
            LocalDateTime from, LocalDateTime to);

    List<HealthRecord> findByTimestampGreaterThanEqualOrderByTimestampAsc(LocalDateTime from);

    List<HealthRecord> findByTimestampLessThanEqualOrderByTimestampAsc(LocalDateTime to);

    List<HealthRecord> findAllByOrderByTimestampAsc();

}
