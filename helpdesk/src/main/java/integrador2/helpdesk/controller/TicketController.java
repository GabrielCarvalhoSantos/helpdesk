package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.*;
import integrador2.helpdesk.enums.Status;
import integrador2.helpdesk.model.User;
import integrador2.helpdesk.repository.UserRepository;
import integrador2.helpdesk.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public Page<TicketResponse> listar(@RequestParam Status status,
                                       @RequestParam int page,
                                       @RequestParam int size) {
        return service.listarPorStatus(status, PageRequest.of(page, size));
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
}
