package database;

import java.util.ArrayList;
import java.util.List;

import Classes.User;

public class StubDatabase {
    private static final List<User> users = new ArrayList<>();

    static {
        // Preload some dummy users
        users.add(new User("player1", "password123", 1000, 5));
        users.add(new User("player2", "securepass", 500, 2));
    }

    public static User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static void addUser(User user) {
        users.add(user);
    }

    public static void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                users.set(i, user);
                return;
            }
        }
    }
     // Print all users (for testing)
    public static void printUsers() {
        for (User user : users) {
            System.out.println(user);
}
    }
}
