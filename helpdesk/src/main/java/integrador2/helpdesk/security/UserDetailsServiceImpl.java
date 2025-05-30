package integrador2.helpdesk.security;

import integrador2.helpdesk.model.User;
import integrador2.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User u = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (Boolean.FALSE.equals(u.getAtivo()))
            throw new DisabledException("Conta não verificada");

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getSenhaHash())
                .roles(u.getTipo().name()) // CLIENTE, TECNICO
                .build();
    }
}
