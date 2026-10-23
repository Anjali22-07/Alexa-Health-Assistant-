package com.alexa.healthassistant.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import com.alexa.healthassistant.entity.RecordType;
import com.fasterxml.jackson.databind.JsonNode;

public interface GarminImporterService {

    public int importZip(InputStream in) throws IOException;
    public int importSleep(JsonNode root);
    public int importDaily(JsonNode root);
     public boolean saveIfNew(RecordType type, String content, Instant when);
      

}
