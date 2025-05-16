package integrador2.helpdesk.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "notificacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User destinatario;

    @Column(nullable = false)
    private String mensagem;

    @Column(nullable = false)
    @Builder.Default
    private Boolean lida = false;

    @Column(nullable = false)
    @Builder.Default
    private Instant criadaEm = Instant.now();
}
