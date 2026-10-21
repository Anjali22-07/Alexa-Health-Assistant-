# Alexa Health Assistant 🩺🎙️

**A voice-first health companion that connects wearable health data with the context of everyday life.**

Alexa Health Assistant is a project exploring how voice interactions and smartwatch data can work together to help users maintain a personal health timeline, recall health-related information, and generate a structured summary for healthcare professionals.

## 💡 The Problem

Health information is often scattered across wearable devices, medicine reminders, personal notes, and everyday experiences. When visiting a doctor or trying to understand changes in their well-being, users may struggle to remember when symptoms started, what medication they reported taking, or what their recent activity and sleep looked like.

## 🎯 The Solution

Alexa Health Assistant aims to bring these pieces of information together in one place.

Users will be able to record health updates through voice, incorporate supported wearable data, and review a timeline that connects health observations with daily events.

### Key Features

- 🎙️ **Voice-based health logging:** Record symptoms, medications, supplements, and health-related events through Alexa.
- ⌚ **Wearable data integration:** Import supported health measurements from Garmin data exports, with the possibility of a future authorized integration.
- 🗓️ **Personal health timeline:** Organize health observations, medication notes, activities, and relevant life events by time.
- 🧠 **Context-aware recall:** Ask questions about previously recorded information.
- 📄 **Healthcare summaries:** Generate a structured report containing recorded health information for discussion with a healthcare professional.
- 🌐 **Web dashboard:** View and manage health records and explore the personal health timeline.

## 🏗️ Planned Architecture

```text
          User
           |
     Alexa Voice Skill
           |
           v
    Spring Boot Backend
           |
      +----+----+
      |         |
   Database   Context Engine
      |         |
      +----+----+
           |
      Web Dashboard
           ^
           |
   Garmin Data Import
```

Alexa and the web dashboard will use the backend to work with health records. Imported wearable measurements and user-reported information will remain distinguishable.

## 🛠️ Technology Stack

| Component | Technology |
|---|---|
| Backend | Java 21, Spring Boot |
| API | REST |
| Persistence | Spring Data JPA |
| Initial database | H2 for development |
| Planned database | MySQL |
| Voice interface | Alexa Custom Skill |
| Frontend | React, Vite |
| Wearable data | Garmin export; integration options to be evaluated |
| Context and summarization | To be determined |

## 🚧 Development Roadmap

- [ ] Create the Spring Boot backend.
- [ ] Implement the health record model and REST APIs.
- [ ] Persist and retrieve health records.
- [ ] Import and inspect available Garmin health data.
- [ ] Build the React dashboard and health timeline.
- [ ] Integrate an Alexa Custom Skill for voice-based logging and recall.
- [ ] Develop context-aware retrieval and summarization.
- [ ] Generate a structured healthcare report.
- [ ] Evaluate secure data access, consent, and deployment.

## 🔐 Privacy and Safety

Health information is sensitive. The project aims to distinguish user-reported information from wearable measurements, preserve timestamps and data sources, and make it clear when information is missing or uncertain.

Generated summaries are intended to support conversations with healthcare professionals, not to diagnose conditions or replace medical advice. Any real-world deployment will require appropriate authentication, authorization, consent, and data protection.

## 📍 Project Status

**Early development — prototype in progress.**

The initial milestone is a Spring Boot backend that can store and retrieve timestamped health records. Alexa integration, Garmin data import, the dashboard, and report generation are planned features and are not yet represented as completed functionality.

---

Built as an exploration of voice-first, context-aware personal health software.
