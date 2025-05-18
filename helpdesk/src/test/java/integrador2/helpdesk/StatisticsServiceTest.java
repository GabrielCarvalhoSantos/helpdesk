package integrador2.helpdesk;

import integrador2.helpdesk.dto.DashboardStatsDTO;
import integrador2.helpdesk.dto.EstatisticaItemDTO;
import integrador2.helpdesk.dto.TechnicianPerformanceDTO;
import integrador2.helpdesk.repository.StatisticsRepository;
import integrador2.helpdesk.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticsServiceTest {

    @Mock
    private StatisticsRepository repo;

    @InjectMocks
    private StatisticsService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDashboardStats_delegates() {
        DashboardStatsDTO dto = new DashboardStatsDTO();
        when(repo.getDashboardGestor()).thenReturn(dto);

        assertSame(dto, service.getDashboardStats());
        verify(repo).getDashboardGestor();
    }

    @Test
    void getTicketCount_unwrapsFirstElement() {
        when(repo.getTicketCounts()).thenReturn(new Object[]{42L, 1L,2L,3L,4L,5L,6L});
        assertEquals(42L, service.getTicketCount());
    }

    @Test
    void getCategoryDistribution_delegatesFully() {
        List<Map<String,Object>> list = List.of(Map.of("nome","X","quantidade",10,"percentual",100.0));
        when(repo.getCategoryDistribution()).thenReturn(list);
        assertSame(list, service.getCategoryDistribution());
    }

    @Test
    void getAvgResolutionTimeByCategory_delegates() {
        List<EstatisticaItemDTO> expected = List.of(new EstatisticaItemDTO("A",1.2));
        when(repo.getAvgResolutionTimeByCategory()).thenReturn(expected);
        assertSame(expected, service.getAvgResolutionTimeByCategory());
    }

    @Test
    void getTechnicianPerformance_delegates() {
        List<TechnicianPerformanceDTO> expected = List.of();
        when(repo.getTechnicianPerformance()).thenReturn(expected);
        assertSame(expected, service.getTechnicianPerformance());
    }

    @Test
    void getActiveClientsCount_unwrapsSecondElement() {
        when(repo.getActiveUsers()).thenReturn(new Object[]{99L, 77L, 2L, 3L});
        assertEquals(77L, service.getActiveClientsCount());
    }

    @Test
    void getSlaCompliance_delegates() {
        when(repo.getSlaCompliance()).thenReturn(88.8);
        assertEquals(88.8, service.getSlaCompliance());
    }
}
