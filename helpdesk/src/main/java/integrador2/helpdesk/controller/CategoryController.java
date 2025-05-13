// src/main/java/.../controller/CategoryController.java
package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.*;
import integrador2.helpdesk.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @GetMapping
    public List<CategoryResponse> listar() {
        return service.listarTodas();
    }

    @PostMapping
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<CategoryResponse> criar(@RequestBody CategoryRequest req) {
        return ResponseEntity.status(201).body(service.criar(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TECNICO')")
    public CategoryResponse atualizar(@PathVariable Long id,
                                      @RequestBody CategoryRequest req) {
        return service.atualizar(id, req);
    }
}
