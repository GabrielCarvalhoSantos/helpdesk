package integrador2.helpdesk.service;

import java.util.List;
import java.util.Map;

import integrador2.helpdesk.dto.TechnicianPerformanceDTO;
import org.springframework.stereotype.Service;
import integrador2.helpdesk.dto.DashboardStatsDTO;
import integrador2.helpdesk.dto.DesempenhoTecnicoDTO;
import integrador2.helpdesk.dto.EstatisticaItemDTO;
import integrador2.helpdesk.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statsRepository;

    public DashboardStatsDTO getDashboardStats() {
        return statsRepository.getDashboardGestor();
    }

    public Long getTicketCount() {
        Object[] counts = statsRepository.getTicketCounts();
        if (counts == null || counts.length == 0) return 0L;
        return ((Number) counts[0]).longValue();
    }

    public Long getActiveClientsCount() {
        Object[] users = statsRepository.getActiveUsers();
        if (users == null || users.length < 2) return 0L;
        return ((Number) users[1]).longValue();
    }

    public Double getSlaCompliance() {
        return statsRepository.getSlaCompliance();
    }

    public List<EstatisticaItemDTO> getAvgResolutionTimeByCategory() {
        return statsRepository.getAvgResolutionTimeByCategory();
    }

    public List<TechnicianPerformanceDTO> getTechnicianPerformance() {
        return statsRepository.getTechnicianPerformance();
    }

    public List<Map<String, Object>> getCategoryDistribution() {
        return statsRepository.getCategoryDistribution();
    }


}
