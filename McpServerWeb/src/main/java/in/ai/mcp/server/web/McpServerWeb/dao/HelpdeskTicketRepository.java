package in.ai.mcp.server.web.McpServerWeb.dao;

import in.ai.mcp.server.web.McpServerWeb.entity.HelpdeskTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpdeskTicketRepository extends JpaRepository<HelpdeskTicket, Long> {

    List<HelpdeskTicket> findByUsername(String username);
}
