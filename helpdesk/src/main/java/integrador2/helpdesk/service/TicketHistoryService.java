package integrador2.helpdesk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import integrador2.helpdesk.enums.Status;
import integrador2.helpdesk.model.*;
import integrador2.helpdesk.repository.TicketHistoryRepository;

@Service
@RequiredArgsConstructor
public class TicketHistoryService {

    private final TicketHistoryRepository repo;

    public void log(Ticket ticket, User usuario, Status de, Status para, String acao) {
        repo.save(TicketHistory.builder()
                .ticket(ticket)
                .usuario(usuario)
                .deStatus(de)
                .paraStatus(para)
                .acao(acao)
                .build());
    }
}
