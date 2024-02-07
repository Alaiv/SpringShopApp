package ecommerce.controllers;

import ecommerce.dtos.CategoryDTO;
import ecommerce.mappers.CategoryMapper;
import ecommerce.models.Category;
import ecommerce.repositories.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ecommerce.utils.SearchHelpers.findEntityByIdOrThrow;

@RestController
@RequestMapping("/api")
public class CategoryController {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public CategoryController(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.repository = categoryRepository;
        this.mapper = categoryMapper;
    }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<Category> search(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return repository.findAll(PageRequest.of(page, limit)).toList();
    }

    @GetMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Category find(@PathVariable Long id) {
        return findEntityByIdOrThrow(repository, id);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public Category create(@RequestBody @Valid CategoryDTO dto) {
        var category = mapper.map(dto);
        repository.save(category);

        return category;
    }

    @PutMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Category update(@RequestBody @Valid CategoryDTO dto, @PathVariable Long id) {
        var category = findEntityByIdOrThrow(repository, id);

        mapper.update(dto, category);
        repository.save(category);

        return category;
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        var category = findEntityByIdOrThrow(repository, id);

        repository.delete(category);
    }
}
