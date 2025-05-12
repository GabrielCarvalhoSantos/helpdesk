// src/main/java/.../service/CategoryService.java
package integrador2.helpdesk.service;

import integrador2.helpdesk.dto.*;
import integrador2.helpdesk.model.Category;
import integrador2.helpdesk.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repo;

    public List<CategoryResponse> listarTodas() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public CategoryResponse criar(CategoryRequest req) {
        if (repo.findByNomeIgnoreCase(req.getNome()).isPresent())
            throw new IllegalArgumentException("Categoria já existe");

        Category c = repo.save(Category.builder()
                .nome(req.getNome())
                .ativo(req.getAtivo())
                .build());
        return toDTO(c);
    }

    @Transactional
    public CategoryResponse atualizar(Long id, CategoryRequest req) {
        Category c = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        c.setNome(req.getNome());
        c.setAtivo(req.getAtivo());
        return toDTO(repo.save(c));
    }

    /* util */
    private CategoryResponse toDTO(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .nome(c.getNome())
                .ativo(c.getAtivo())
                .build();
    }
}
