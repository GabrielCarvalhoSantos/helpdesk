// AttachmentRepository.java
package integrador2.helpdesk.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import integrador2.helpdesk.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByTicket_Id(Long ticketId);
}
