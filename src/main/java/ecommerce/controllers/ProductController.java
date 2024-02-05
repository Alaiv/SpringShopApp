package ecommerce.controllers;

import ecommerce.dtos.ProductDto;
import ecommerce.dtos.ProductVM;
import jakarta.validation.Valid;
import ecommerce.mappers.ProductMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ecommerce.repositories.ProductsRepository;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductsRepository productsRepository;
    private final ProductMapper productMapper;

    public ProductController(ProductMapper productMapper, ProductsRepository productsRepository) {
        this.productMapper = productMapper;
        this.productsRepository = productsRepository;
    }

    @GetMapping("/products")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductVM> search() {
        var products = productsRepository.findAll();
        var result = products.stream()
                .map(productMapper::map)
                .toList();

        return result;
    }

    @GetMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductVM find(@PathVariable Long id) {
        var product = productsRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        return productMapper.map(product);
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductVM find(@Valid @RequestBody ProductDto dto) {
        var productModel = productMapper.map(dto);
        productsRepository.save(productModel);
        return productMapper.map(productModel);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long id) {
        productsRepository.deleteById(id);
    }

    @PutMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductVM remove(@PathVariable Long id, @RequestBody @Valid ProductDto dto) {
        var product = productsRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        productMapper.update(dto, product);
        productsRepository.save(product);

        return productMapper.map(product);
    }
}
