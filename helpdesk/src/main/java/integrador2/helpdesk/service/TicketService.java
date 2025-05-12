package integrador2.helpdesk.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import integrador2.helpdesk.dto.*;
import integrador2.helpdesk.enums.Status;
import integrador2.helpdesk.model.*;
import integrador2.helpdesk.repository.*;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository      ticketRepo;
    private final CategoryRepository    categoryRepo;
    private final SlaService            slaService;
    private final TicketHistoryService  historySrv;

    /** abrir chamado */
    @Transactional
    public TicketResponse criar(TicketRequest dto, User cliente) {
        Category categoria = categoryRepo.findById(dto.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria inválida"));

        Ticket ticket = Ticket.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .prioridade(dto.getPrioridade())
                .categoria(categoria)
                .cliente(cliente)
                .prazoSla(slaService.calcularPrazo(categoria, dto.getPrioridade()))
                .build();
        ticketRepo.save(ticket);

        historySrv.log(ticket, cliente, null, Status.ABERTO, "Chamado criado");

        return toResponse(ticket);
    }

    /** mudar status */
    @Transactional
    public void mudarStatus(Long id, Status novoStatus, User usuario) {
        Ticket t = ticketRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado"));

        Status antigo = t.getStatus();
        t.setStatus(novoStatus);
        ticketRepo.save(t);

        historySrv.log(t, usuario, antigo, novoStatus, "Status alterado");
    }

    /** listagem paginada */
    public Page<TicketResponse> listarPorStatus(Status status, Pageable page) {
        return ticketRepo.findByStatus(status, page).map(this::toResponse);
    }

    // ===== util =====
    private TicketResponse toResponse(Ticket t) {
        return TicketResponse.builder()
                .id(t.getId())
                .titulo(t.getTitulo())
                .prioridade(t.getPrioridade())
                .status(t.getStatus())
                .categoria(t.getCategoria().getNome())
                .abertoEm(t.getAbertoEm())
                .prazoSla(t.getPrazoSla())
                .build();
    }
}
