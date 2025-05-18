package integrador2.helpdesk;

import integrador2.helpdesk.dto.CategoryRequest;
import integrador2.helpdesk.dto.CategoryResponse;
import integrador2.helpdesk.model.Category;
import integrador2.helpdesk.repository.CategoryRepository;
import integrador2.helpdesk.service.CategoryService;
import integrador2.helpdesk.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryAndDepartmentServiceTest {

    @Mock
    private CategoryRepository categoryRepo;

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private DepartmentService departmentService;

    @Test
    void listAllCategories_returnsMappedResponses() {
        Category c1 = Category.builder().id(1L).nome("Hardware").ativo(true).build();
        Category c2 = Category.builder().id(2L).nome("Software").ativo(false).build();
        when(categoryRepo.findAll()).thenReturn(List.of(c1, c2));

        List<CategoryResponse> list = categoryService.listarTodas();

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals("Hardware", list.get(0).getNome());
        assertTrue(list.get(0).getAtivo());
        assertEquals(2L, list.get(1).getId());
        assertEquals("Software", list.get(1).getNome());
        assertFalse(list.get(1).getAtivo());
        verify(categoryRepo).findAll();
    }

    @Test
    void createCategory_savesNewCategory_whenNomeUnique() {
        CategoryRequest req = new CategoryRequest();
        req.setNome("Rede");
        req.setAtivo(true);
        when(categoryRepo.findByNomeIgnoreCase("Rede")).thenReturn(Optional.empty());
        Category saved = Category.builder().id(3L).nome("Rede").ativo(true).build();
        when(categoryRepo.save(any(Category.class))).thenReturn(saved);

        var response = categoryService.criar(req);

        assertEquals(3L, response.getId());
        assertEquals("Rede", response.getNome());
        assertTrue(response.getAtivo());
        verify(categoryRepo).findByNomeIgnoreCase("Rede");
        verify(categoryRepo).save(any(Category.class));
    }

    @Test
    void createCategory_throwsException_whenNomeExists() {
        CategoryRequest req = new CategoryRequest();
        req.setNome("Hardware");
        when(categoryRepo.findByNomeIgnoreCase("Hardware"))
                .thenReturn(Optional.of(Category.builder().build()));

        var ex = assertThrows(IllegalArgumentException.class, () -> categoryService.criar(req));
        assertEquals("Categoria já existe", ex.getMessage());
        verify(categoryRepo).findByNomeIgnoreCase("Hardware");
        verify(categoryRepo, never()).save(any());
    }

    @Mock
    private integrador2.helpdesk.repository.DepartmentRepository deptRepo;

    @InjectMocks
    private DepartmentService departmentServiceInstance;

    @Test
    void listDepartments_returnsAll() {
        var d1 = integrador2.helpdesk.model.Department.builder().id(1L).nome("RH").ativo(true).build();
        var d2 = integrador2.helpdesk.model.Department.builder().id(2L).nome("TI").ativo(false).build();
        when(deptRepo.findAll()).thenReturn(List.of(d1, d2));

        var list = departmentServiceInstance.list();

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals("RH", list.get(0).getNome());
        assertTrue(list.get(0).getAtivo());
        assertEquals(2L, list.get(1).getId());
        assertEquals("TI", list.get(1).getNome());
        assertFalse(list.get(1).getAtivo());
        verify(deptRepo).findAll();
    }

    @Test
    void createDepartment_savesAndReturnsDTO() {
        var dto = new integrador2.helpdesk.dto.DepartmentDTO( null, "Infra", null);
        var saved = integrador2.helpdesk.model.Department.builder().id(3L).nome("Infra").ativo(true).build();
        when(deptRepo.save(any())).thenReturn(saved);

        var response = departmentServiceInstance.create(dto);

        assertEquals(3L, response.getId());
        assertEquals("Infra", response.getNome());
        assertTrue(response.getAtivo());
        verify(deptRepo).save(any());
    }

    @Test
    void updateDepartment_modifiesExisting() {
        var existing = integrador2.helpdesk.model.Department.builder().id(5L).nome("Antigo").ativo(true).build();
        var dto = new integrador2.helpdesk.dto.DepartmentDTO( null, "Novo", false);
        when(deptRepo.findById(5L)).thenReturn(Optional.of(existing));
        when(deptRepo.save(existing)).thenReturn(existing);

        var response = departmentServiceInstance.update(5L, dto);

        assertEquals(5L, response.getId());
        assertEquals("Novo", response.getNome());
        assertFalse(response.getAtivo());
        verify(deptRepo).findById(5L);
        verify(deptRepo).save(existing);
    }
}

