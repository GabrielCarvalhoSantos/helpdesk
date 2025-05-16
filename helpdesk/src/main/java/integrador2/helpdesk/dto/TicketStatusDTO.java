// TicketStatusDTO.java
package integrador2.helpdesk.dto;

import integrador2.helpdesk.enums.Status;
import lombok.Data;

@Data
public class TicketStatusDTO {
    private Status novoStatus;
}
