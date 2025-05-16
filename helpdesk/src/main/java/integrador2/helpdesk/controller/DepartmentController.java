package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.DepartmentDTO;
import integrador2.helpdesk.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departamentos")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService svc;

    @GetMapping
    public List<DepartmentDTO> list() {
        return svc.list();
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public DepartmentDTO create(@RequestBody DepartmentDTO d) {
        return svc.create(d);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public DepartmentDTO update(@PathVariable Long id,
                                @RequestBody DepartmentDTO d) {
        return svc.update(id, d);
    }
}
