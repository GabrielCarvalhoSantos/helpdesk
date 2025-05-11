// SlaRepository.java
package integrador2.helpdesk.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import integrador2.helpdesk.enums.Priority;
import integrador2.helpdesk.model.Category;
import integrador2.helpdesk.model.Sla;

public interface SlaRepository extends JpaRepository<Sla, Long> {
    Optional<Sla> findByCategoriaAndPrioridade(Category categoria, Priority prioridade);
}
