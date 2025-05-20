package integrador2.helpdesk.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsSchedulerService.class);
    private final JdbcTemplate jdbcTemplate;

    /**
     * Atualiza as views materializadas de estatísticas a cada 3 minutos
     */
    @Scheduled(fixedRate = 100000)
    public void refreshStatisticsViews() {
        try {
            logger.info("Iniciando atualização das views materializadas de estatísticas");

            // Registrar início da atualização
            jdbcTemplate.update(
                    "INSERT INTO statistics_refresh_log (refreshed_at, success, error_message) VALUES (NOW(), false, 'Em andamento')"
            );

            long startTime = System.currentTimeMillis();

            // Atualizar cada view em sequência
            jdbcTemplate.execute("REFRESH MATERIALIZED VIEW mv_dashboard_stats");
            jdbcTemplate.execute("REFRESH MATERIALIZED VIEW mv_category_distribution");
            jdbcTemplate.execute("REFRESH MATERIALIZED VIEW mv_resolution_time");
            jdbcTemplate.execute("REFRESH MATERIALIZED VIEW mv_technician_performance");

            long duration = System.currentTimeMillis() - startTime;

            // Atualizar o registro de log com sucesso
            jdbcTemplate.update(
                    "UPDATE statistics_refresh_log SET success = true, error_message = ? WHERE success = false AND error_message = 'Em andamento'",
                    "Concluído em " + duration + "ms"
            );

            logger.info("Views materializadas de estatísticas atualizadas com sucesso em {}ms", duration);
        } catch (Exception e) {
            logger.error("Erro ao atualizar views materializadas: {}", e.getMessage(), e);

            // Registrar falha
            try {
                jdbcTemplate.update(
                        "UPDATE statistics_refresh_log SET success = false, error_message = ? WHERE success = false AND error_message = 'Em andamento'",
                        "Erro: " + e.getMessage()
                );
            } catch (Exception ex) {
                logger.error("Erro ao registrar falha: {}", ex.getMessage());
            }
        }
    }

    /**
     * Atualiza as views imediatamente na inicialização da aplicação
     */
    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE) // Executa uma vez, 10 segundos após o início
    public void initialRefresh() {
        logger.info("Executando atualização inicial das estatísticas");
        refreshStatisticsViews();
    }
}