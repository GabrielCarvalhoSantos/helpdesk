package integrador2.helpdesk.dto;

import integrador2.helpdesk.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NovoUsuarioDTO {
    @NotBlank private String nome;
    @Email    private String email;
    @NotBlank private String senha;
    private UserType tipo;
}