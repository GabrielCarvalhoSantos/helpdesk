package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.PreferenciasNotificacaoDTO;
import integrador2.helpdesk.model.User;
import integrador2.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;

    // GET /api/v1/usuarios/preferencias
    @GetMapping("/preferencias")
    public PreferenciasNotificacaoDTO getPreferencias(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        User u = userRepo.findByEmail(principal.getUsername()).orElseThrow();
        PreferenciasNotificacaoDTO dto = new PreferenciasNotificacaoDTO();
        dto.setNotificarAtualizacao(u.isNotificarAtualizacao());
        dto.setNotificarFechamento(u.isNotificarFechamento());
        dto.setNotificarPorEmail(u.isNotificarPorEmail());
        return dto;
    }

    @PutMapping("/preferencias")
    public void atualizarPreferencias(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestBody PreferenciasNotificacaoDTO dto) {

        User u = userRepo.findByEmail(principal.getUsername()).orElseThrow();
        u.setNotificarAtualizacao(dto.isNotificarAtualizacao());
        u.setNotificarFechamento(dto.isNotificarFechamento());
        u.setNotificarPorEmail(dto.isNotificarPorEmail());
        userRepo.save(u);
    }

}
