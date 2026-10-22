package com.alexa.healthassistant.service.Implementation;

import java.time.Instant;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.alexa.healthassistant.dto.HealthRecordDto;
import com.alexa.healthassistant.entity.HealthRecord;
import com.alexa.healthassistant.repository.HealthRepository;
import com.alexa.healthassistant.service.HealthService;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class HealthServiceImplementation implements HealthService {

    private final HealthRepository healthRepo;
    private final ModelMapper modelMapper;



    @Override
    public HealthRecordDto save(HealthRecordDto healthDto) {

          System.out.println("CONTENT RECEIVED: " + healthDto.getContent());
       HealthRecord healthRecord = new HealthRecord();
       healthRecord.setRecordType(healthDto.getRecordType());
       healthRecord.setContent(healthDto.getContent());
       healthRecord.setRecordedAt(healthDto.getRecordedAt());
       healthRecord.setSource(healthDto.getSource());
       
        HealthRecord saveHealth= healthRepo.save(healthRecord);
        return modelMapper.map(saveHealth, HealthRecordDto.class);
          
    }

    @Override
    public List<HealthRecordDto> getRecords(Instant from, Instant to) {
        if (from != null && to != null && from.isAfter(to)) {
        throw new IllegalArgumentException("'from' must not be after 'to'");
    }

    List<HealthRecord> records;
    if (from != null && to != null) {
        records = healthRepo.findByRecordedAtBetweenOrderByRecordedAtAsc(from, to);
    } else if (from != null) {
        records = healthRepo.findByRecordedAtGreaterThanEqualOrderByRecordedAtAsc(from);
    } else if (to != null) {
        records = healthRepo.findByRecordedAtLessThanEqualOrderByRecordedAtAsc(to);
    } else {
        records = healthRepo.findAllByOrderByRecordedAtAsc();
    }

    return records.stream()
                   .map(record -> modelMapper.map(record, HealthRecordDto.class))
                   .toList();
    }

}
