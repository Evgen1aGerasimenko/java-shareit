package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface JpaItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequester(User user);

    List<ItemRequest> findAllByOrderByCreatedDesc();
}
