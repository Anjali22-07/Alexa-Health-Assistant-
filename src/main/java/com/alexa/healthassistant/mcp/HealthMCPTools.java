package com.alexa.healthassistant.mcp;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import com.alexa.healthassistant.dto.HealthRecordDto;
import com.alexa.healthassistant.entity.RecordType;
import com.alexa.healthassistant.service.HealthService;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class HealthMCPTools {

    private final HealthService healthService;

    @McpTool(name="ping", description = "Check if the health assistant server is running")
    public String ping(){
        return "pong";
    }

      @McpTool(name = "log_health_record",
             description = "Save a health record the user reports, such as a medication taken, "
                         + "a symptom, a supplement or a health event.")
    public String logHealthRecord(
            @McpToolParam(description = "Kind of record. One of: MEDICATION, SYMPTOM, SUPPLEMENT, EVENT")
            String recordType,
            @McpToolParam(description = "What happened, for example 'Paracetamol 500 mg' or 'Headache, severity 6 out of 10'")
            String content,
            @McpToolParam(description = "When it happened, in ISO-8601 UTC like 2026-10-22T15:30:00Z. Leave empty for now.",
                          required = false)
            String recordedAt) {

        try {
            HealthRecordDto dto = new HealthRecordDto();
            dto.setRecordType(RecordType.valueOf(recordType.trim().toUpperCase()));
            dto.setContent(content);
            dto.setRecordedAt(parseInstant(recordedAt));          // CHANGED: uses the helper

            HealthRecordDto saved = healthService.save(dto);
            return "Saved " + saved.getRecordType() + ": " + saved.getContent()
                    + " at " + saved.getRecordedAt();
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return "Could not save. Check the record type and the time format. " + e.getMessage();
        }
    }

    @McpTool(name = "get_records",
             description = "Fetch the user's saved health records in time order, optionally within a date range.")
    public String getRecords(
            @McpToolParam(description = "Start of range, ISO-8601 UTC like 2026-10-21T18:30:00Z", required = false)
            String from,
            @McpToolParam(description = "End of range, ISO-8601 UTC like 2026-10-22T18:30:00Z", required = false)
            String to) {

        try {
            Instant fromInstant = parseInstant(from);             // CHANGED: uses the helper
            Instant toInstant = parseInstant(to);                 // CHANGED: uses the helper

            List<HealthRecordDto> records = healthService.getRecords(fromInstant, toInstant);
            if (records.isEmpty()) {
                return "No records found for that period.";
            }
            StringBuilder sb = new StringBuilder();
            for (HealthRecordDto r : records) {
                sb.append(r.getRecordedAt()).append(" | ")
                  .append(r.getRecordType()).append(" | ")
                  .append(r.getContent()).append("\n");
            }
            return sb.toString();
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return "Could not read records. Check the date format. " + e.getMessage();
        }
    }

    // NEW: accepts "2026-10-22T19:28:15Z", "2026-10-22 19:28:15", "2026-10-22", and offsets like +05:30
    private Instant parseInstant(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String value = text.trim().replace(' ', 'T');

        if (value.length() == 10) {                               // date only
            value = value + "T00:00:00Z";
        } else if (!value.endsWith("Z") && !value.matches(".*[+-]\\d{2}:\\d{2}$")) {
            value = value + "Z";                                  // no zone given, treat as UTC
        }
        return Instant.parse(value);
    }
}
