package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.LoginRequest;
import integrador2.helpdesk.dto.LoginResponse;
import integrador2.helpdesk.enums.UserType;      // ← enum
import integrador2.helpdesk.model.User;         // ← entidade
import integrador2.helpdesk.repository.UserRepository;
import integrador2.helpdesk.security.JwtUtil;
import integrador2.helpdesk.service.EmailService;
import jakarta.validation.Valid;                // ← @Valid
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
    private final EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        authMgr.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getSenha()));

        String token = jwtUtil.generate(req.getEmail());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    /** Cadastro simples de Cliente para testes  */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid LoginRequest req) {

        if (!req.getEmail().toLowerCase().endsWith("@gmail.com")) {
            return ResponseEntity.badRequest().body("Apenas contas @gmail.com são permitidas");
        }
        if (userRepo.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body("E‑mail já cadastrado");
        }

        User novo = User.builder()
                .nome(req.getEmail())
                .email(req.getEmail())
                .senhaHash(encoder.encode(req.getSenha()))
                .tipo(UserType.CLIENTE)
                .build();
        userRepo.save(novo);

        /* envia e‑mail de boas‑vindas */
        try {
            emailService.enviarBoasVindas(novo.getEmail());
        } catch (jakarta.mail.MessagingException e) {
            // loga e segue – não bloqueia o cadastro
            e.printStackTrace();
        }

        return ResponseEntity.ok("Cadastro realizado. Verifique seu Gmail.");
    }

}
