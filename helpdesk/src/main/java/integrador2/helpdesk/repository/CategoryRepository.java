// CategoryRepository.java
package integrador2.helpdesk.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import integrador2.helpdesk.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNomeIgnoreCase(String nome);
}
