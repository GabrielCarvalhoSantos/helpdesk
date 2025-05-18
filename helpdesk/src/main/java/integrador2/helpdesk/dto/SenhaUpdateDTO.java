package integrador2.helpdesk.dto;

import lombok.Data;

@Data
public class SenhaUpdateDTO {
    private String senhaAtual;
    private String novaSenha;
}