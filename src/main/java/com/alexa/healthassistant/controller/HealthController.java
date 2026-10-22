package com.alexa.healthassistant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexa.healthassistant.dto.HealthRecordDto;
import com.alexa.healthassistant.service.HealthService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/V1/healthRecords")
@RequiredArgsConstructor 
public class HealthController {

    private final HealthService healthService;

   @PostMapping()
    public ResponseEntity<HealthRecordDto> createRecord(@RequestBody  HealthRecordDto healthRecordDto){
       return ResponseEntity.status(201).body(healthService.save(healthRecordDto));
    }

}
