package com.alexa.healthassistant.config;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alexa.healthassistant.dto.HealthRecordDto;
import com.alexa.healthassistant.service.AssistantService;
import com.alexa.healthassistant.service.HealthService;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class ProactiveCheckInScheduler {

    public record Nudge(String text, Instant createdAt) {}

    private final AssistantService assistant;
    private final HealthService healthService;

    private final AtomicReference<Nudge> latest = new AtomicReference<>();
    private String lastSignature = "";

    public Nudge latest() {
        return latest.get();
    }

    @Scheduled(fixedDelayString = "${app.checkin.interval-ms:900000}",
               initialDelayString = "${app.checkin.initial-delay-ms:60000}")
    public void run() {
        Instant now = Instant.now();
        List<HealthRecordDto> recent =
                healthService.getRecords(now.minus(7, ChronoUnit.DAYS), now);
        if (recent.isEmpty()) return;

        // Only check in when something new was logged, which also saves free-tier quota.
        HealthRecordDto newest = recent.get(recent.size() - 1);
        String signature = recent.size() + ":" + newest.getRecordedAt();
        if (signature.equals(lastSignature)) return;
        lastSignature = signature;

        try {
            latest.set(new Nudge(assistant.checkIn(), now));
        } catch (Exception e) {
            System.out.println("Proactive check-in failed: " + e.getMessage());
        }
    }
}
