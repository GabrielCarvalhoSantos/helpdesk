package integrador2.helpdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DesempenhoTecnicoDTO {
    private String nomeTecnico;
    private long totalResolvidos;
}
