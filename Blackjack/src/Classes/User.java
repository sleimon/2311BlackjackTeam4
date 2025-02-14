package Classes;

// this class is to store the user information
public class User {
    private String username;
    private String password;
    private int chips;
    private int wins;

    public User(String username, String password, int chips, int wins) {
        this.username = username;
        this.password = password;
        this.chips = chips;
        this.wins = wins;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getChips() { return chips; }
    public int getWins() { return wins; }

    public void setChips(int chips) { this.chips = chips; }
    public void setWins(int wins) { this.wins = wins; }
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", chips=" + chips +
                ", wins=" + wins +
                '}';
    }
}