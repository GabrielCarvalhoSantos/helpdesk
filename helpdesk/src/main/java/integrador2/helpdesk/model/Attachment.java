package integrador2.helpdesk.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "anexo")
public class Attachment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) private Ticket ticket;
    @ManyToOne(optional = false) private User   usuario;   // quem enviou

    @Column(nullable = false) private String nomeArquivo;
    @Column(nullable = false) private String caminhoArquivo;
    @Column(nullable = false) private Long   tamanhoBytes;

    @Builder.Default private Instant uploadedAt = Instant.now();
}
