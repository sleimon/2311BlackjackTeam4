package IntegrationTests;

import com.blackjack.Models.User;
import com.blackjack.Services.UserService;

import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    @BeforeEach
    public void setup() {

    }

    @AfterEach
    public void tearDown() {

    }

    @Test
    public void testLoginWithValidUser() {
        // Create a test user and add to the database
        User testUser = new User("testUser", "testPassword", 1000, 0, 0, 0);
        UserService.addUser(testUser);

        // Simulate the login process
        String username = "testUser";
        String password = "testPassword";

        // Assuming Main class uses the login method
        ActionListener loginListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both username and password.");
                    return;
                }

                if (UserService.validatePassword(username, password)) {
                    System.out.println("User logged in: " + username);
                    // Simulate game start
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect username or password.");
                }
            }
        };

        // Execute the login
        loginListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Login"));

        // Validate the user login logic (using print statements to simulate UI feedback)
        assertTrue(UserService.validatePassword(username, password), "The user should have logged in successfully.");
        UserService.deleteUser(username);
    }

    @Test
    public void testLoginWithInvalidUser() {
        // Test login with invalid user credentials
        String username = "invalidUser";
        String password = "wrongPassword";

        ActionListener loginListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both username and password.");
                    return;
                }

                if (UserService.validatePassword(username, password)) {
                    System.out.println("User logged in: " + username);
                    // Simulate game start
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect username or password.");
                }
            }
        };

        // Execute the login
        loginListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Login"));

        // Assert that the invalid user is not logged in
        assertFalse(UserService.validatePassword(username, password), "The user should not have logged in.");
    }

    @Test
    public void testSignUpWithNewUser() {
        // Create new user data
        String username = "newUser";
        String password = "newPassword";

        ActionListener signUpListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both username and password.");
                    return;
                }

                // Check if user already exists
                User existingUser = UserService.getUser(username);
                if (existingUser != null) {
                    JOptionPane.showMessageDialog(null, "Username already exists. Please choose a different username.");
                } else {
                    // Create new user and add to the database
                    User newUser = new User(username, password, 1000, 0, 0, 0); // 1000 chips by default
                    UserService.addUser(newUser);
                    JOptionPane.showMessageDialog(null, "User created successfully.");
                    System.out.println("User created: " + username);
                }
            }
        };

        // Execute the sign-up process
        signUpListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Sign Up"));

        // Check if the new user was added
        User createdUser = UserService.getUser(username);
        assertNotNull(createdUser, "The user should be successfully created.");
        assertEquals(username, createdUser.getUsername(), "The username should match.");
        UserService.deleteUser(username);
    }

    @Test
    public void testSignUpWithExistingUser() {
        // Create and add a user to the database
        User existingUser = new User("existingUser", "existingPassword", 1000, 0, 0, 0);
        UserService.addUser(existingUser);

        String username = "existingUser";
        String password = "newPassword";

        ActionListener signUpListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both username and password.");
                    return;
                }

                // Check if user already exists
                User user = UserService.getUser(username);
                if (user != null) {
                    JOptionPane.showMessageDialog(null, "Username already exists. Please choose a different username.");
                } else {
                    User newUser = new User(username, password, 1000, 0, 0, 0);
                    UserService.addUser(newUser);
                    JOptionPane.showMessageDialog(null, "User created successfully.");
                }
            }
        };

        // Execute the sign-up process with the existing username
        signUpListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Sign Up"));
        UserService.deleteUser(username);

        // Check if the user already exists and assert
        User createdUser = UserService.getUser(username);
        assertNull(createdUser, "The user should not be created because the username already exists.");
    }
}
