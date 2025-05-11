// UserRepository.java
package integrador2.helpdesk.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import integrador2.helpdesk.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);          // login
    boolean existsByEmail(String email);
}
