package integrador2.helpdesk.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianPerformanceDTO {
    private String nome;
    private long atribuidos;
    private long resolvidos;
    private String taxa;         // String para "50%"
    private BigDecimal avgHours; // numeric
    private String classificacao;
}
