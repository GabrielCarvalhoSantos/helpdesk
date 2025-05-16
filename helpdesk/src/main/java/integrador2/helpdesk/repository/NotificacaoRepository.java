package integrador2.helpdesk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import integrador2.helpdesk.model.Notificacao;
import integrador2.helpdesk.model.User;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByDestinatarioOrderByCriadaEmDesc(User destinatario);
}
