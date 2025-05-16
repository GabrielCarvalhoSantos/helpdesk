package integrador2.helpdesk.dto;

import lombok.Data;

@Data
public class PreferenciasNotificacaoDTO {
    private boolean notificarAtualizacao;
    private boolean notificarFechamento;
    private boolean notificarPorEmail;
}
