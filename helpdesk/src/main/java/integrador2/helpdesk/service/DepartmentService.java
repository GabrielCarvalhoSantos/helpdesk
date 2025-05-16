package integrador2.helpdesk.service;

import integrador2.helpdesk.dto.DepartmentDTO;
import integrador2.helpdesk.model.Department;
import integrador2.helpdesk.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository repo;

    public List<DepartmentDTO> list(){
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public DepartmentDTO create(DepartmentDTO d){
        Department dep = repo.save(Department.builder()
                .nome(d.getNome()).build());
        return toDTO(dep);
    }

    @Transactional
    public DepartmentDTO update(Long id, DepartmentDTO d){
        Department dep = repo.findById(id).orElseThrow();
        dep.setNome(d.getNome());
        dep.setAtivo(d.getAtivo());
        return toDTO(repo.save(dep));
    }

    private DepartmentDTO toDTO(Department d){
        return DepartmentDTO.builder()
                .id(d.getId()).nome(d.getNome()).ativo(d.getAtivo())
                .build();
    }
}
