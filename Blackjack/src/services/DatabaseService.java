package services;
import Classes.User;
import Classes.Main;
import java.util.List;

public interface DatabaseService {
User getUser(String username);
void addUser(User user);
void updateUser(User user);
boolean validatePassword(String username, String password);
List<User> getAllUsers();
void deleteUser(String username);
}
