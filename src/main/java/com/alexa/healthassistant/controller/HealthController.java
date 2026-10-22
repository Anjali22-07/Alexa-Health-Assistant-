package com.alexa.healthassistant.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @GetMapping()
    public  List<HealthRecordDto> getRecords(@RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        return healthService.getRecords(from, to);

}
}
