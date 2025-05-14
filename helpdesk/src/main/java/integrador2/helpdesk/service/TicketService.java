package integrador2.helpdesk.service;

import java.util.List;
import java.util.Optional;

import integrador2.helpdesk.enums.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import integrador2.helpdesk.dto.*;
import integrador2.helpdesk.enums.Status;
import integrador2.helpdesk.model.*;
import integrador2.helpdesk.repository.*;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepo;
    private final CategoryRepository categoryRepo;
    private final SlaService slaService;
    private final TicketHistoryService historySrv;
    private final DepartmentRepository  deptRepo;

    @Transactional
    public TicketResponse criar(TicketRequest dto, User cliente) {

        Category    categoria    = categoryRepo.findById(dto.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria inválida"));
        Department  departamento = deptRepo.findById(dto.getDepartamentoId())
                .orElseThrow(() -> new IllegalArgumentException("Departamento inválido"));

        Ticket t = Ticket.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .prioridade(dto.getPrioridade())
                .categoria(categoria)
                .departamento(departamento)        //  ✓
                .cliente(cliente)
                .prazoSla(slaService.calcularPrazo(categoria, dto.getPrioridade()))
                .build();
        ticketRepo.save(t);
        historySrv.log(t, cliente, null, Status.ABERTO, "Chamado criado");
        return toResponse(t);
    }


    @Transactional
    public void mudarStatus(Long id, Status novoStatus, User usuario) {
        Ticket t = ticketRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado"));

        Status antigo = t.getStatus();
        t.setStatus(novoStatus);
        ticketRepo.save(t);

        historySrv.log(t, usuario, antigo, novoStatus, "Status alterado");
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> listarTodos(Status st){
        return ticketRepo.findByStatus(st).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<TicketResponse> listarPorStatus(Status st,int page,int size){
        return ticketRepo.findByStatus(st, PageRequest.of(page,size))
                .map(this::toResponse);
    }

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

    @Transactional
    public void mudarCategoria(Long ticketId, Long novaCategoriaId, User tecnico) {

        Ticket t = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado"));

        Category nova = categoryRepo.findById(novaCategoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria inválida"));

        Category antiga = t.getCategoria();
        t.setCategoria(nova);
        ticketRepo.save(t);

        historySrv.log(t, tecnico, null, null,
                "Categoria alterada de %s para %s".formatted(antiga.getNome(), nova.getNome()));
    }

    @Transactional
    public void cancelarChamado(Long id, User cliente){
        Ticket t = ticketRepo.findById(id).orElseThrow();
        if(!t.getCliente().getId().equals(cliente.getId()))             // compara id
            throw new AccessDeniedException("Não é seu chamado");
        if(t.getStatus()!=Status.ABERTO)
            throw new IllegalStateException("Só chamado ABERTO pode cancelar");
        t.setStatus(Status.FECHADO);
        ticketRepo.save(t);
        historySrv.log(t,cliente,Status.ABERTO,Status.FECHADO,"Cancelado pelo cliente");
    }

    @Transactional
    public void atribuirTecnico(Long id, User tecnico){
        if (tecnico==null || tecnico.getTipo()!=UserType.TECNICO)
            throw new IllegalArgumentException("Somente técnicos podem assumir");

        Ticket t = ticketRepo.findById(id).orElseThrow();
        t.setTecnico(tecnico);
        t.setStatus(Status.EM_ATENDIMENTO);
        ticketRepo.save(t);

        historySrv.log(t, tecnico, Status.ABERTO, Status.EM_ATENDIMENTO,
                "Técnico %s assumiu o chamado".formatted(tecnico.getNome()));
    }


}
