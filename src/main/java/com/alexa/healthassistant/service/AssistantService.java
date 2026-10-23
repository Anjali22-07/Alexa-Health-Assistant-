package com.alexa.healthassistant.service;

public interface AssistantService {

    String chat(String userMessage);
    String checkIn();
}