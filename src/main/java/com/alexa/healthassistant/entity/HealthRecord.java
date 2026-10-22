package com.alexa.healthassistant.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@AllArgsConstructor 
@NoArgsConstructor 
@Getter 
@Setter 
public class HealthRecord {

@Id 
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
@Enumerated (value=EnumType.STRING)
@Column(nullable = false)
private RecordType recordType;
@Column(nullable = false)
private String content;
@Column(nullable = false)
private Instant recordedAt;
private Instant createdAt=Instant.now();
@Enumerated (value=EnumType.STRING)
private Source source;

}
