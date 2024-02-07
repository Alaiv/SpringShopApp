package ecommerce.controllers;

import ecommerce.dtos.brand.BrandDTO;
import ecommerce.exceptions.NotFoundException;
import ecommerce.mappers.BrandMapper;
import ecommerce.models.Brand;
import ecommerce.repositories.BrandRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<Brand> search(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return repository.findAll(PageRequest.of(page, limit)).toList();
    }

    @GetMapping("/brands/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Brand find(@PathVariable Long id) {
        return findBrandByIdOrThrow(id);
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
        var brand = findBrandByIdOrThrow(id);

        mapper.update(dto, brand);
        repository.save(brand);

        return brand;
    }

    @DeleteMapping("/brands/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        var brand = findBrandByIdOrThrow(id);

        repository.delete(brand);
    }

    private Brand findBrandByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Brand with id %d not found", id)));
    }
}
