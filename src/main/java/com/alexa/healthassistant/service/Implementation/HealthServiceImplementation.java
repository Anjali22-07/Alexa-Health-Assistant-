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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRecords'");
    }

}
