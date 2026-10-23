package com.alexa.healthassistant.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alexa.healthassistant.service.GarminImporterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/V1/import")
@RequiredArgsConstructor 
public class ImportController {

     private final GarminImporterService service;

    @PostMapping ("/garmin")
    public Map<String, Integer> importGarmin(@RequestParam ("file") MultipartFile file) throws IOException {
        return Map.of("imported", service.importZip(file.getInputStream()));
    }

}
