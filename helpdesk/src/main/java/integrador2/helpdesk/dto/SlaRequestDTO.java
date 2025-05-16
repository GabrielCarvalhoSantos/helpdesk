package integrador2.helpdesk.dto;

import integrador2.helpdesk.enums.Priority;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SlaRequestDTO {

    @NotNull
    private Long categoriaId;

    @NotNull
    private Priority prioridade;

    @NotNull
    @Min(1)
    private Integer minutosResolucao;
}
