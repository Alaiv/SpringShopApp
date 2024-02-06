package ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.App;
import ecommerce.dtos.ProductDto;
import ecommerce.mappers.ProductMapper;
import ecommerce.models.Brand;
import ecommerce.models.Category;
import ecommerce.models.Product;
import ecommerce.repositories.BrandRepository;
import ecommerce.repositories.CategoryRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ecommerce.repositories.ProductsRepository;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductsRepository productsRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Product testProduct;
    private Brand testBrand;
    private Category testCategory;

    @BeforeEach
    public void setup() {
        Faker faker = new Faker();
        testBrand = Instancio.of(Brand.class)
                .create();

        testCategory = Instancio.of(Category.class)
                .create();

        testProduct = Instancio.of(Product.class)
                .ignore(Select.field(Product::getId))
                .supply(Select.field(Product::getName), () -> faker.gameOfThrones().character())
                .supply(Select.field(Product::getBrand), () -> testBrand)
                .supply(Select.field(Product::getCategory), () -> testCategory)
                .supply(Select.field(Product::getPrice), () -> faker.number().randomDouble(3, 1, 200))
                .create();
    }

    @Test
    public void testSearch() throws Exception {
        // Arrange
        categoryRepository.save(testCategory);
        brandRepository.save(testBrand);
        productsRepository.save(testProduct);

        // Act
        var res = mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andReturn();

        var body = res.getResponse().getContentAsString();

        // Assert
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        // Arrange
        var dto = productMapper.map(testProduct);
        var req = post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        // Act
        mockMvc.perform(req)
                .andExpect(status().isCreated());

        // Assert
        var product = productsRepository.findByName(dto.getName()).get();
        assertNotNull(product);
        assertThat(product.getName()).isEqualTo(dto.getName());
    }

    @Test
    public void testFind() throws Exception {
        // Arrange
        categoryRepository.save(testCategory);
        brandRepository.save(testBrand);
        productsRepository.save(testProduct);

        // Act
        var res = mockMvc.perform(get("/api/products/" + testProduct.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = res.getResponse().getContentAsString();

        // Assert
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testProduct.getName()),
                v -> v.node("price").isEqualTo(testProduct.getPrice()),
                v -> v.node("category").node("name").isEqualTo(testProduct.getCategory().getName()),
                v -> v.node("brand").node("name").isEqualTo(testProduct.getBrand().getName())
        );
    }

    @Test
    public void testUpdate() throws Exception {
        // Arrange
        categoryRepository.save(testCategory);
        brandRepository.save(testBrand);
        productsRepository.save(testProduct);

        var dto = new ProductDto();
        dto.setName(JsonNullable.of("test123"));
        dto.setPrice(JsonNullable.of(123321.123));
        dto.setLeftInStock(JsonNullable.of(123));

        var req = put("/api/products/" + testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        // Act
        mockMvc.perform(req)
                .andExpect(status().isOk());

        // Assert
        testProduct = productsRepository.findById(testProduct.getId()).get();
        assertNotNull(testProduct);
        assertThat(testProduct.getName()).isEqualTo(dto.getName().get());
        assertThat(testProduct.getPrice()).isEqualTo(dto.getPrice().get());
        assertThat(testProduct.getLeftInStock()).isEqualTo(dto.getLeftInStock().get());
    }

    @Test
    public void testDelete() throws Exception {
        // Arrange
        categoryRepository.save(testCategory);
        brandRepository.save(testBrand);
        productsRepository.save(testProduct);
        var req = delete("/api/products/" + testProduct.getId());

        // Act
        mockMvc.perform(req)
                .andExpect(status().isNoContent());

        // Assert
        assertThat(productsRepository.existsById(testProduct.getId())).isFalse();
    }
}

