// TicketRepository.java
package integrador2.helpdesk.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import integrador2.helpdesk.enums.Status;
import integrador2.helpdesk.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByStatus(Status status, Pageable page);

    Page<Ticket> findByCliente_Id(Long clienteId, Pageable page);

    Page<Ticket> findByTecnico_Id(Long tecnicoId, Pageable page);

    long countByStatus(Status status);                 // KPI rápido
}
