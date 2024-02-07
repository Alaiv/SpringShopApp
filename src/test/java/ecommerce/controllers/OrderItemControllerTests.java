package ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.App;
import ecommerce.dtos.OrderItemDto;
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

import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
public class OrderItemControllerTests {
    @Autowired
    private OrderItemRepository orderItemRepository;

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
    private Faker faker;

    private Basket testBasket;
    private OrderItem testOrderItem;
    private Product testProduct;

    @BeforeEach
    public void setup() {
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
                .supply(Select.field(User::getPassword), () -> faker.funnyName().name())
                .create();

        testBasket = Instancio.of(Basket.class)
                .ignore(Select.field(Basket::getId))
                .supply(Select.field(Basket::getUser), () -> List.of(user))
                .create();

        testOrderItem = Instancio.of(OrderItem.class)
                .ignore(Select.field(OrderItem::getId))
                .supply(Select.field(OrderItem::getBasket), () -> testBasket)
                .supply(Select.field(OrderItem::getProduct), () -> testProduct)
                .create();

        brandRepository.save(testBrand);
        categoryRepository.save(testCategory);
        productsRepository.save(testProduct);
        userRepository.save(user);
    }

    @Test
    public void testSearch() throws Exception {
        // Arrange
        basketRepository.save(testBasket);
        orderItemRepository.save(testOrderItem);

        // Act
        var res = mockMvc.perform(get("/api/orderItem/search/" + testBasket.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = res.getResponse().getContentAsString();

        // Assert
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        // Arrange
        var dto = new OrderItemDto();
        dto.setBaskedId(testBasket.getId());
        dto.setProductId(testProduct.getId());

        var req = post("/api/orderItem/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        // Act
        mockMvc.perform(req)
                .andExpect(status().isCreated());

        // Assert
        var order = orderItemRepository
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
        orderItemRepository.save(testOrderItem);

        // Act
        var res = mockMvc.perform(get("/api/orderItem/find/" + testOrderItem.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = res.getResponse().getContentAsString();

        // Assert
        assertThatJson(body).and(
                v -> v.node("basket").node("id").isEqualTo(testOrderItem.getBasket().getId().intValue()),
                v -> v.node("product").node("id").isEqualTo(testOrderItem.getProduct().getId().intValue())
        );
    }

    @Test
    public void testDelete() throws Exception {
        // Arrange
        basketRepository.save(testBasket);
        orderItemRepository.save(testOrderItem);

        var req = delete("/api/orderItem/remove/" + testOrderItem.getId());

        // Act
        mockMvc.perform(req)
                .andExpect(status().isNoContent());

        // Assert
        assertThat(orderItemRepository.existsById(testOrderItem.getId())).isFalse();
    }

    @Test
    public void testDeleteAll() throws Exception {
        // Arrange
        basketRepository.save(testBasket);
        orderItemRepository.save(testOrderItem);

        var req = delete("/api/orderItem/removeAll/" + testBasket.getId());

        // Act
        mockMvc.perform(req)
                .andExpect(status().isNoContent());

        // Assert
        assertThat(orderItemRepository.existsById(testOrderItem.getId())).isFalse();
    }
}
