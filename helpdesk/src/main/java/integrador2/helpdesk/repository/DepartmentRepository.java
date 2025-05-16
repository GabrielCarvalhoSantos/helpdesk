package integrador2.helpdesk.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import integrador2.helpdesk.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByAtivoTrue();
}