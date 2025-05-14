package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.*;
import integrador2.helpdesk.model.User;
import integrador2.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('TECNICO')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository repo;
    private final BCryptPasswordEncoder enc;

    @GetMapping
    public Page<UserDTO> list(Pageable p){
        return repo.findAll(p).map(UserDTO::of);
    }

    @PostMapping
    public UserDTO create(@RequestBody NovoUsuarioDTO d){
        User u = User.builder()
                .nome(d.getNome())
                .email(d.getEmail())
                .senhaHash(enc.encode(d.getSenha()))
                .tipo(d.getTipo())
                .ativo(true)
                .build();
        return UserDTO.of(repo.save(u));
    }

    @PutMapping("/{id}/status")
    public void ativar(@PathVariable Long id, @RequestParam boolean ativo){
        repo.findById(id).ifPresent(u -> { u.setAtivo(ativo); repo.save(u); });
    }
}
