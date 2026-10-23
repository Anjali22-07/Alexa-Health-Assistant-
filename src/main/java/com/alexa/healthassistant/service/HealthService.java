package com.alexa.healthassistant.service;

import java.time.Instant;
import java.util.List;
import com.alexa.healthassistant.dto.HealthRecordDto;
import com.alexa.healthassistant.entity.Source;


public interface HealthService {

    HealthRecordDto save(HealthRecordDto healthDto, Source source);
    // HealthRecordDto updateRecord(String id, HealthRecordDto healthDto);
    // HealthRecordDto deleteRecord(String id);
    List<HealthRecordDto> getRecords(Instant from , Instant to);


}
