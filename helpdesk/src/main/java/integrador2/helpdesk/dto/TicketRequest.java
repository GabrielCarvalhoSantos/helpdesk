package integrador2.helpdesk.dto;

import integrador2.helpdesk.enums.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketRequest {

    @NotNull private String   titulo;
    @NotNull private String   descricao;
    @NotNull private Long     categoriaId;
    @NotNull private Long     departamentoId;   // ← NOVO
    @NotNull private Priority prioridade;
}
