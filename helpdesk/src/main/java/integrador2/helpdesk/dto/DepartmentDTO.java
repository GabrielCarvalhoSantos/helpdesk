package integrador2.helpdesk.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DepartmentDTO {
    private Long    id;
    private String  nome;
    private Boolean ativo;
}
