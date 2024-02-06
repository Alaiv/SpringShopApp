package ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.App;
import ecommerce.dtos.BrandDTO;
import ecommerce.mappers.BrandMapper;
import ecommerce.models.Brand;
import ecommerce.repositories.BrandRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
public class BrandControllerTests {
    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BrandMapper brandMapper;

    private Brand testBrand;

    @BeforeEach
    public void setup() {
        Faker faker = new Faker();

        testBrand = Instancio.of(Brand.class)
                .ignore(Select.field(Brand::getId))
                .supply(Select.field(Brand::getName), () -> faker.gameOfThrones().character())
                .create();
    }

    @Test
    public void testSearch() throws Exception {
        // Arrange
        brandRepository.save(testBrand);

        // Act
        var res = mockMvc.perform(get("/api/brands"))
                .andExpect(status().isOk())
                .andReturn();

        var body = res.getResponse().getContentAsString();

        // Assert
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        // Arrange
        var dto = brandMapper.map(testBrand);
        var req = post("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        // Act
        mockMvc.perform(req)
                .andExpect(status().isCreated());

        // Assert
        var brand = brandRepository.findByName(testBrand.getName()).get();
        assertNotNull(brand);
        assertThat(brand.getName()).isEqualTo(dto.getName());
    }

    @Test
    public void testFind() throws Exception {
        // Arrange
        brandRepository.save(testBrand);

        // Act
        var res = mockMvc.perform(get("/api/brands/" + testBrand.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = res.getResponse().getContentAsString();

        // Assert
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testBrand.getName())
        );
    }

    @Test
    public void testUpdate() throws Exception {
        // Arrange
        brandRepository.save(testBrand);

        var dto = new BrandDTO();
        dto.setName("new-name123");

        var req = put("/api/brands/" + testBrand.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        // Act
        mockMvc.perform(req)
                .andExpect(status().isOk());

        // Assert
        testBrand = brandRepository.findByName(dto.getName()).get();
        assertNotNull(testBrand);
    }

    @Test
    public void testDelete() throws Exception {
        // Arrange
        brandRepository.save(testBrand);
        var req = delete("/api/brands/" + testBrand.getId());

        // Act
        mockMvc.perform(req)
                .andExpect(status().isNoContent());

        // Assert
        assertThat(brandRepository.existsById(testBrand.getId())).isFalse();
    }
}
