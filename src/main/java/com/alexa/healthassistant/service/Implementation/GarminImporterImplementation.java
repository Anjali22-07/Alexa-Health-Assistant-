package com.alexa.healthassistant.service.Implementation;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.alexa.healthassistant.dto.HealthRecordDto;
import com.alexa.healthassistant.entity.RecordType;
import com.alexa.healthassistant.entity.Source;
import com.alexa.healthassistant.repository.HealthRepository;
import com.alexa.healthassistant.service.GarminImporterService;
import com.alexa.healthassistant.service.HealthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class GarminImporterImplementation  implements  GarminImporterService{
    private static final ZoneId USER_ZONE = ZoneId.of("Asia/Kolkata");

    private final HealthService healthService;
    private final HealthRepository repository;
    private final ObjectMapper mapper;

    public int importZip(InputStream in) throws IOException {
        int count = 0;
        try (ZipInputStream zis = new ZipInputStream(in)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                if (entry.isDirectory() || !name.endsWith(".json")) continue;

                if (name.contains("sleepData")) {
                    count += importSleep(mapper.readTree(zis.readAllBytes()));
                } else if (name.contains("UDSFile")) {
                    count += importDaily(mapper.readTree(zis.readAllBytes()));
                }
            }
        }
        return count;
    }

    public int importSleep(JsonNode root) {
        int n = 0;
        for (JsonNode day : root) {
            String end = day.path("sleepEndTimestampGMT").textValue();
            if (end == null) continue;                       // skips the {"retro":false} entry

            long deep = day.path("deepSleepSeconds").asLong();
            long light = day.path("lightSleepSeconds").asLong();
            long rem = day.path("remSleepSeconds").asLong();
            long total = deep + light + rem;
            if (total == 0) continue;

            Instant wokeUp = LocalDateTime.parse(end).toInstant(ZoneOffset.UTC);

            StringBuilder sb = new StringBuilder(String.format(
                    "Sleep: %dh %dm (deep %dm, light %dm, REM %dm, awake %dm)",
                    total / 3600, (total % 3600) / 60,
                    deep / 60, light / 60, rem / 60,
                    day.path("awakeSleepSeconds").asLong() / 60));
            JsonNode score = day.path("sleepScores").path("overallScore");
            if (score.isNumber()) sb.append(", score ").append(score.asInt()).append("/100");
            JsonNode spo2 = day.path("spo2SleepSummary").path("lowestSPO2");
            if (spo2.isNumber()) sb.append(", lowest SpO2 ").append(spo2.asInt()).append("%");

            if (saveIfNew(RecordType.SLEEP, sb.toString(), wokeUp)) n++;
        }
        return n;
    }

    public int importDaily(JsonNode root) {
        int n = 0;
        for (JsonNode day : root) {
            String date = day.path("calendarDate").textValue();
            if (date == null) continue;
            Instant dayStart = LocalDate.parse(date).atStartOfDay(USER_ZONE).toInstant();

            if (day.path("totalSteps").isNumber()) {
                String content = String.format("Steps: %d (goal %d), %d kcal, %d m",
                        day.path("totalSteps").asInt(),
                        day.path("dailyStepGoal").asInt(),
                        Math.round(day.path("totalKilocalories").asDouble()),
                        day.path("totalDistanceMeters").asInt());
                if (saveIfNew(RecordType.ACTIVITY, content, dayStart)) n++;
            }

            if (day.path("restingHeartRate").isNumber()) {
                String content = String.format("Heart rate: resting %d bpm (min %d, max %d)",
                        day.path("restingHeartRate").asInt(),
                        day.path("minHeartRate").asInt(),
                        day.path("maxHeartRate").asInt());
                if (saveIfNew(RecordType.HEART_RATE, content, dayStart)) n++;
            }
        }
        return n;
    }

    public boolean saveIfNew(RecordType type, String content, Instant when) {
        if (repository.existsByRecordTypeAndSourceAndRecordedAt(type, Source.GARMIN, when)) {
            return false;
        }
        HealthRecordDto dto = new HealthRecordDto();
        dto.setRecordType(type);
        dto.setContent(content);
        dto.setRecordedAt(when);
        healthService.save(dto, Source.GARMIN);
        return true;
    }

}
