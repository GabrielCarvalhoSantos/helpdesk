package integrador2.helpdesk;

import integrador2.helpdesk.dto.SlaBatchDTO;
import integrador2.helpdesk.dto.SlaRequestDTO;
import integrador2.helpdesk.dto.SlaResponseDTO;
import integrador2.helpdesk.enums.Priority;
import integrador2.helpdesk.model.Category;
import integrador2.helpdesk.model.Sla;
import integrador2.helpdesk.repository.CategoryRepository;
import integrador2.helpdesk.repository.SlaRepository;
import integrador2.helpdesk.service.SlaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlaServiceTest {

    @Mock
    private SlaRepository slaRepo;

    @Mock
    private CategoryRepository categoryRepo;

    @InjectMocks
    private SlaService slaService;

    private final Category category = Category.builder()
            .id(1L)
            .nome("Hardware")
            .build();

    @Test
    void calcularPrazo_returnsInstantPlusMinutes_whenSlaExists() {
        Sla sla = Sla.builder()
                .categoria(category)
                .prioridade(Priority.ALTA)
                .minutosResolucao(30)
                .build();
        when(slaRepo.findByCategoriaAndPrioridade(category, Priority.ALTA))
                .thenReturn(Optional.of(sla));

        Instant before = Instant.now();
        Instant result = slaService.calcularPrazo(category, Priority.ALTA);
        Instant after = Instant.now();

        assertNotNull(result);
        assertFalse(result.isBefore(before.plus(30, ChronoUnit.MINUTES)));
        assertFalse(result.isAfter(after.plus(30, ChronoUnit.MINUTES)));
    }

    @Test
    void calcularPrazo_returnsNull_whenNoSlaFound() {
        when(slaRepo.findByCategoriaAndPrioridade(category, Priority.MEDIA))
                .thenReturn(Optional.empty());

        assertNull(slaService.calcularPrazo(category, Priority.MEDIA));
    }

    @Test
    void listarTodos_mapsEntitiesToDtoList() {
        Sla s1 = Sla.builder()
                .id(1L)
                .categoria(category)
                .prioridade(Priority.ALTA)
                .minutosResolucao(30)
                .build();
        Sla s2 = Sla.builder()
                .id(2L)
                .categoria(category)
                .prioridade(Priority.MEDIA)
                .minutosResolucao(60)
                .build();
        when(slaRepo.findAll()).thenReturn(List.of(s1, s2));

        List<SlaResponseDTO> list = slaService.listarTodos();

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getCategoriaId());
        assertEquals(Priority.ALTA, list.get(0).getPrioridade());
        assertEquals(30, list.get(0).getMinutosResolucao());
        assertEquals(Priority.MEDIA, list.get(1).getPrioridade());
    }

    @Test
    void salvarOuAtualizar_updatesExistingSla() {
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));

        Sla existing = Sla.builder()
                .id(1L)
                .categoria(category)
                .prioridade(Priority.BAIXA)
                .minutosResolucao(10)
                .build();
        when(slaRepo.findByCategoriaAndPrioridade(category, Priority.BAIXA))
                .thenReturn(Optional.of(existing));

        SlaRequestDTO dto = new SlaRequestDTO();
        dto.setCategoriaId(1L);
        dto.setPrioridade(Priority.BAIXA);
        dto.setMinutosResolucao(20);

        slaService.salvarOuAtualizar(dto);

        assertEquals(20, existing.getMinutosResolucao());
        verify(slaRepo).save(existing);
    }

    @Test
    void salvarOuAtualizar_createsNewSla_whenNoneExists() {
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
        when(slaRepo.findByCategoriaAndPrioridade(category, Priority.ALTA))
                .thenReturn(Optional.empty());

        SlaRequestDTO dto = new SlaRequestDTO();
        dto.setCategoriaId(1L);
        dto.setPrioridade(Priority.ALTA);
        dto.setMinutosResolucao(15);

        slaService.salvarOuAtualizar(dto);

        ArgumentCaptor<Sla> captor = ArgumentCaptor.forClass(Sla.class);
        verify(slaRepo).save(captor.capture());
        Sla saved = captor.getValue();
        assertEquals(category, saved.getCategoria());
        assertEquals(Priority.ALTA, saved.getPrioridade());
        assertEquals(15, saved.getMinutosResolucao());
    }

    @Test
    void salvarBatch_savesEachItem() {
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
        when(slaRepo.findByCategoriaAndPrioridade(category, Priority.BAIXA))
                .thenReturn(Optional.empty());
        when(slaRepo.findByCategoriaAndPrioridade(category, Priority.MEDIA))
                .thenReturn(Optional.empty());

        SlaBatchDTO.Item it1 = new SlaBatchDTO.Item();
        it1.setCategoriaId(1L);
        it1.setPrioridade(Priority.BAIXA);
        it1.setMinutosResolucao(5);
        SlaBatchDTO.Item it2 = new SlaBatchDTO.Item();
        it2.setCategoriaId(1L);
        it2.setPrioridade(Priority.MEDIA);
        it2.setMinutosResolucao(10);

        slaService.salvarBatch(List.of(it1, it2));

        verify(slaRepo, times(2)).save(any(Sla.class));
    }
}
