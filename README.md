# Alexa Health Assistant 🩺🎙️

**A voice-first health assistant that brings together user-reported symptoms, medication history, and wearable health data to make personal health information easier to access and share.**

Alexa Health Assistant is a healthcare-focused prototype built with Java, Spring Boot, React, and MySQL. It allows users to record health information through a conversational voice interface, review their health history on a dashboard, and combine different sources of health data in one place.

The project explores how voice interactions, structured health records, and wearable data can help users maintain a more accessible health history, including information that may be useful during a medical emergency.

## ✨ Features

- **🎙️ Voice-enabled health logging:** Speak naturally to the assistant to report symptoms and record medications or supplements.
- **💬 Conversational interface:** Type messages or use browser-based speech recognition to interact with the assistant.
- **💊 Medication and symptom tracking:** Store health events with timestamps and review previously recorded information.
- **⌚ Garmin data integration:** Import exported Garmin health data and store supported measurements such as sleep, heart rate, steps, and activity.
- **📊 Health dashboard:** View health records, recent symptom and medication counts, Garmin measurements, and a combined chronological timeline.
- **🔎 Unified health history:** Distinguish between wearable measurements and information reported through the assistant or website.
- **🚑 Emergency health reporting — planned:** Develop a printable, doctor-friendly summary of recorded symptoms, medications, and available wearable measurements for medical professionals.

## 🏗️ Architecture

```text
                 User
                  |
          Voice or Text Input
                  |
          React Web Interface
                  |
          Spring Boot Backend
                  |
       Spring AI / ChatClient
                  |
          MCP Health Tools
                  |
          Health Service
                  |
              MySQL
                  |
       +----------+-----------+
       |          |           |
   Symptoms   Medications   Garmin Data
       |          |           |
       +----------+-----------+
                  |
           Health Dashboard
```

The browser provides the conversational interface, while the Spring Boot backend manages health records and assistant interactions. The Garmin importer processes supported files exported from Garmin rather than relying on a live wearable connection.

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Backend development |
| Spring Boot | REST APIs and application services |
| Spring AI | Conversational AI integration |
| Model Context Protocol (MCP) | Exposing health-record tools to the assistant |
| MySQL | Persistent health-record storage |
| Spring Data JPA / Hibernate | Database access and entity management |
| React | Frontend and health dashboard |
| Vite | Frontend development server and build tooling |
| Web Speech API | Browser-based speech recognition and text-to-speech |
| Garmin export files | Importing supported wearable health data |

## 📋 Health Data Sources

The application distinguishes between three record sources:

- **ALEXA:** Health information reported through the conversational assistant.
- **WEBSITE:** Health information entered through the web interface.
- **GARMIN:** Measurements imported from supported Garmin export files.

Each record stores information such as its type, content, recorded timestamp, creation timestamp, and source.

Supported record types include:

- `SYMPTOM`
- `MEDICATION`
- `SLEEP`
- `HEART_RATE`
- `ACTIVITY`

The application can be extended to support additional health-record types as the data model evolves.

## 🔌 API Endpoints

The current frontend and backend integration includes the following endpoints:

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/V1/chat` | Send a message to the assistant |
| `GET` | `/api/V1/healthRecords` | Retrieve stored health records |
| `POST` | `/api/V1/import/garmin` | Import supported Garmin export data |

The exact request format and required parameters depend on the backend implementation.

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven
- Node.js and npm
- MySQL
- A supported Google GenAI / Gemini API configuration for the AI integration

### 1. Clone the repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd <YOUR_PROJECT_DIRECTORY>
```

### 2. Configure MySQL

Create a database for the application:

```sql
CREATE DATABASE healthassistant;
```

Configure your database connection and AI credentials using environment variables or your local Spring configuration.

Do not commit database passwords, API keys, or other secrets to GitHub.

### 3. Start the backend

From the Spring Boot project directory, run:

```bash
mvn spring-boot:run
```

Ensure that MySQL is running and the application configuration is correct.

### 4. Start the frontend

Open a terminal in the React/Vite frontend directory and run:

```bash
npm install
npm run dev
```

Open the local URL printed by Vite in your terminal.

Use a supported browser with microphone permission enabled to test voice input.

> Note: Configure the frontend development proxy to route `/api` requests to the Spring Boot backend, or configure the appropriate backend URL and CORS settings for your environment.

## 🧪 Example Interactions

**Record a symptom**

> "Alexa, I've had a headache since this morning."

The assistant should identify the reported symptom and save it as a health record.

**Record medication**

> "Alexa, I took paracetamol 500 milligrams at 10 AM today."

The assistant should record the medicine and the details provided by the user.

**Review health history**

> "Alexa, what symptoms have I reported recently?"

The assistant can use its health-record retrieval tools to look up available records.

These interactions depend on the assistant's configured tool-calling behavior and successful database persistence.

## 🔒 Privacy and Medical Safety

Health information is sensitive. This prototype is intended for development and demonstration, not as a production clinical system.

- Store only health information needed for the intended functionality.
- Do not commit real patient records or credentials to the repository.
- Clearly distinguish imported wearable measurements from user-reported information.
- Do not infer a diagnosis or claim that a wearable measurement caused a symptom.
- Missing or unverified data should be identified explicitly.
- Any emergency report should be reviewed by a qualified medical professional and should not replace clinical assessment.

## 🗺️ Future Improvements

- [ ] Generate downloadable PDF emergency health reports.
- [ ] Add structured health summaries for sleep, activity, and heart rate.
- [ ] Support configurable 7-day and 15-day reporting periods.
- [ ] Improve multilingual voice interactions.
- [ ] Add stronger validation and error handling for health-record tool calls.
- [ ] Add authentication, authorization, and per-user health-data isolation.
- [ ] Improve automated testing for the API, data importer, and conversational workflows.
- [ ] Explore secure integrations with additional health-data providers.

## 🎯 Project Goal

The goal of Alexa Health Assistant is to explore a practical way of combining conversational interfaces and personal health records. By bringing reported symptoms, medication history, and wearable measurements together, the project aims to make relevant health information easier to review and communicate when it matters.

---

**Built with Java, Spring Boot, Spring AI, React, and MySQL.**
