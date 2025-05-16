package integrador2.helpdesk.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import integrador2.helpdesk.model.TicketHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {
    List<TicketHistory> findByTicket_IdOrderByCriadoEmAsc(Long ticketId);

    @Query("SELECT th FROM TicketHistory th WHERE th.ticket.id = :ticketId ORDER BY th.criadoEm ASC")
    List<TicketHistory> findHistoryByTicketId(@Param("ticketId") Long ticketId);

    List<TicketHistory> findByTicketIdOrderByCriadoEmAsc(Long ticketId);
}