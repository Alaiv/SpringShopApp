package ecommerce.utils;

import ecommerce.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public class SearchHelpers {
    public static <T> T findEntityByIdOrThrow(JpaRepository<T, Long> repository, Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Entity with id %d not found", id)));
    }
}
