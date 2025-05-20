package integrador2.helpdesk;

import integrador2.helpdesk.enums.UserType;
import integrador2.helpdesk.model.Notificacao;
import integrador2.helpdesk.model.User;
import integrador2.helpdesk.repository.NotificacaoRepository;
import integrador2.helpdesk.service.NotificacaoService;
import integrador2.helpdesk.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private NotificacaoRepository notificacaoRepo;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private NotificacaoService service;

    private User destinatario;

    @BeforeEach
    void setUp() {
        destinatario = User.builder()
                .id(1L)
                .nome("User")
                .email("user@example.com")
                .tipo(UserType.CLIENTE)
                .notificarAtualizacao(true)
                .notificarFechamento(true)
                .notificarPorEmail(true)
                .build();
    }

    @Test
    void notificar_savesAndEmails_whenPreferencesAllow() {
        service.notificar(destinatario, "Test message");

        ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
        verify(notificacaoRepo).save(captor.capture());
        Notificacao saved = captor.getValue();
        assertEquals(destinatario, saved.getDestinatario());
        assertEquals("Test message", saved.getMensagem());
        assertFalse(saved.getLida());

        verify(emailService).enviarMensagem(
                eq("user@example.com"),
                eq("Atualização de chamado"),
                eq("Test message")
        );
    }

    @Test
    void notificar_doesNothing_whenAtuAtualizacaoDisabled() {
        destinatario.setNotificarAtualizacao(false);
        service.notificar(destinatario, "Ignored");

        verifyNoInteractions(notificacaoRepo, emailService);
    }

    @Test
    void notificarFechamento_savesAndEmails_whenPreferencesAllow() {
        service.notificarFechamento(destinatario, "Closed");

        ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
        verify(notificacaoRepo).save(captor.capture());
        Notificacao saved = captor.getValue();
        assertEquals("Closed", saved.getMensagem());

        verify(emailService).enviarMensagem(
                eq("user@example.com"),
                eq("Chamado encerrado"),
                eq("Closed")
        );
    }

    @Test
    void notificarFechamento_doesNothing_whenFechamentoDisabled() {
        destinatario.setNotificarFechamento(false);
        service.notificarFechamento(destinatario, "Ignored");

        verifyNoInteractions(notificacaoRepo, emailService);
    }

    @Test
    void listar_returnsOrderedNotifications() {
        Notificacao n1 = Notificacao.builder().id(10L).mensagem("A").build();
        Notificacao n2 = Notificacao.builder().id(11L).mensagem("B").build();
        when(notificacaoRepo.findByDestinatarioOrderByCriadaEmDesc(destinatario))
                .thenReturn(List.of(n2, n1));

        var list = service.listar(destinatario);
        assertEquals(2, list.size());
        assertSame(n2, list.get(0));
        assertSame(n1, list.get(1));
        verify(notificacaoRepo).findByDestinatarioOrderByCriadaEmDesc(destinatario);
    }

    @Test
    void marcarComoLida_updatesFlag() {
        Notificacao n = Notificacao.builder().id(20L).lida(false).build();
        when(notificacaoRepo.findById(20L)).thenReturn(Optional.of(n));

        service.marcarComoLida(20L);

        assertTrue(n.getLida());
        verify(notificacaoRepo).save(n);
    }
}
