package database;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import Classes.User;

public class StubDatabase {
    private static final List<User> users = new ArrayList<>();
    private static final String FILE_PATH = "user_data.txt"; // File to store user data

    static {
        loadUsersFromFile(); // Load users from the text file when the program starts
    }

    // Retrieving a user by username
    public static User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    // Adding a new user to the stub database (if not already existing)
    public static void addUser(User user) {
        if (getUser(user.getUsername()) == null) { // Prevent duplicates
            users.add(user);
            saveUsersToFile(); // Save changes
        }
    }

    // This is to update an existing user's data
    public static void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                users.set(i, user);
                saveUsersToFile(); // Save changes
                return;
            }
        }
    }

    // Print all users (it's just for debugging purposes)
    public static void printUsers() {
        for (User user : users) {
            System.out.println(user);
        }
    }

    // Saving user data to a file to get it in the next game
    private static void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                writer.write(user.getUsername() + "," +
                        user.getPassword() + "," +  // Save the password securely (plain text for now)
                        user.getChips() + "," +
                        user.getWins() + "," +
                        user.getLosses() + "," +
                        user.getPushes());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("There is an error in saving user data: " + e.getMessage());
        }
    }

    // Loading user data from a file
    private static void loadUsersFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return; // If there's no file, return early

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String username = parts[0];
                    String password = parts[1];
                    int chips = Integer.parseInt(parts[2]);
                    int wins = Integer.parseInt(parts[3]);
                    int losses = Integer.parseInt(parts[4]);
                    int pushes = Integer.parseInt(parts[5]);

                    users.add(new User(username, password, chips, wins, losses, pushes));
                }
            }
        } catch (IOException e) {
            System.out.println("There is an error in loading user data: " + e.getMessage());
        }
    }

    // Validate password for the given username
    public static boolean validatePassword(String username, String password) {
        User user = getUser(username);
        return user != null && user.getPassword().equals(password);
    }
}


    /*static {

    }
// retrieving a user by username
    public static User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
// add new user(new Player) to teh stub db without duplicating
    public static void addUser(User user) {
        if (getUser(user.getUsername()) == null) { // Prevent duplicates
            users.add(user);
    }
    }
    // update new player's data in stub db
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
}*/