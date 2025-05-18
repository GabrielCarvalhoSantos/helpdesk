package integrador2.helpdesk.controller;

import integrador2.helpdesk.dto.DashboardStatsDTO;
import integrador2.helpdesk.dto.DesempenhoTecnicoDTO;
import integrador2.helpdesk.dto.EstatisticaItemDTO;
import integrador2.helpdesk.dto.TechnicianPerformanceDTO;
import integrador2.helpdesk.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/statistics")
@PreAuthorize("hasRole('GESTOR')")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService service;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(service.getDashboardStats());
    }

    @GetMapping("/ticket-counts")
    public ResponseEntity<Long> getTicketCount() {
        return ResponseEntity.ok(service.getTicketCount());
    }

    @GetMapping("/active-clients")
    public ResponseEntity<Long> getActiveClientsCount() {
        return ResponseEntity.ok(service.getActiveClientsCount());
    }

    @GetMapping("/sla-compliance")
    public ResponseEntity<Double> getSlaCompliance() {
        return ResponseEntity.ok(service.getSlaCompliance());
    }

    @GetMapping("/resolution-time")
    public ResponseEntity<List<EstatisticaItemDTO>> getAvgResolutionTimeByCategory() {
        return ResponseEntity.ok(service.getAvgResolutionTimeByCategory());
    }

    @GetMapping("/technician-performance")
    public ResponseEntity<List<TechnicianPerformanceDTO>> getTechnicianPerformance() {
        return ResponseEntity.ok(service.getTechnicianPerformance());
    }

    @GetMapping("/category-distribution")
    public ResponseEntity<List<Map<String, Object>>> getCategoryDistribution() {
        return ResponseEntity.ok(service.getCategoryDistribution());
    }

}