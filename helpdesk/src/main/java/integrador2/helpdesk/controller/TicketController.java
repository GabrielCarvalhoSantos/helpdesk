package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.*;
import integrador2.helpdesk.enums.Status;
import integrador2.helpdesk.model.User;
import integrador2.helpdesk.repository.UserRepository;
import integrador2.helpdesk.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService service;
    private final UserRepository userRepo;

    /** Abre um novo chamado (cliente autenticado) */
    @PostMapping
    public ResponseEntity<TicketResponse> abrir(@RequestBody TicketRequest dto,
                                                @AuthenticationPrincipal(expression = "username") String email) {

        User usuario = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        TicketResponse resp = service.criar(dto, usuario);
        return ResponseEntity.status(201).body(resp);
    }

    /** Altera o status de um chamado (técnico autenticado) */
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> mudarStatus(@PathVariable Long id,
                                            @RequestBody TicketStatusDTO body,
                                            @AuthenticationPrincipal(expression = "username") String email) {

        User usuario = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        service.mudarStatus(id, body.getNovoStatus(), usuario);
        return ResponseEntity.ok().build();
    }

    /** Lista chamados pelo status (paginado) */
    @GetMapping
    public Page<TicketResponse> listar(@RequestParam Status status,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {

        return service.listarPorStatus(status, PageRequest.of(page, size, Sort.by("abertoEm").descending()));
    }
}
