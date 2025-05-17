package integrador2.helpdesk.model;

import integrador2.helpdesk.enums.Priority;
import integrador2.helpdesk.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "chamado")
public class Ticket {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;
    @Column(nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Priority prioridade;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ABERTO;

    @ManyToOne(optional = false)
    private Category categoria;
    @ManyToOne(optional = false)
    private User cliente;
    @ManyToOne
    private User tecnico;   // nulo até atribuir

    private Instant prazoSla;
    @Builder.Default private Instant abertoEm = Instant.now();
    private Instant fechadoEm;

    @ManyToOne(optional = false)
    @JoinColumn(name = "departamento_id")   // ← garante o nome da coluna
    private Department departamento;

    @Column(name = "assumido_em")
    private Instant assumidoEm;

    /** atualiza timestamp sempre que salvar */
    @PreUpdate
    private void onUpdate() { this.fechadoEm = (status == Status.FECHADO ? Instant.now() : fechadoEm); }
}
