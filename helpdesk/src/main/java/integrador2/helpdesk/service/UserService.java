package integrador2.helpdesk.service;

import integrador2.helpdesk.dto.NomeDTO;
import integrador2.helpdesk.dto.SenhaUpdateDTO;
import integrador2.helpdesk.model.User;
import integrador2.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * Busca um usuário pelo email
     * @param email Email do usuário
     * @return O usuário encontrado
     * @throws IllegalArgumentException se o usuário não for encontrado
     */
    public User findUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    /**
     * Atualiza o nome do usuário
     * @param email Email do usuário
     * @param nome Novo nome
     * @return O usuário atualizado
     */
    @Transactional
    public User updateName(String email, String nome) {
        User user = findUserByEmail(email);
        user.setNome(nome);
        return userRepo.save(user);
    }

    /**
     * Obtém o nome do usuário
     * @param email Email do usuário
     * @return Nome do usuário
     */
    public String getName(String email) {
        User user = findUserByEmail(email);
        return user.getNome();
    }

    /**
     * Atualiza a senha do usuário
     * @param email Email do usuário
     * @param senhaAtual Senha atual
     * @param novaSenha Nova senha
     * @return true se a senha foi atualizada, false caso contrário
     */
    @Transactional
    public boolean updatePassword(String email, String senhaAtual, String novaSenha) {
        User user = findUserByEmail(email);

        // Verificar se a senha atual está correta
        if (!passwordEncoder.matches(senhaAtual, user.getSenhaHash())) {
            return false; // Senha atual incorreta
        }

        // Atualizar com a nova senha
        user.setSenhaHash(passwordEncoder.encode(novaSenha));
        userRepo.save(user);

        return true;
    }
}