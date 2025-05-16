package integrador2.helpdesk.model;

import integrador2.helpdesk.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "usuario")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private UserType tipo;               // CLIENTE ou TECNICO

    @Column(nullable = false)
    private boolean notificarAtualizacao = true;

    @Column(nullable = false)
    private boolean notificarFechamento = true;

    @Column(nullable = false)
    private boolean notificarPorEmail = true;

    @Builder.Default
    private Boolean ativo = false;
}
