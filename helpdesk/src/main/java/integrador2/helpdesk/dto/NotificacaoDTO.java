package integrador2.helpdesk.dto;

import integrador2.helpdesk.model.Notificacao;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class NotificacaoDTO {
    private Long id;
    private String mensagem;
    private Boolean lida;
    private Instant criadaEm;

    public static NotificacaoDTO of(Notificacao n) {
        return NotificacaoDTO.builder()
                .id(n.getId())
                .mensagem(n.getMensagem())
                .lida(n.getLida())
                .criadaEm(n.getCriadaEm())
                .build();
    }
}
