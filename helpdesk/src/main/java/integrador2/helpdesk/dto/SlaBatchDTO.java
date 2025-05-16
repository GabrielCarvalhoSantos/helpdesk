package integrador2.helpdesk.dto;

import integrador2.helpdesk.enums.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SlaBatchDTO {
    @Data
    public static class Item {
        @NotNull private Long categoriaId;
        @NotNull private Priority prioridade;
        @NotNull private Integer minutosResolucao;
    }

    @NotNull
    private List<Item> slas;
}
