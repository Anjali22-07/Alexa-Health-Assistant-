package com.alexa.healthassistant.dto;

import java.time.Instant;
import java.util.UUID;
import com.alexa.healthassistant.entity.Source;
import com.alexa.healthassistant.entity.RecordType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@ToString 
public class HealthRecordDto {
    
private UUID id;
@NotBlank(message = "type is required")
private RecordType recordType;
@Size(max = 1000, message = "notes must be 1000 characters or fewer")
private String content;
@NotBlank(message = "rwcordedAt is required")
private Instant recordedAt;
private Instant createdAt;
@NotBlank(message = "source  is required")
private Source source;

}
