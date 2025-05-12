package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.LoginRequest;
import integrador2.helpdesk.dto.LoginResponse;
import integrador2.helpdesk.dto.VerifyDTO;
import integrador2.helpdesk.enums.UserType;
import integrador2.helpdesk.model.User;
import integrador2.helpdesk.model.VerificationCode;
import integrador2.helpdesk.repository.UserRepository;
import integrador2.helpdesk.repository.VerificationCodeRepository;
import integrador2.helpdesk.security.JwtUtil;
import integrador2.helpdesk.service.EmailService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.Instant;

/* …imports… */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository             userRepo;
    private final VerificationCodeRepository codeRepo;
    private final PasswordEncoder            encoder;
    private final EmailService               emailService;
    private final AuthenticationManager      authMgr;
    private final JwtUtil                    jwtUtil;

    /* ---------- REGISTRO ---------- */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid LoginRequest req) {

        String email = req.getEmail().toLowerCase();
        if (!email.endsWith("@gmail.com"))
            return ResponseEntity.badRequest().body("Apenas contas @gmail.com são permitidas");
        if (userRepo.existsByEmail(email))
            return ResponseEntity.badRequest().body("E‑mail já cadastrado");

        /* gera e salva código */
        String codigo = "%06d".formatted(new SecureRandom().nextInt(1_000_000));
        codeRepo.deleteByEmail(email);
        codeRepo.save(VerificationCode.builder()
                .email(email)
                .code(codigo)
                .expiresAt(Instant.now().plusSeconds(900))
                .build());

        /* cria usuário inativo */
        userRepo.save(User.builder()
                .nome(email)
                .email(email)
                .senhaHash(encoder.encode(req.getSenha()))
                .tipo(UserType.CLIENTE)
                .ativo(false)
                .build());

        emailService.enviarCodigo(email, codigo);
        return ResponseEntity.ok("Código enviado. Use /auth/verify-code.");
    }

    /* ---------- VERIFICA ---------- */
    @PostMapping("/verify-code")
    @Transactional
    public ResponseEntity<String> verify(@RequestBody @Valid VerifyDTO dto) {

        VerificationCode vc = codeRepo.findByEmailAndCode(dto.getEmail(), dto.getCodigo())
                .filter(c -> c.getExpiresAt().isAfter(Instant.now()))
                .orElse(null);

        if (vc == null)
            return ResponseEntity.badRequest().body("Código inválido ou expirado");

        userRepo.findByEmail(dto.getEmail())
                .ifPresent(u -> u.setAtivo(true));      // ativa conta

        codeRepo.delete(vc);
        return ResponseEntity.ok("Conta verificada! Faça login.");
    }

    /* ---------- LOGIN ---------- */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
        authMgr.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getSenha()));
        return ResponseEntity.ok(new LoginResponse(jwtUtil.generate(req.getEmail())));
    }
}
