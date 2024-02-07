package ecommerce.controllers;

import ecommerce.dtos.ProductDto;
import ecommerce.dtos.ProductVM;
import ecommerce.dtos.filterDtos.ProductRequestFilterDto;
import ecommerce.mappers.ProductMapper;
import ecommerce.repositories.ProductsRepository;
import ecommerce.specifications.ProductSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ecommerce.utils.SearchHelpers.findEntityByIdOrThrow;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductsRepository productsRepository;
    private final ProductSpecification specification;
    private final ProductMapper productMapper;

    public ProductController(
            ProductMapper productMapper,
            ProductsRepository productsRepository,
            ProductSpecification productSpecification) {
        this.productMapper = productMapper;
        this.productsRepository = productsRepository;
        this.specification = productSpecification;
    }

    @PostMapping("/products/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductVM> search(
            @RequestBody ProductRequestFilterDto filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        var spec = specification.build(filter);
        var products = productsRepository.findAll(spec, PageRequest.of(page - 1, limit));

        var result = products
                .map(productMapper::map)
                .toList();

        return result;
    }

    @GetMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductVM find(@PathVariable Long id) {
        var product = findEntityByIdOrThrow(productsRepository, id);

        return productMapper.map(product);
    }

    @PostMapping("/products/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductVM create(@Valid @RequestBody ProductDto dto) {
        var productModel = productMapper.map(dto);
        productsRepository.save(productModel);

        return productMapper.map(productModel);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long id) {
        var product = findEntityByIdOrThrow(productsRepository, id);

        productsRepository.delete(product);
    }

    @PutMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductVM remove(@PathVariable Long id, @RequestBody @Valid ProductDto dto) {
        var product = findEntityByIdOrThrow(productsRepository, id);

        productMapper.update(dto, product);
        productsRepository.save(product);

        return productMapper.map(product);
    }
}
