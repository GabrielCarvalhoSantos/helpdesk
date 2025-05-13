package integrador2.helpdesk.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CategoryResponse {
    private Long    id;
    private String  nome;
    private Boolean ativo;
}