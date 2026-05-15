package com.neroyun.mediator;

import java.util.ArrayList;
import java.util.List;

public class UserStore {
    private static UserStore instance;

    public static UserStore getInstance() {
        if (instance == null) {
            instance = new UserStore();
        }
        return instance;
    }

    private final List<User> users = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public User getUser(Long id) {
        return users.stream().filter(user -> user.id().equals(id)).findFirst().orElse(null);
    }

    public void clear() {
        users.clear();
    }
}
