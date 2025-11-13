/******************************************
 * Filename		: Player.java
 * Description	: model class representing a player with game statistics.
 *                stores player information including username, score, and ball count.
 * Project      : Catch the balls game
 * Programmer	: Mochamad Zidan Rusdhiana 
 * Date			: 2025-06-17
******************************************/
package model;

public class Player {
    // player information properties
    private String username;       // player's unique username
    private int skor;              // player's total score points
    private int count;             // total number of balls collected
    
    // constructor
    public Player() {
    }

    public Player(String username, int skor, int count) {
        this.username = username;
        this.skor = skor;
        this.count = count;
    }    
    
    // getters and setters for player properties
    
    // username getter and setter
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    // score getter and setter
    public int getSkor() {
        return skor;
    }
    
    public void setSkor(int skor) {
        this.skor = skor;
    }
    
    // ball count getter and setter
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
}