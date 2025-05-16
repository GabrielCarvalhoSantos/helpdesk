package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.NotificacaoDTO;
import integrador2.helpdesk.model.User;
import integrador2.helpdesk.service.NotificacaoService;
import integrador2.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService service;
    private final UserRepository userRepo;

    @GetMapping
    public List<NotificacaoDTO> listar(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        User usuario = userRepo.findByEmail(principal.getUsername()).orElseThrow();
        return service.listar(usuario).stream().map(NotificacaoDTO::of).toList();
    }

    @PutMapping("/{id}/lida")
    public void marcarComoLida(@PathVariable Long id) {
        service.marcarComoLida(id);
    }
}
