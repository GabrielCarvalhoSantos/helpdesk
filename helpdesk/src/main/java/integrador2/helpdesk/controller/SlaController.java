package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.SlaBatchDTO;
import integrador2.helpdesk.dto.SlaRequestDTO;
import integrador2.helpdesk.dto.SlaResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import integrador2.helpdesk.service.SlaService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/sla")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GESTOR')")
public class SlaController {

    private final SlaService service;

    @PostMapping
    public void salvar(@RequestBody @Valid SlaRequestDTO dto) {
        service .salvarOuAtualizar(dto);
    }

    @GetMapping
    public List<SlaResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @PostMapping("/batch")
    public void salvarBatch(@RequestBody @Valid SlaBatchDTO dto) {
        service.salvarBatch(dto.getSlas());
    }

}
