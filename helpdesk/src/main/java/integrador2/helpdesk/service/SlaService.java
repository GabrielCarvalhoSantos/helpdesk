package integrador2.helpdesk.service;

import integrador2.helpdesk.dto.SlaBatchDTO;
import integrador2.helpdesk.dto.SlaRequestDTO;
import integrador2.helpdesk.dto.SlaResponseDTO;
import integrador2.helpdesk.enums.Priority;
import integrador2.helpdesk.model.Category;
import integrador2.helpdesk.model.Sla;
import integrador2.helpdesk.repository.CategoryRepository;
import integrador2.helpdesk.repository.SlaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlaService {

    private final SlaRepository slaRepo;
    private final CategoryRepository categoriaRepo;

    public Instant calcularPrazo(Category categoria, Priority prioridade) {
        return slaRepo.findByCategoriaAndPrioridade(categoria, prioridade)
                .map(sla -> Instant.now()
                        .plus(sla.getMinutosResolucao(), ChronoUnit.MINUTES))
                .orElse(null);
    }

    @Transactional
    public void salvarOuAtualizar(SlaRequestDTO dto) {
        var categoria = categoriaRepo.findById(dto.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        var existente = slaRepo.findByCategoriaAndPrioridade(categoria, dto.getPrioridade());

        Sla sla = existente.orElse(Sla.builder()
                .categoria(categoria)
                .prioridade(dto.getPrioridade())
                .build());

        sla.setMinutosResolucao(dto.getMinutosResolucao());
        slaRepo.save(sla);
    }

    public List<SlaResponseDTO> listarTodos() {
        return slaRepo.findAll().stream()
                .map(sla -> SlaResponseDTO.builder()
                        .categoriaId(sla.getCategoria().getId())
                        .prioridade(sla.getPrioridade())
                        .minutosResolucao(sla.getMinutosResolucao())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void salvarBatch(List<SlaBatchDTO.Item> items) {
        for (var dto : items) {
            var categoria = categoriaRepo.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

            var existente = slaRepo.findByCategoriaAndPrioridade(categoria, dto.getPrioridade());

            Sla sla = existente.orElse(Sla.builder()
                    .categoria(categoria)
                    .prioridade(dto.getPrioridade())
                    .build());

            sla.setMinutosResolucao(dto.getMinutosResolucao());
            slaRepo.save(sla);
        }
    }


}
