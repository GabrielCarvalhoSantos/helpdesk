// TicketResponse.java
package integrador2.helpdesk.dto;

import integrador2.helpdesk.enums.*;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data @Builder
public class TicketResponse {
    private Long      id;
    private String    titulo;
    private Priority  prioridade;
    private Status    status;
    private String    categoria;
    private Instant   abertoEm;
    private Instant   prazoSla;
}
