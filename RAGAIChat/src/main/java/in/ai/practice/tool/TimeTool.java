package in.ai.practice.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeTool {

    //returnDirect=true means response will be shown directly to enduser,
    // it will not go back to LLM for post processing
    //@Tool(name = "currentTime", description = "get current time of user system")
    @Tool(name = "currentTime", description = "get current time of user system", returnDirect = true)
    public String currentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    @Tool(name = "zoneBasedTime", description = "Get time for specified timezone")
    public String currentZoneTime(@ToolParam(description = "ZoneID for provided timezone") ZoneId zoneId) {
        // Get current date-time in that zone
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

        // Format output
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss z");
        String formattedDateTime = zonedDateTime.format(formatter);
        return formattedDateTime;
    }
}
