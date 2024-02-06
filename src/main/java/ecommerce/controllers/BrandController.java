package ecommerce.controllers;

import ecommerce.dtos.BrandDTO;
import ecommerce.mappers.BrandMapper;
import ecommerce.models.Brand;
import ecommerce.repositories.BrandRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class BrandController {
    private final BrandRepository repository;
    private final BrandMapper mapper;

    public BrandController(BrandRepository brandRepository, BrandMapper brandMapper) {
        this.repository = brandRepository;
        this.mapper = brandMapper;
    }

    @GetMapping("/brands")
    @ResponseStatus(HttpStatus.OK)
    public List<Brand> search() {
        return repository.findAll();
    }

    @GetMapping("/brands/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Brand find(@PathVariable Long id) {
        var brand = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        return brand;
    }

    @PostMapping("/brands")
    @ResponseStatus(HttpStatus.CREATED)
    public Brand create(@RequestBody @Valid BrandDTO dto) {
        var brand = mapper.map(dto);
        repository.save(brand);

        return brand;
    }

    @PutMapping("/brands/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Brand update(@RequestBody @Valid BrandDTO dto, @PathVariable Long id) {
        var brand = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.update(dto, brand);
        repository.save(brand);

        return brand;
    }

    @DeleteMapping("/brands/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        var brand = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        repository.delete(brand);
    }
}
