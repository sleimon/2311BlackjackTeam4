package Classes;
    import database.StubDatabase;

    public class DatabaseTest {
        public static void main(String[] args) {
            // Test: Print all users
            System.out.println("Initial users:");
            StubDatabase.printUsers();
    
            // Test: Try logging in an existing user
            User existingUser = StubDatabase.getUser("player1");
            System.out.println("Found user: " + (existingUser != null ? existingUser : "Not found"));
    
            // Test: Register a new user
            User newUser = new User("newPlayer", "newPass", 1200, 0,0,0);
            StubDatabase.addUser(newUser);
            System.out.println("After adding newPlayer:");
            StubDatabase.printUsers();
    
            // Test: Update user data
            newUser.setChips(2000);
            newUser.setWins(10);
            StubDatabase.updateUser(newUser);
            System.out.println("After updating newPlayer:");
            StubDatabase.printUsers();
        }
    }