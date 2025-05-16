package integrador2.helpdesk.dto;

import integrador2.helpdesk.enums.Priority;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SlaResponseDTO {
    private Long categoriaId;
    private Priority prioridade;
    private Integer minutosResolucao;
}
