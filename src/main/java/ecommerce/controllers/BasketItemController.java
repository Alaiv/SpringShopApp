package ecommerce.controllers;

import ecommerce.dtos.basketItems.BasketItemDto;
import ecommerce.dtos.basketItems.BasketItemVM;
import ecommerce.mappers.BasketItemMapper;
import ecommerce.repositories.BasketRepository;
import ecommerce.repositories.BasketItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ecommerce.utils.SearchHelpers.findEntityByIdOrThrow;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class BasketItemController {
    private final BasketItemRepository basketItemRepository;
    private final BasketRepository basketRepository;
    private final BasketItemMapper basketItemMapper;

    @GetMapping("/basketItem/search/{basketId}")
    @ResponseStatus(HttpStatus.OK)
    public List<BasketItemVM> searchBasketItems(@PathVariable Long basketId) {
        var basket = findEntityByIdOrThrow(basketRepository, basketId);
        var orders = basketItemRepository.findAllByBasketId(basket.getId());

        return orders.stream()
                .map(basketItemMapper::map)
                .toList();
    }

    @GetMapping("/basketItem/find/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BasketItemVM findBasketItem(@PathVariable Long id) {
        var order = findEntityByIdOrThrow(basketItemRepository, id);

        return basketItemMapper.map(order);
    }

    @PostMapping("/basketItem/create")
    @ResponseStatus(HttpStatus.CREATED)
    public BasketItemVM createBasketItem(@RequestBody BasketItemDto dto) {
        var order = basketItemMapper.map(dto);
        basketItemRepository.save(order);

        return basketItemMapper.map(order);
    }

    @DeleteMapping("/basketItem/remove/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBasketItem(@PathVariable Long id) {
       var order = findEntityByIdOrThrow(basketItemRepository, id);

       basketItemRepository.delete(order);
    }

    @Transactional
    @DeleteMapping("/basketItem/removeAll/{basketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllBasketItems(@PathVariable Long basketId) {
        findEntityByIdOrThrow(basketRepository, basketId);

        basketItemRepository.deleteAllByBasketId(basketId);
    }
}