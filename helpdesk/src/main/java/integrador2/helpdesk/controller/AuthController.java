package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.*;
import integrador2.helpdesk.repository.UserRepository;
import integrador2.helpdesk.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authMgr;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        authMgr.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getSenha()));

        String token = jwtUtil.generate(req.getEmail());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    /** Cadastro simples de Cliente para testes  */
    @PostMapping("/register")
    public void register(@RequestBody LoginRequest req){
        var user = integrador2.helpdesk.model.User.builder()
                .nome(req.getEmail())
                .email(req.getEmail())
                .senhaHash(encoder.encode(req.getSenha()))
                .tipo(integrador2.helpdesk.enums.UserType.CLIENTE)
                .build();
        userRepo.save(user);
    }
}
