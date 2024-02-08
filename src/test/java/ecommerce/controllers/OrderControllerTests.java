package ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.App;
import ecommerce.dtos.basketItems.BasketItemDto;
import ecommerce.dtos.filterDtos.OrderRequestFilterDto;
import ecommerce.dtos.order.OrderCreateDto;
import ecommerce.dtos.order.OrderVM;
import ecommerce.enums.OrderStatuses;
import ecommerce.mappers.OrderItemMapper;
import ecommerce.mappers.OrderMapper;
import ecommerce.models.*;
import ecommerce.repositories.*;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Assertions;
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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
public class OrderControllerTests {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BasketItemRepository basketItemRepository;

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
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private Faker faker;

    private Order testOrder;
    private Basket testBasket;
    private BasketItem testBasketItem;
    private Product testProduct;
    private User user;

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

        user = Instancio.of(User.class)
                .supply(Select.field(User::getEmail), () -> faker.darkSouls().classes() + "@google.com")
                .supply(Select.field(User::getPassword), () -> faker.funnyName().name())
                .create();

        testBasket = Instancio.of(Basket.class)
                .ignore(Select.field(Basket::getId))
                .supply(Select.field(Basket::getUser), () -> List.of(user))
                .create();

        testBasketItem = Instancio.of(BasketItem.class)
                .ignore(Select.field(BasketItem::getId))
                .supply(Select.field(BasketItem::getBasket), () -> testBasket)
                .supply(Select.field(BasketItem::getProduct), () -> testProduct)
                .create();

        testOrder = Instancio.of(Order.class)
                .ignore(Select.field(Order::getId))
                .supply(Select.field(Order::getUser), () -> user)
                .supply(Select.field(Order::getOrderItems), () -> List.of(orderItemMapper.map(testBasketItem)))
                .supply(Select.field(Order::getOrderStatus), () -> OrderStatuses.CREATED)
                .supply(Select.field(Order::getBasket), () -> testBasket)
                .create();

        brandRepository.save(testBrand);
        categoryRepository.save(testCategory);
        productsRepository.save(testProduct);
        userRepository.save(user);
        basketRepository.save(testBasket);
        basketItemRepository.save(testBasketItem);
    }

    @Test
    public void testSearch() throws Exception {
        // Arrange
        orderRepository.save(testOrder);
        OrderRequestFilterDto filter = new OrderRequestFilterDto();
        filter.setUserId(user.getId());
        filter.setOrderStatus(testOrder.getOrderStatus());

        var req = post("/api/orders/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(filter));

        // Act
        var res = mockMvc.perform(req)
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var body = res.getResponse().getContentAsString();
        assertThatJson(body).isArray().hasSize(1);
    }

    @Test
    public void testCreate() throws Exception {
        // Arrange
        var dto = new OrderCreateDto();
        dto.setBasketId(testBasket.getId());
        dto.setUserId(user.getId());

        var req = post("/api/orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        // Act
        var res = mockMvc.perform(req)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Assert
        var respOrder = om.readValue(res, Order.class);
        var order = orderRepository
                .findById(respOrder.getId())
                .orElse(null);

        assertNotNull(order);
        assertThat(respOrder).isEqualTo(order);
    }

    @Test
    public void testFind() throws Exception {
        // Arrange
        orderRepository.save(testOrder);
        var orderVm = orderMapper.map(testOrder);

        // Act
        var res = mockMvc.perform(get("/api/orders/find/" + testOrder.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = res.getResponse().getContentAsString();
        var result = om.readValue(body, OrderVM.class);

        // Assert
        assertThat(result.getId()).isEqualTo(orderVm.getId());
        assertThat(result.getOrderStatus()).isEqualTo(orderVm.getOrderStatus());
        assertThat(result.getUserId()).isEqualTo(orderVm.getUserId());
    }

    @Test
    public void testDelete() throws Exception {
        // Arrange
        orderRepository.save(testOrder);

        var req = delete("/api/orders/remove/" + testOrder.getId());

        // Act
        mockMvc.perform(req)
                .andExpect(status().isNoContent());

        // Assert
        assertThat(orderRepository.existsById(testOrder.getId())).isFalse();
    }

    @Test
    public void testUpdate() throws Exception {
        // Arrange
        orderRepository.save(testOrder);

        var req = put(String.format("/api/orders/%s/update", testOrder.getId()))
                .param("status", "CONFIRMED");

        // Act
        mockMvc.perform(req)
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var order = orderRepository.findById(testOrder.getId()).orElse(null);

        assertNotNull(order);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatuses.CONFIRMED);
    }
}
