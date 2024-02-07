package ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.App;
import ecommerce.dtos.BrandDTO;
import ecommerce.mappers.CategoryMapper;
import ecommerce.models.Category;
import ecommerce.repositories.CategoryRepository;
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
public class CategoryControllerTests {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private Faker faker;

    private Category testCategory;

    @BeforeEach
    public void setup() {
        testCategory = Instancio.of(Category.class)
                .ignore(Select.field(Category::getId))
                .supply(Select.field(Category::getName), () -> faker.gameOfThrones().character())
                .create();
    }

    @Test
    public void testSearch() throws Exception {
        // Arrange
        categoryRepository.save(testCategory);

        // Act
        var res = mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andReturn();

        var body = res.getResponse().getContentAsString();

        // Assert
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        // Arrange
        var dto = categoryMapper.map(testCategory);
        var req = post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        // Act
        mockMvc.perform(req)
                .andExpect(status().isCreated());

        // Assert
        var brand = categoryRepository.findByName(testCategory.getName()).get();
        assertNotNull(brand);
        assertThat(brand.getName()).isEqualTo(dto.getName());
    }

    @Test
    public void testFind() throws Exception {
        // Arrange
        categoryRepository.save(testCategory);

        // Act
        var res = mockMvc.perform(get("/api/categories/" + testCategory.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = res.getResponse().getContentAsString();

        // Assert
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testCategory.getName())
        );
    }

    @Test
    public void testUpdate() throws Exception {
        // Arrange
        categoryRepository.save(testCategory);

        var dto = new BrandDTO();
        dto.setName("new-name1233");

        var req = put("/api/categories/" + testCategory.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        // Act
        mockMvc.perform(req)
                .andExpect(status().isOk());

        // Assert
        testCategory = categoryRepository.findByName(dto.getName()).get();
        assertNotNull(testCategory);
    }

    @Test
    public void testDelete() throws Exception {
        // Arrange
        categoryRepository.save(testCategory);
        var req = delete("/api/categories/" + testCategory.getId());

        // Act
        mockMvc.perform(req)
                .andExpect(status().isNoContent());

        // Assert
        assertThat(categoryRepository.existsById(testCategory.getId())).isFalse();
    }
}
