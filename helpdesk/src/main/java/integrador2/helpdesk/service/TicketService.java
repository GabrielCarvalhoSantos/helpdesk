package integrador2.helpdesk.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import integrador2.helpdesk.enums.Priority;
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
import integrador2.helpdesk.dto.TicketHistoryResponse;


@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepo;
    private final CategoryRepository categoryRepo;
    private final SlaService slaService;
    private final TicketHistoryService historySrv;
    private final DepartmentRepository  deptRepo;
    private final TicketHistoryRepository ticketHistoryRepo;
    private final NotificacaoService notificacaoService;
    private final UserRepository userRepo;

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
                .build();
        ticketRepo.save(t);
        historySrv.log(t, cliente, null, Status.ABERTO, "Chamado criado");

        if (t.getPrioridade() == Priority.ALTA) {
            List<User> tecnicos = userRepo.findByTipo(UserType.TECNICO);
            for (User tecnico : tecnicos) {
                notificacaoService.notificar(
                        tecnico,
                        "Novo chamado de ALTA prioridade criado: #" + t.getId()
                );
            }
        }

        return toResponse(t);
    }


    @Transactional
    public void mudarStatus(Long id, Status novoStatus, User usuario) {
        Ticket t = ticketRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado"));

        Status antigo = t.getStatus();
        t.setStatus(novoStatus);
        ticketRepo.save(t);

        if (novoStatus == Status.AGUARDANDO_CLIENTE || novoStatus == Status.RESOLVIDO) {
            notificacaoService.notificar(
                    t.getCliente(),
                    "Seu chamado #" + t.getId() + " foi atualizado para " + novoStatus
            );
        }


        historySrv.log(t, usuario, antigo, novoStatus, "Status alterado");
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> listarTodos(Status st, User usuarioAutenticado) {
        // Se o usuário for CLIENTE, mostrar apenas seus próprios chamados
        if (usuarioAutenticado.getTipo() == UserType.CLIENTE) {
            return ticketRepo.findByStatusAndCliente_Id(st, usuarioAutenticado.getId()).stream()
                    .map(this::toResponse)
                    .toList();
        }

        // Para GESTOR e TECNICO, mostrar todos os chamados (mantém o comportamento atual)
        return ticketRepo.findByStatus(st).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<TicketResponse> listarPorStatus(Status st, int page, int size, User usuarioAutenticado) {
        // Se o usuário for CLIENTE, mostrar apenas seus próprios chamados
        if (usuarioAutenticado.getTipo() == UserType.CLIENTE) {
            return ticketRepo.findByStatusAndCliente_Id(st, usuarioAutenticado.getId(), PageRequest.of(page, size))
                    .map(this::toResponse);
        }

        // Para GESTOR e TECNICO, mostrar todos os chamados (mantém o comportamento atual)
        return ticketRepo.findByStatus(st, PageRequest.of(page, size))
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
    public void atribuirTecnico(Long id, User tecnico) {
        if (tecnico == null || tecnico.getTipo() != UserType.TECNICO) {
            throw new IllegalArgumentException("Somente técnicos podem assumir");
        }

        Ticket t = ticketRepo.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Chamado não encontrado"));

        if (t.getTecnico() != null) {
            throw new IllegalStateException("Este chamado já está sendo atendido por " + t.getTecnico().getNome());
        }

        if (t.getStatus() != Status.ABERTO) {
            throw new IllegalStateException("Apenas chamados ABERTOS podem ser assumidos");
        }

        t.setTecnico(tecnico);
        t.setStatus(Status.EM_ATENDIMENTO);

        // ⏰ Ativa o SLA aqui:
        Instant prazo = slaService.calcularPrazo(t.getCategoria(), t.getPrioridade());
        t.setPrazoSla(prazo);

        ticketRepo.save(t);

        notificacaoService.notificar(
                t.getCliente(),
                "Um técnico assumiu o chamado #" + t.getId()
        );

        historySrv.log(t, tecnico, Status.ABERTO, Status.EM_ATENDIMENTO,
                "Técnico %s assumiu o chamado".formatted(tecnico.getNome()));
    }


    public TicketDetailResponse getTicketDetails(Long id, User usuario) {
        Ticket ticket = ticketRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado"));

        if (usuario.getTipo() == UserType.CLIENTE && !ticket.getCliente().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar este chamado");
        }

        return TicketDetailResponse.fromTicket(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketHistoryResponse> getTicketHistory(Long id, User usuario) {
        Ticket ticket = ticketRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado"));
        verificarPermissaoAcesso(ticket, usuario);
        List<TicketHistory> history = ticketHistoryRepo.findHistoryByTicketId(id);
        return history.stream()
                .map(TicketHistoryResponse::fromHistory)
                .collect(Collectors.toList());
    }

    private void verificarPermissaoAcesso(Ticket ticket, User usuario) {
        boolean hasAccess = ticket.getCliente().getId().equals(usuario.getId()) ||
                (ticket.getTecnico() != null && ticket.getTecnico().getId().equals(usuario.getId())) ||
                usuario.getTipo() == UserType.GESTOR;

        if (!hasAccess) {
            throw new AccessDeniedException("Você não tem permissão para acessar este chamado");
        }
    }

    @Transactional
    public void addComment(Long ticketId, String comment, User user) {
        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado"));

        // Verificar se o usuário tem permissão (é o técnico, gestor ou cliente do chamado)
        boolean hasAccess = ticket.getCliente().getId().equals(user.getId()) ||
                (ticket.getTecnico() != null && ticket.getTecnico().getId().equals(user.getId())) ||
                user.getTipo() == UserType.GESTOR;

        if (!hasAccess) {
            throw new AccessDeniedException("Você não tem permissão para adicionar comentários a este chamado");
        }

        historySrv.log(ticket, user, null, null, comment);
    }

    public List<TicketResponse> getTicketsByTecnico(User tecnico) {
        // Buscar chamados atribuídos ao técnico
        List<Ticket> tickets = ticketRepo.findByTecnicoId(tecnico.getId());

        // Mapear para DTO de resposta
        return tickets.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void mudarPrioridade(Long id, Priority nova, User tecnico) {
        Ticket t = ticketRepo.findById(id).orElseThrow();
        Priority anterior = t.getPrioridade();
        t.setPrioridade(nova);
        t.setPrazoSla(slaService.calcularPrazo(t.getCategoria(), nova));
        ticketRepo.save(t);
        historySrv.log(t, tecnico, null, null,
                "Prioridade alterada de %s para %s".formatted(anterior, nova));
    }

}
