package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface JpaItemRepository extends JpaRepository<Item, Long> {

    List<Item> getItemsByOwnerId(Long userId);

    @Query("select i " +
            "from Item as i join fetch i.owner " +
            "where i.available = true and " +
            "(upper(i.name) like upper(concat('%', ?1, '%') ) or " +
            "upper(i.description) like upper(concat('%', ?1, '%') ))")
    List<Item> searchItem(String text);
}
