package integrador2.helpdesk.service;

import integrador2.helpdesk.model.Notificacao;
import integrador2.helpdesk.model.User;
import integrador2.helpdesk.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository repo;
    private final EmailService emailService;


    public void notificar(User destinatario, String mensagem) {
        if (destinatario.isNotificarAtualizacao()) {
            repo.save(Notificacao.builder()
                    .destinatario(destinatario)
                    .mensagem(mensagem)
                    .build());

            if (destinatario.isNotificarPorEmail()) {
                emailService.enviarMensagem(destinatario.getEmail(), "Atualização de chamado", mensagem);
            }
        }
    }

    public void notificarFechamento(User destinatario, String mensagem) {
        if (destinatario.isNotificarFechamento()) {
            repo.save(Notificacao.builder()
                    .destinatario(destinatario)
                    .mensagem(mensagem)
                    .build());

            if (destinatario.isNotificarPorEmail()) {
                emailService.enviarMensagem(destinatario.getEmail(), "Chamado encerrado", mensagem);
            }
        }
    }

    // Notificação por e-mail (ex: envio futuro de email real)
    public void notificarEmail(User destinatario, String mensagem) {
        if (destinatario.isNotificarPorEmail()) {
            // Aqui seria o envio real de email (ex: via JavaMail ou MailService)
            System.out.println("Enviar email para " + destinatario.getEmail() + ": " + mensagem);
        }
    }

    public List<Notificacao> listar(User usuario) {
        return repo.findByDestinatarioOrderByCriadaEmDesc(usuario);
    }

    public void marcarComoLida(Long id) {
        repo.findById(id).ifPresent(n -> {
            n.setLida(true);
            repo.save(n);
        });
    }
}
