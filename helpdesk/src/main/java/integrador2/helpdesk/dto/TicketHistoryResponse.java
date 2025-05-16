// src/main/java/integrador2/helpdesk/dto/TicketHistoryResponse.java

package integrador2.helpdesk.dto;

import integrador2.helpdesk.enums.Status;
import integrador2.helpdesk.model.TicketHistory;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TicketHistoryResponse {
    private Long id;
    private UserDTO usuario;
    private Status deStatus;
    private Status paraStatus;
    private String acao;
    private Instant criadoEm;

    public static TicketHistoryResponse fromHistory(TicketHistory history) {
        return TicketHistoryResponse.builder()
                .id(history.getId())
                .usuario(UserDTO.of(history.getUsuario()))
                .deStatus(history.getDeStatus())
                .paraStatus(history.getParaStatus())
                .acao(history.getAcao())
                .criadoEm(history.getCriadoEm())
                .build();
    }
}