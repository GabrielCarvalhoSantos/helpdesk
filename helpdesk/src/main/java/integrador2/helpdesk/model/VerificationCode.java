package integrador2.helpdesk.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "verification_code")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VerificationCode {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Builder.Default
    private Instant expiresAt = Instant.now().plus(15, ChronoUnit.MINUTES);
}
