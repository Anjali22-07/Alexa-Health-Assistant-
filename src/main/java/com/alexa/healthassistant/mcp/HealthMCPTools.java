package com.alexa.healthassistant.mcp;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

@Component 
public class HealthMCPTools {

    @McpTool(name="ping", description = "Check if the health assistant server is running")
    public String ping(){
        return "pong";
    }

}
