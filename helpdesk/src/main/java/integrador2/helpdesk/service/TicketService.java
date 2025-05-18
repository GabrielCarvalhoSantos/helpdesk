package integrador2.helpdesk.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


// Correção no src/main/java/integrador2/helpdesk/service/TicketService.java

    @Transactional
    public void mudarStatus(Long id, Status novoStatus, User usuario) {
        Ticket t = ticketRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado"));

        Status antigo = t.getStatus();
        t.setStatus(novoStatus);

        if (novoStatus == Status.RESOLVIDO || novoStatus == Status.FECHADO) {
            t.setFechadoEm(Instant.now());
        }

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
        t.setAssumidoEm(Instant.now()); // <-- NOVO

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

        // Modificar a verificação de permissão - permitir acesso para todos os técnicos
        verificarPermissaoHistorico(ticket, usuario);

        List<TicketHistory> history = ticketHistoryRepo.findHistoryByTicketId(id);
        return history.stream()
                .map(TicketHistoryResponse::fromHistory)
                .collect(Collectors.toList());
    }

    @Transactional
    public TicketResponse atualizar(Long id, TicketRequest dto, User cliente) {
        Ticket t = ticketRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado"));

        // Verificar se o chamado pertence ao cliente
        if (!t.getCliente().getId().equals(cliente.getId())) {
            throw new AccessDeniedException("Não é seu chamado");
        }

        // Verificar se o chamado está em estado aberto e sem técnico
        if (t.getStatus() != Status.ABERTO || t.getTecnico() != null) {
            throw new IllegalStateException("Este chamado não pode ser editado");
        }

        // Atualizar os dados
        Category categoria = categoryRepo.findById(dto.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria inválida"));
        Department departamento = deptRepo.findById(dto.getDepartamentoId())
                .orElseThrow(() -> new IllegalArgumentException("Departamento inválido"));

        t.setTitulo(dto.getTitulo());
        t.setDescricao(dto.getDescricao());
        t.setPrioridade(dto.getPrioridade());
        t.setCategoria(categoria);
        t.setDepartamento(departamento);

        Ticket updated = ticketRepo.save(t);

        // Registrar no histórico
        historySrv.log(t, cliente, null, null, "Chamado editado pelo cliente");

        return toResponse(updated);
    }

    // Método separado para verificar permissão ao histórico
    private void verificarPermissaoHistorico(Ticket ticket, User usuario) {
        // Técnicos e gestores sempre podem ver o histórico
        if (usuario.getTipo() == UserType.TECNICO || usuario.getTipo() == UserType.GESTOR) {
            return; // Permissão concedida
        }

        // Para clientes, somente os próprios chamados
        if (usuario.getTipo() == UserType.CLIENTE && ticket.getCliente().getId().equals(usuario.getId())) {
            return; // Permissão concedida
        }

        // Se chegar aqui, não tem permissão
        throw new AccessDeniedException("Você não tem permissão para acessar este histórico");
    }

    // O método original verificarPermissaoAcesso deve ser mantido para outras operações
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
    public void mudarPrioridade(Long id, Priority nova, User tecnico, String comentario) {
        Ticket t = ticketRepo.findById(id).orElseThrow();
        Priority anterior = t.getPrioridade();
        t.setPrioridade(nova);
        t.setPrazoSla(slaService.calcularPrazo(t.getCategoria(), nova));
        ticketRepo.save(t);

        // Registrar a alteração no histórico com o comentário
        String mensagem = "Prioridade alterada de %s para %s. Justificativa: %s"
                .formatted(anterior, nova, comentario);

        historySrv.log(t, tecnico, null, null, mensagem);

        // Notificar o cliente sobre a alteração
        if (t.getCliente() != null) {
            notificacaoService.notificar(
                    t.getCliente(),
                    "Prioridade do seu chamado #" + t.getId() + " foi alterada de " +
                            anterior + " para " + nova + ". Justificativa: " + comentario
            );
        }
    }

    public double calcularConformidadeSla() {
        var chamados = ticketRepo.findAll().stream()
                .filter(t -> t.getFechadoEm() != null && t.getPrazoSla() != null)
                .toList();

        if (chamados.isEmpty()) return 100.0;

        long dentroPrazo = chamados.stream()
                .filter(t -> !t.getFechadoEm().isAfter(t.getPrazoSla()))
                .count();

        return (100.0 * dentroPrazo) / chamados.size();
    }

    public List<EstatisticaItemDTO> calcularTempoMedioPorCategoria() {
        var todasCategorias = categoryRepo.findAll().stream()
                .map(Category::getNome)
                .collect(Collectors.toSet());

        var mediasPorCategoria = ticketRepo.findAll().stream()
                .filter(t -> t.getFechadoEm() != null && t.getAssumidoEm() != null)
                .filter(t -> !t.getFechadoEm().isBefore(t.getAssumidoEm()))
                .collect(Collectors.groupingBy(
                        t -> t.getCategoria().getNome(),
                        Collectors.averagingDouble(t ->
                                Math.max(0, Duration.between(t.getAssumidoEm(), t.getFechadoEm()).toMinutes()))
                ));

        return todasCategorias.stream()
                .map(nome -> {
                    double mediaMinutos = mediasPorCategoria.getOrDefault(nome, 0.0);
                    double mediaHoras = Math.round((mediaMinutos / 60.0) * 10.0) / 10.0;
                    return new EstatisticaItemDTO(nome, mediaHoras);
                })
                .toList();
    }

    public List<DesempenhoTecnicoDTO> getDesempenhoTecnicos() {
        List<Ticket> tickets = ticketRepo.findAll().stream()
                .filter(t -> t.getTecnico() != null)
                .toList();

        Map<String, List<Ticket>> ticketsPorTecnico = tickets.stream()
                .collect(Collectors.groupingBy(t -> t.getTecnico().getNome()));

        List<DesempenhoTecnicoDTO> resultado = new ArrayList<>();

        for (Map.Entry<String, List<Ticket>> entry : ticketsPorTecnico.entrySet()) {
            String nome = entry.getKey();
            List<Ticket> chamados = entry.getValue();

            long atribuídos = chamados.size();
            long resolvidos = chamados.stream()
                    .filter(t -> t.getStatus() == Status.RESOLVIDO || t.getStatus() == Status.FECHADO)
                    .count();

            double mediaHoras = chamados.stream()
                    .filter(t -> t.getAssumidoEm() != null && t.getFechadoEm() != null)
                    .mapToDouble(t -> {
                        long minutos = Duration.between(t.getAssumidoEm(), t.getFechadoEm()).toMinutes();
                        return Math.max(minutos, 0) / 60.0;
                    })
                    .average().orElse(0.0);

            int taxa = atribuídos > 0 ? (int) ((resolvidos * 100) / atribuídos) : 0;

            String classificacao = "Sem Avaliação";
            if (atribuídos > 0) {
                if (taxa >= 90 && mediaHoras <= 24) classificacao = "Excelente";
                else if (taxa >= 75 && mediaHoras <= 48) classificacao = "Bom";
                else if (taxa >= 50) classificacao = "Regular";
                else classificacao = "Precisa Melhorar";
            }

            resultado.add(new DesempenhoTecnicoDTO(nome, atribuídos, resolvidos,
                    taxa + "%", Math.round(mediaHoras * 10.0) / 10.0, classificacao));
        }
        return resultado;
    }

}
