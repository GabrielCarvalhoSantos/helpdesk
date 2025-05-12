package integrador2.helpdesk.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import integrador2.helpdesk.enums.Priority;
import integrador2.helpdesk.model.Category;
import integrador2.helpdesk.repository.SlaRepository;

@Service
@RequiredArgsConstructor
public class SlaService {

    private final SlaRepository slaRepo;

    /** devolve deadline (Instant) a partir de agora  */
    public Instant calcularPrazo(Category categoria, Priority prioridade) {
        return slaRepo.findByCategoriaAndPrioridade(categoria, prioridade)
                .map(sla -> Instant.now()
                        .plus(sla.getMinutosResolucao(), ChronoUnit.MINUTES))
                .orElse(null);

    }
}
