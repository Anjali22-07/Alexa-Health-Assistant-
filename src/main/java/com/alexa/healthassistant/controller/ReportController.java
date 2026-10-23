package com.alexa.healthassistant.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexa.healthassistant.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/V1/reports")
@RequiredArgsConstructor 
public class ReportController {

    private final ReportService reportService;

@GetMapping("/nudges")
public Map<String, String> nudges() {
    return Map.of("nudges", reportService.generateNudges());
}
}
