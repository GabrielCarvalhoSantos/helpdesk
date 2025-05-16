package integrador2.helpdesk.model;

import jakarta.persistence.*;

@Entity
public class PreferenciasNotificacao {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private User user;

    private boolean atualizacaoChamado = true;
    private boolean fechamentoChamado = true;
    private boolean notificacaoEmail = true;
}

