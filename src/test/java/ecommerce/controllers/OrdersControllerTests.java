package ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.App;
import ecommerce.dtos.OrderDto;
import ecommerce.mappers.OrderMapper;
import ecommerce.models.*;
import ecommerce.repositories.*;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.control.MappingControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderMapper orderMapper;
    private Basket testBasket;
    private Order testOrder;
    private Product testProduct;

    @BeforeEach
    public void setup() {
        Faker faker = new Faker();

        var testBrand = Instancio.of(Brand.class)
                .create();

        var testCategory = Instancio.of(Category.class)
                .create();

        testProduct = Instancio.of(Product.class)
                .ignore(Select.field(Product::getId))
                .supply(Select.field(Product::getName), () -> faker.gameOfThrones().character())
                .supply(Select.field(Product::getBrand), () -> testBrand)
                .supply(Select.field(Product::getCategory), () -> testCategory)
                .supply(Select.field(Product::getPrice), () -> faker.number().randomDouble(3, 1, 200))
                .create();

        var user = Instancio.of(User.class)
                .supply(Select.field(User::getEmail), () -> faker.darkSouls().classes() + "@google.com")
                .supply(Select.field(User::getPassword), () -> faker.darkSouls().shield())
                .create();

        brandRepository.save(testBrand);
        categoryRepository.save(testCategory);
        productsRepository.save(testProduct);
        userRepository.save(user);

        testBasket = Instancio.of(Basket.class)
                .ignore(Select.field(Basket::getId))
                .supply(Select.field(Basket::getUser), () -> List.of(user))
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
        var res = mockMvc.perform(get("/api/orders/search/" + testBasket.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = res.getResponse().getContentAsString();

        // Assert
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        // Arrange
        var dto = new OrderDto();
        dto.setBaskedId(testBasket.getId());
        dto.setProductId(testProduct.getId());

        var req = post("/api/orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        // Act
        mockMvc.perform(req)
                .andExpect(status().isCreated());

        // Assert
        var order = orderRepository
                .findAllByBasketId(testBasket.getId())
                .stream()
                .findFirst()
                .orElse(null);

        assertNotNull(order);
    }

    @Test
    public void testFind() throws Exception {
        // Arrange
        basketRepository.save(testBasket);
        orderRepository.save(testOrder);

        // Act
        var res = mockMvc.perform(get("/api/orders/find/" + testOrder.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = res.getResponse().getContentAsString();

        // Assert
        assertThatJson(body).and(
                v -> v.node("basket").node("id").isEqualTo(testOrder.getBasket().getId().intValue()),
                v -> v.node("product").node("id").isEqualTo(testOrder.getProduct().getId().intValue())
        );
    }

    @Test
    public void testDelete() throws Exception {
        // Arrange
        basketRepository.save(testBasket);
        orderRepository.save(testOrder);

        var req = delete("/api/orders/remove/" + testOrder.getId());

        // Act
        mockMvc.perform(req)
                .andExpect(status().isNoContent());

        // Assert
        assertThat(orderRepository.existsById(testOrder.getId())).isFalse();
    }

    @Test
    public void testDeleteAll() throws Exception {
        // Arrange
        basketRepository.save(testBasket);
        orderRepository.save(testOrder);

        var req = delete("/api/orders/removeAll/" + testBasket.getId());

        // Act
        mockMvc.perform(req)
                .andExpect(status().isNoContent());

        // Assert
        assertThat(orderRepository.existsById(testOrder.getId())).isFalse();
    }
}
