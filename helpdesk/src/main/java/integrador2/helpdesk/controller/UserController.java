package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.NomeDTO;
import integrador2.helpdesk.dto.PreferenciasNotificacaoDTO;
import integrador2.helpdesk.dto.SenhaUpdateDTO;
import integrador2.helpdesk.model.User;
import integrador2.helpdesk.repository.UserRepository;
import integrador2.helpdesk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final UserService userService;

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

    @PutMapping("/nome")
    public ResponseEntity<Void> updateName(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestBody @Valid NomeDTO body) {

        userService.updateName(principal.getUsername(), body.getNome());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nome")
    public ResponseEntity<NomeDTO> getName(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        String nome = userService.getName(principal.getUsername());
        return ResponseEntity.ok(new NomeDTO(nome));
    }

    @PutMapping("/senha")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestBody SenhaUpdateDTO dto) {

        boolean updated = userService.updatePassword(
                principal.getUsername(),
                dto.getSenhaAtual(),
                dto.getNovaSenha()
        );

        if (!updated) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.noContent().build();
    }
}