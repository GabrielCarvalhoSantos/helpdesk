package integrador2.helpdesk.repository;

import integrador2.helpdesk.dto.DashboardStatsDTO;
import integrador2.helpdesk.dto.DesempenhoTecnicoDTO;
import integrador2.helpdesk.dto.EstatisticaItemDTO;
import integrador2.helpdesk.dto.TechnicianPerformanceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StatisticsRepository {

    private final JdbcTemplate jdbc;

    public Object[] getTicketCounts() {
        return jdbc.query("SELECT * FROM fn_ticket_counts()", rs -> {
            rs.next();
            return new Object[]{
                    rs.getLong("total"),
                    rs.getLong("abertos"),
                    rs.getLong("em_analise"),
                    rs.getLong("em_atendimento"),
                    rs.getLong("aguardando_cliente"),
                    rs.getLong("resolvidos"),
                    rs.getLong("fechados")
            };
        });
    }

    public Object[] getActiveUsers() {
        return jdbc.query("SELECT * FROM fn_active_users()", rs -> {
            rs.next();
            return new Object[]{
                    rs.getLong("total"),
                    rs.getLong("clientes"),
                    rs.getLong("tecnicos"),
                    rs.getLong("gestores")
            };
        });
    }

    public Double getSlaCompliance() {
        return jdbc.queryForObject("SELECT fn_sla_compliance()", Double.class);
    }

    public List<EstatisticaItemDTO> getAvgResolutionTimeByCategory() {
        return jdbc.query("SELECT * FROM mv_resolution_time", (rs, rowNum) ->
                new EstatisticaItemDTO(
                        rs.getString("categoria_nome"),
                        rs.getDouble("valor")
                )
        );
    }

    public List<TechnicianPerformanceDTO> getTechnicianPerformance() {
        return jdbc.query("SELECT * FROM mv_technician_performance", (rs, rowNum) ->
                new TechnicianPerformanceDTO(
                        rs.getString("nome"),
                        rs.getLong("atribuidos"),
                        rs.getLong("resolvidos"),
                        rs.getString("taxa"),
                        rs.getBigDecimal("avg_hours"),
                        rs.getString("classificacao")
                )
        );
    }

    public DashboardStatsDTO getDashboardGestor() {
        return jdbc.queryForObject(
                "SELECT * FROM mv_dashboard_stats",
                (rs, rowNum) -> new DashboardStatsDTO(
                        rs.getLong("total_chamados"),
                        rs.getLong("chamados_abertos"),
                        rs.getLong("chamados_em_analise"),
                        rs.getLong("chamados_em_atendimento"),
                        rs.getLong("chamados_aguardando_cliente"),
                        rs.getLong("chamados_resolvidos"),
                        rs.getLong("chamados_fechados"),
                        rs.getLong("chamados_baixa"),
                        rs.getLong("chamados_media"),
                        rs.getLong("chamados_alta"),
                        rs.getDouble("sla_conformidade"),
                        rs.getLong("usuarios_ativos")
                )
        );
    }

    public List<Map<String, Object>> getCategoryDistribution() {
        return jdbc.query("SELECT * FROM mv_category_distribution", (rs, rowNum) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("nome", rs.getString("categoria_nome"));
            item.put("quantidade", rs.getLong("quantidade"));
            item.put("percentual", rs.getDouble("percentual"));
            return item;
        });
    }
}

