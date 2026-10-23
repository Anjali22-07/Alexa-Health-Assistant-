package com.alexa.healthassistant.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexa.healthassistant.config.ProactiveCheckInScheduler;
import com.alexa.healthassistant.service.AssistantService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/V1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final AssistantService assistantService;
    private final ProactiveCheckInScheduler scheduler;

    public record ChatRequest(@NotBlank(message = "message is required") String message) {}

    @PostMapping()
    public Map<String, String> chat(@Valid @RequestBody ChatRequest request) {
        return Map.of("reply", assistantService.chat(request.message()));
    }

    @GetMapping("/proactive")
  public ResponseEntity<ProactiveCheckInScheduler.Nudge> proactive() {
    var nudge = scheduler.latest();
    return nudge == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(nudge);
}
}


