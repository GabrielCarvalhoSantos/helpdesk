package integrador2.helpdesk.dto;

import integrador2.helpdesk.enums.UserType;
import integrador2.helpdesk.model.User;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class UserDTO {
    private Long id;
    private String nome;
    private String email;
    private UserType tipo;
    private Boolean ativo;

    public static UserDTO of(User u){
        return new UserDTO(u.getId(),u.getNome(),u.getEmail(),u.getTipo(),u.getAtivo());
    }
}