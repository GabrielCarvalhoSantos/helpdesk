package integrador2.helpdesk.repository;

import integrador2.helpdesk.model.VerificationCode;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface VerificationCodeRepository
        extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findByEmailAndCode(String email, String code);

    @Modifying
    @Transactional
    @Query("delete from VerificationCode v where v.email = :email")
    void deleteByEmail(@Param("email") String email);

}
