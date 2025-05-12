package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.*;
import integrador2.helpdesk.enums.Status;
import integrador2.helpdesk.model.User;
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

    @PostMapping
    public ResponseEntity<TicketResponse> abrir(@RequestBody TicketRequest dto,
                                                @AuthenticationPrincipal User user){
        return ResponseEntity.status(201).body(service.criar(dto, user));
    }

    @PutMapping("/{id}/status")
    public void mudarStatus(@PathVariable Long id,
                            @RequestBody TicketStatusDTO body,
                            @AuthenticationPrincipal User user){
        service.mudarStatus(id, body.getNovoStatus(), user);
    }

    @GetMapping
    public Page<TicketResponse> listar(@RequestParam Status status,
                                       @RequestParam int page, @RequestParam int size){
        return service.listarPorStatus(status, PageRequest.of(page, size));
    }
}
