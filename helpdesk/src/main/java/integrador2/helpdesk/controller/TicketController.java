package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.*;
import integrador2.helpdesk.enums.Priority;
import integrador2.helpdesk.enums.Status;
import integrador2.helpdesk.model.Ticket;
import integrador2.helpdesk.model.User;
import integrador2.helpdesk.repository.UserRepository;
import integrador2.helpdesk.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService service;
    private final UserRepository userRepo;

    @PostMapping
    public ResponseEntity<TicketResponse> abrir(@RequestBody TicketRequest dto,
                                                @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        User cliente = userRepo.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return ResponseEntity.status(201).body(service.criar(dto, cliente));
    }

    @PutMapping("/{id}/status")
    public void mudarStatus(@PathVariable Long id,
                            @RequestBody TicketStatusDTO body,
                            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        User usuario = userRepo.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        service.mudarStatus(id, body.getNovoStatus(), usuario);
    }



    @PutMapping("/{id}/categoria")
    @PreAuthorize("hasRole('TECNICO')")
    public void mudarCategoria(@PathVariable Long id,
                               @RequestParam Long novaCategoriaId,
                               @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        User tecnico = userRepo.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        service.mudarCategoria(id, novaCategoriaId, tecnico);
    }

    @PutMapping("/{id}/cancel")
    public void cancelar(@PathVariable Long id,
                         @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        var cliente = userRepo.findByEmail(principal.getUsername()).orElseThrow();
        service.cancelarChamado(id, cliente);
    }

    @PutMapping("/{id}/assign") @PreAuthorize("hasRole('TECNICO')")
    public void assign(@PathVariable Long id,
                       @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        var tecnico = userRepo.findByEmail(principal.getUsername()).orElseThrow();

        service.atribuirTecnico(id, tecnico);
    }

    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('TECNICO','GESTOR')")
    public void resolver(@PathVariable Long id,
                         @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        User tecnico = userRepo.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        service.mudarStatus(id, Status.RESOLVIDO, tecnico);
    }

    @GetMapping
    public Object listar(@RequestParam Status status,
                         @RequestParam(required = false) Integer page,
                         @RequestParam(required = false) Integer size,
                         @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        User usuario = userRepo.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return (page == null || size == null)
                ? service.listarTodos(status, usuario)
                : service.listarPorStatus(status, page, size, usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailResponse> getTicketById(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        User usuario = userRepo.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        TicketDetailResponse detail = service.getTicketDetails(id, usuario);

        return ResponseEntity.ok(detail);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TicketHistoryResponse>> getTicketHistory(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        User usuario = userRepo.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        List<TicketHistoryResponse> history = service.getTicketHistory(id, usuario);

        return ResponseEntity.ok(history);
    }

    @GetMapping("/my-tickets")
    @PreAuthorize("hasAnyRole('TECNICO', 'GESTOR')")
    public ResponseEntity<List<TicketResponse>> getMyTickets(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        User tecnico = userRepo.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        List<TicketResponse> tickets = service.getTicketsByTecnico(tecnico);
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/{id}/comment")
    @PreAuthorize("hasAnyRole('TECNICO', 'GESTOR')")
    public ResponseEntity<Void> addComment(
            @PathVariable Long id,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        User usuario = userRepo.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        service.addComment(id, request.getComment(), usuario);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/prioridade")
    @PreAuthorize("hasRole('TECNICO')")
    public void mudarPrioridade(@PathVariable Long id, @RequestParam Priority novaPrioridade,
                                @AuthenticationPrincipal User principal) {
        service.mudarPrioridade(id, novaPrioridade, principal);
    }

    @GetMapping("/sla/conformidade")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Double> getSlaConformidade() {
        return ResponseEntity.ok(service.calcularConformidadeSla());
    }

    @GetMapping("/tempo-medio/categoria")
    public ResponseEntity<List<EstatisticaItemDTO>> getTempoMedioPorCategoria() {
        return ResponseEntity.ok(service.calcularTempoMedioPorCategoria());
    }

    @GetMapping("/estatisticas/desempenho-tecnicos")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<List<DesempenhoTecnicoDTO>> getDesempenhoTecnicos() {
        return ResponseEntity.ok(service.getDesempenhoTecnicos());
    }

}
