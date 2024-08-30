package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long idGenerator = 0L;

    @Override
    public User createUser(User user) {
        long id = ++idGenerator;
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        user.setId(id);
        user.setName(user.getName());
        user.setEmail(user.getEmail());
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public User deleteUserById(Long id) {
        return users.remove(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}