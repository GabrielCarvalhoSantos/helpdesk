package integrador2.helpdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DesempenhoTecnicoDTO {
    private String nome;
    private long atribuídos;
    private long resolvidos;
    private String taxaResolucao;
    private double mediaHoras;
    private String classificacao;
}

