package com.blackjack.Models;

// this class is to store the user information
public class User {
    private int userId;
    private String username;
    private String password;
    private int chips;
    private int wins;
    private int losses;
    private int pushes;
    //User class keeps track of lifetime wins and losses

    public User(String username, String password, int chips, int wins, int losses, int pushes) {
        this.username = username;
        this.password = password;
        this.chips = chips;
        this.wins = wins;
        this.losses = losses;
        this.pushes = pushes;
    }

    public int getUserId() {return userId;}
    public String getUsername() {
        return username;
     }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() { 
        return password; 
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getChips() { 
        return chips; 
    }
    public void setChips(int chips) { 
        this.chips = chips;
     }
    public int getWins() { 
        return wins; 
    }
    public void setWins(int wins){ 
        this.wins = wins;
    }
    public int getLosses(){
         return losses;
        }
    public void setLosses(int losses){ 
        this.losses = losses;
    }
    public int getPushes(){
        return pushes;
    }
    public void setPushes(int pushes){
        this.pushes =pushes;
    }
    public void oneWin(){
        this.wins += 1;
    }

    public void oneLoss(){
        this.losses += 1;
    }

    public void onePush(){
        this.pushes += 1;
    }
    
    
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' + ", chips=" + chips + ", wins=" + wins + ", losses" + losses +", pushes" +pushes +'}';
    }
}