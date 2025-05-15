package integrador2.helpdesk.model;

import integrador2.helpdesk.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "historico_chamado")
public class TicketHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) private Ticket ticket;
    @ManyToOne(optional = false) private User   usuario;

    @Enumerated(EnumType.STRING) private Status deStatus;
    @Enumerated(EnumType.STRING) private Status paraStatus;

    private String acao;
    @Builder.Default private Instant criadoEm = Instant.now();
}
