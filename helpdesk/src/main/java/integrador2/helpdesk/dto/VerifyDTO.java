package integrador2.helpdesk.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class VerifyDTO {
    @Email @NotBlank
    private String email;

    @NotBlank
    private String codigo;
}
