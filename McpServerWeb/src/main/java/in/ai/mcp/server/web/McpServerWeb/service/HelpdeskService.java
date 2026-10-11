package in.ai.mcp.server.web.McpServerWeb.service;

import in.ai.mcp.server.web.McpServerWeb.dao.HelpdeskTicketRepository;
import in.ai.mcp.server.web.McpServerWeb.entity.HelpdeskTicket;
import in.ai.mcp.server.web.McpServerWeb.model.TicketInput;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HelpdeskService {

    private final HelpdeskTicketRepository helpdeskTicketRepository;

    public HelpdeskTicket createTicket(TicketInput ticketInput) {
        HelpdeskTicket helpdeskTicket = HelpdeskTicket.builder()
                .description(ticketInput.description())
                .username(ticketInput.username())
                .status("Open")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(ticketInput.username())
                .build();
        return helpdeskTicketRepository.save(helpdeskTicket);
    }

    public List<HelpdeskTicket> getTicketsByUsername(String username) {
        return helpdeskTicketRepository.findByUsername(username);
    }
}
