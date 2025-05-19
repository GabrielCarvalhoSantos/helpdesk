package integrador2.helpdesk.service;

import integrador2.helpdesk.enums.Priority;
import integrador2.helpdesk.enums.Status;
import integrador2.helpdesk.enums.UserType;
import integrador2.helpdesk.model.User;
import integrador2.helpdesk.repository.TicketRepository;
import integrador2.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerAlertScheduler {

    private final TicketRepository ticketRepo;
    private final UserRepository userRepo;
    private final EmailService emailService;

    // roda a cada 15 minutos
    @Scheduled(fixedRate = 900_000)
    public void checkAlerts() {
        List<User> gestores = userRepo.findByTipo(UserType.GESTOR);

        Instant now = Instant.now();

        // 1) SLA em 1/3 restante
        ticketRepo.findByStatusNotIn(List.of(Status.RESOLVIDO, Status.FECHADO))
                .stream()
                .filter(t -> t.getPrazoSla() != null)
                .forEach(t -> {
                    Duration total = Duration.between(t.getAbertoEm(), t.getPrazoSla());
                    Duration remaining = Duration.between(now, t.getPrazoSla());
                    if (!t.getStatus().equals(Status.RESOLVIDO) &&
                            remaining.toMillis() <= total.toMillis() / 3 &&
                            remaining.toMillis() > 0) {
                        String msg = String.format(
                                "Chamado #%d está com %d minutos restantes (>=2/3 do SLA usado)",
                                t.getId(), remaining.toMinutes()
                        );
                        gestores.forEach(g ->
                                emailService.enviarMensagem(
                                        g.getEmail(),
                                        "Alerta de SLA próximo do fim",
                                        msg
                                )
                        );
                    }
                });

        // 2) Alta prioridade sem técnico há >= 3h
        ticketRepo.findByPrioridadeAndTecnicoIsNull(Priority.ALTA)
                .forEach(t -> {
                    Duration sinceOpen = Duration.between(t.getAbertoEm(), now);
                    if (sinceOpen.toHours() >= 3) {
                        String msg = String.format(
                                "Chamado ALTA prioridade #%d sem técnico há %d horas",
                                t.getId(), sinceOpen.toHours()
                        );
                        gestores.forEach(g ->
                                emailService.enviarMensagem(
                                        g.getEmail(),
                                        "Alerta: chamado ALTA sem técnico",
                                        msg
                                )
                        );
                    }
                });
    }
}
