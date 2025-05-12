// TicketRequest.java
package integrador2.helpdesk.dto;

import integrador2.helpdesk.enums.Priority;
import lombok.Data;

@Data
public class TicketRequest {
    private String   titulo;
    private String   descricao;
    private Long     categoriaId;
    private Priority prioridade;
}
