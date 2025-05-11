// TicketHistoryRepository.java
package integrador2.helpdesk.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import integrador2.helpdesk.model.TicketHistory;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {
    List<TicketHistory> findByTicket_IdOrderByCriadoEmAsc(Long ticketId);
}
