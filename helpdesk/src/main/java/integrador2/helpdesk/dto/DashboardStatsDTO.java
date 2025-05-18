package integrador2.helpdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalChamados;
    private long chamadosAbertos;
    private long chamadosEmAnalise;
    private long chamadosEmAtendimento;
    private long chamadosAguardandoCliente;
    private long chamadosResolvidos;
    private long chamadosFechados;
    private long chamadosBaixa;
    private long chamadosMedia;
    private long chamadosAlta;
    private double slaConformidade;
    private long usuariosAtivos;
}