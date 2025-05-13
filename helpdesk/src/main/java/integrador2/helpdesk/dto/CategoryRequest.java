package integrador2.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank
    private String nome;
    private Boolean ativo = true;   // opcional em update
}