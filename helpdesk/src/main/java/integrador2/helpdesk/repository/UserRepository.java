// UserRepository.java
package integrador2.helpdesk.repository;

import java.util.List;
import java.util.Optional;

import integrador2.helpdesk.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import integrador2.helpdesk.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);          // login
    boolean existsByEmail(String email);
    List<User> findByTipo(UserType tipo);
}
