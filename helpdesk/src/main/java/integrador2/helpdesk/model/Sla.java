package integrador2.helpdesk.model;

import integrador2.helpdesk.enums.Priority;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "sla")
public class Sla {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Category categoria;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Priority prioridade;

    /** prazo para resolução em minutos */
    @Column(nullable = false)
    private Integer minutosResolucao;
}
