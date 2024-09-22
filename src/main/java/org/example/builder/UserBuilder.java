package org.example.builder;

import org.example.model.User;

public class UserBuilder {
    private User user;

    public UserBuilder() {
        user = new User();
    }

    public UserBuilder withEmail(String email) {
        user.setEmail(email);
        return this;
    }

    public UserBuilder withPassword(String password) {
        user.setPassword(password);
        return this;
    }

    public UserBuilder withName(String name) {
        user.setName(name);
        return this;
    }

    public User build() {
        return user;
    }
}
