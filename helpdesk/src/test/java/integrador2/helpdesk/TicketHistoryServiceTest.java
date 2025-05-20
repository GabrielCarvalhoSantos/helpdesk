package integrador2.helpdesk;

import integrador2.helpdesk.enums.Status;
import integrador2.helpdesk.model.Ticket;
import integrador2.helpdesk.model.TicketHistory;
import integrador2.helpdesk.model.User;
import integrador2.helpdesk.repository.TicketHistoryRepository;
import integrador2.helpdesk.service.TicketHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TicketHistoryServiceTest {

    @Mock
    private TicketHistoryRepository repo;

    @InjectMocks
    private TicketHistoryService service;

    private Ticket ticket;
    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ticket = Ticket.builder().id(100L).build();
        user   = User.builder().id(50L).build();
    }

    @Test
    void log_savesHistoryWithAllFields() {
        service.log(ticket, user, Status.ABERTO, Status.RESOLVIDO, " ocorreu X");

        ArgumentCaptor<TicketHistory> cap = ArgumentCaptor.forClass(TicketHistory.class);
        verify(repo).save(cap.capture());

        TicketHistory th = cap.getValue();
        assertEquals(ticket, th.getTicket());
        assertEquals(user,   th.getUsuario());
        assertEquals(Status.ABERTO,   th.getDeStatus());
        assertEquals(Status.RESOLVIDO, th.getParaStatus());
        assertEquals(" ocorreu X",    th.getAcao());
    }
}
