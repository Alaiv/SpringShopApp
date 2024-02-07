package ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.App;
import ecommerce.mappers.OrderMapper;
import ecommerce.models.*;
import ecommerce.repositories.*;
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
public class OrdersControllerTests {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductsRepository productsRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderMapper orderMapper;
    private Basket testBasket;
    private Order testOrder;

    @BeforeEach
    public void setup() {
        Faker faker = new Faker();

        var testBrand = Instancio.of(Brand.class)
                .create();

        var testCategory = Instancio.of(Category.class)
                .create();

        var testProduct = Instancio.of(Product.class)
                .ignore(Select.field(Product::getId))
                .supply(Select.field(Product::getName), () -> faker.gameOfThrones().character())
                .supply(Select.field(Product::getBrand), () -> testBrand)
                .supply(Select.field(Product::getCategory), () -> testCategory)
                .supply(Select.field(Product::getPrice), () -> faker.number().randomDouble(3, 1, 200))
                .create();

        brandRepository.save(testBrand);
        categoryRepository.save(testCategory);
        productsRepository.save(testProduct);

        testBasket = Instancio.of(Basket.class)
                .ignore(Select.field(Product::getId))
                .create();

        testOrder = Instancio.of(Order.class)
                .ignore(Select.field(Order::getId))
                .supply(Select.field(Order::getBasket), () -> testBasket)
                .supply(Select.field(Order::getProduct), () -> testProduct)
                .create();
    }

    @Test
    public void testSearch() throws Exception {
        // Arrange
        basketRepository.save(testBasket);
        orderRepository.save(testOrder);

        // Act
        var res = mockMvc.perform(get("/api/orders/" + testBasket.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = res.getResponse().getContentAsString();

        // Assert
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        // Arrange
        var dto = orderMapper.map(testOrder);
        var req = post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        // Act
        mockMvc.perform(req)
                .andExpect(status().isCreated());

        // Assert
        var order = orderRepository.findById(testOrder.getId()).get();
        assertNotNull(order);
        assertThat(order.getBasket().getId()).isEqualTo(dto.getBasket().getId());
    }

    @Test
    public void testFind() throws Exception {
        // Arrange
        basketRepository.save(testBasket);
        orderRepository.save(testOrder);

        // Act
        var res = mockMvc.perform(get("/api/orders/" + testOrder.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = res.getResponse().getContentAsString();

        // Assert
        assertThatJson(body).and(
                v -> v.node("basket").node("id").isEqualTo(testOrder.getBasket().getId()),
                v -> v.node("product").node("id").isEqualTo(testOrder.getProduct().getId())
        );
    }

    @Test
    public void testDelete() throws Exception {
        // Arrange
        basketRepository.save(testBasket);
        orderRepository.save(testOrder);

        var req = delete("/api/orders/" + testOrder.getId());

        // Act
        mockMvc.perform(req)
                .andExpect(status().isNoContent());

        // Assert
        assertThat(orderRepository.existsById(testOrder.getId())).isFalse();
    }
}
