package in.ai.mcp.server.stdio.tools;

import in.ai.mcp.server.stdio.entity.HelpdeskTicket;
import in.ai.mcp.server.stdio.model.TicketInput;
import in.ai.mcp.server.stdio.service.HelpdeskService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

@Component
@RequiredArgsConstructor
public class HelpdeskTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelpdeskTool.class);

    private final HelpdeskService helpdeskService;

    @Tool(name = "createTicket", description = "create a support ticket")
    public String createTicket(@ToolParam(description = "details to create support ticket") TicketInput ticketInput) {
        LOGGER.info("Creating support ticket for username: {} with description: {}", ticketInput.username(), ticketInput.description());
        HelpdeskTicket savedTicket = helpdeskService.createTicket(ticketInput);
        LOGGER.info("Ticket created successfully. ticketId {}", savedTicket.getId());
        return "Ticket " + savedTicket.getId() + " created successfully for user " + savedTicket.getUsername();
    }

    @Tool(name = "fetchTicket", description = "fetch all support tickets for a given user")
    public List<HelpdeskTicket> fetchTicket(@ToolParam(description = "username to fetch all support tickets") String username) {
        LOGGER.info("Fetching tickets for username: {}", username);
        List<HelpdeskTicket> tickets = helpdeskService.getTicketsByUsername(username);
        LOGGER.info("Tickets fetched successfully. ticketSize {}", tickets.size());
        return tickets;
    }
}

