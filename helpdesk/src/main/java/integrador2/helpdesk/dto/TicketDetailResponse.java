// src/main/java/integrador2/helpdesk/dto/TicketDetailResponse.java

package integrador2.helpdesk.dto;

import integrador2.helpdesk.enums.Priority;
import integrador2.helpdesk.enums.Status;
import integrador2.helpdesk.model.Ticket;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TicketDetailResponse {
    private Long id;
    private String titulo;
    private String descricao;
    private Priority prioridade;
    private Status status;
    private String categoria;
    private String departamento;
    private UserDTO cliente;
    private UserDTO tecnico;
    private Instant abertoEm;
    private Instant prazoSla;
    private Instant fechadoEm;

    public static TicketDetailResponse fromTicket(Ticket ticket) {
        return TicketDetailResponse.builder()
                .id(ticket.getId())
                .titulo(ticket.getTitulo())
                .descricao(ticket.getDescricao())
                .prioridade(ticket.getPrioridade())
                .status(ticket.getStatus())
                .categoria(ticket.getCategoria().getNome())
                .departamento(ticket.getDepartamento().getNome())
                .cliente(UserDTO.of(ticket.getCliente()))
                .tecnico(ticket.getTecnico() != null ? UserDTO.of(ticket.getTecnico()) : null)
                .abertoEm(ticket.getAbertoEm())
                .prazoSla(ticket.getPrazoSla())
                .fechadoEm(ticket.getFechadoEm())
                .build();
    }
}