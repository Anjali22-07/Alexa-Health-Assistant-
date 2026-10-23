package com.alexa.healthassistant.service.Implementation;

import java.time.Instant;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import com.alexa.healthassistant.service.AssistantService;

@Service 
public class AssistantServiceImplementation implements AssistantService {

    private final ChatClient chatClient;
    private final ToolCallbackProvider mcpTools;

    public AssistantServiceImplementation(ChatClient.Builder builder, ToolCallbackProvider mcpTools) {
       ChatMemory chatMemory = MessageWindowChatMemory.builder()
            .maxMessages(8)
            .build();
  
        this.chatClient = builder
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
            .build();

        this.mcpTools = mcpTools;
    }

    @Override
    public String chat(String userMessage) {
        String system = """
            You are a friendly voice assistant that helps the user keep a personal health log.
            You can save records and look them up using the tools provided.
            Current time: %s (UTC). The user's time zone is Asia/Kolkata (UTC+05:30).
            When you pass times to tools, convert them to UTC in ISO-8601 ending in Z,
            for example 2026-10-22T15:30:00Z.
            "Yesterday" and "today" mean days in the user's time zone, so convert their start and end to UTC.
            If the user gives a time like "at 9 PM" without a date, use today in their time zone.
            If the user gives no time, leave the time empty so the server uses the current time.
            Valid record types are MEDICATION, SYMPTOM, SUPPLEMENT and EVENT.
            If something required is missing, ask one short question instead of guessing.
            Keep replies short and natural, since they are spoken aloud.
            You are not a doctor: never diagnose or give medical advice.
            
            """.formatted(Instant.now());

        return chatClient.prompt()
                .system(system)
                .user(userMessage)
                 .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "demo"))
                .tools(mcpTools)
                .call()
                .content();
    }

    @Override
public String checkIn() {
    String system = """
        You are a caring health-log assistant that checks in on the user without being asked.
        Call get_records for the last 7 days and read them. Then write 2 to 4 short, gentle reminders,
        one sentence each, one per line, with no markdown symbols.
        Allowed: suggest rest or fluids when sleep is short or symptoms are recent; remind them about a
        medicine or supplement THEY logged themselves, using their own words, when a regular dose has no
        entry this week (say it is a reminder, not advice); ask a follow-up about a symptom they logged;
        suggest speaking to a doctor or pharmacist if a symptom has lasted several days or was severe.
        If a record mentions chest pain, trouble breathing or similar, say to contact a doctor or
        emergency services now.
        Forbidden: recommending any new medicine, dose or treatment; telling them to stop or skip a medicine;
        naming a likely condition or giving a diagnosis.
        Always say which record each reminder is based on, for example "your Garmin sleep data" or
        "your fever entry". Source GARMIN means wearable sample data.
        Current time: %s (UTC). The user's time zone is Asia/Kolkata (UTC+05:30).
        When you pass times to tools, use UTC ISO-8601 ending in Z.
        """.formatted(Instant.now());

    return chatClient.prompt()
            .system(system)
            .user("Run my check-in now.")
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "checkin"))
            .tools(mcpTools)       // use the same line you kept in chat()
            .call()
            .content();
}

}
