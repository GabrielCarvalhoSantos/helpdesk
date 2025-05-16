/* ---------- model/Department.java ---------- */
package integrador2.helpdesk.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="departamento")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Department {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String nome;
    @Builder.Default private Boolean ativo = true;
}
