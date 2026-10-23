package com.alexa.healthassistant.service.Implementation;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import com.alexa.healthassistant.dto.HealthRecordDto;
import com.alexa.healthassistant.service.HealthService;
import com.alexa.healthassistant.service.ReportService;

import lombok.RequiredArgsConstructor;

@Service 
public class ReportServiceImplementation implements ReportService {

        private final ChatClient chatClient;
        private final HealthService healthService;

    public ReportServiceImplementation(ChatClient.Builder builder, HealthService healthService) {
        this.chatClient = builder.build();
        this.healthService = healthService;
    }

    @Override
    public String generateNudges() {

          Instant now = Instant.now();
    List<HealthRecordDto> records =
            healthService.getRecords(now.minus(7, java.time.temporal.ChronoUnit.DAYS), now);
    if (records.isEmpty()) {
        return "No recent records yet. Tell me how you're feeling and I'll keep track.";
    }

    ZoneId ist = ZoneId.of("Asia/Kolkata");
    String data = records.stream()
            .map(r -> "%s | %s | %s | source: %s".formatted(
                    r.getRecordedAt().atZone(ist).toLocalDateTime(),
                    r.getRecordType(), r.getContent(), r.getSource()))
            .collect(Collectors.joining("\n"));

    String system = """
        You are a caring health-log assistant. Based only on the records given, write 2 to 4 short,
        gentle reminders for the person, each one sentence, as a plain list with one reminder per line.
        Allowed:
        - Suggest rest, fluids, or taking it easy, when sleep is short or symptoms are recent.
        - Remind them about a medicine or supplement THEY logged themselves, using their own words,
          for example when a regular dose has no entry this week. Say it is a reminder, not advice.
        - Ask a follow-up about a symptom they logged.
        - Suggest speaking to a doctor or pharmacist if a symptom has lasted several days or was severe.
        - If a record mentions chest pain, trouble breathing, or similar, say to contact a doctor
          or emergency services now.
        Forbidden:
        - Recommending any new medicine, dose, or treatment.
        - Telling them to stop or skip a medicine.
        - Naming a likely condition or giving a diagnosis.
        Always say which record a reminder is based on (for example "your Garmin sleep data" or
        "your fever entry"). Source GARMIN means wearable sample data. Times are India time (IST).
        Current time in India: %s.
        Do not use markdown symbols.
        """.formatted(java.time.LocalDateTime.now(ist));

    return chatClient.prompt()
            .system(system)
            .user("Records from the last 7 days:\n" + data)
            .call()
            .content();
  }


}
