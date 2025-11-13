/******************************************
 * Filename		: DatabaseService.java
 * Description	: Service class for handling database operations related to player data.
 *                Manages player score storage, retrieval, and updates in MySQL database.
 * Project      : Catch the balls game
 * Programmer	: Mochamad Zidan Rusdhiana
 * Date			: 2025-06-17
******************************************/
package model;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {    

    // test database connection
    public boolean testConnection() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            System.out.println("Database connection test successful!");
            return true;
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    // retrieves all players from the database, ordered by score in descending order
    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        String query = "SELECT * FROM thasil ORDER BY skor DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Process each record from the result set
            while (rs.next()) {
                Player player = new Player();
                player.setUsername(rs.getString("username"));
                player.setSkor(rs.getInt("skor"));
                player.setCount(rs.getInt("count"));
                players.add(player);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving players: " + e.getMessage());
        }
        
        return players;
    }    
    
    // saves or updates a player's score and count in the database
    public void saveOrUpdatePlayer(Player player) {
        // Validate input data
        if (player == null || player.getUsername() == null || player.getUsername().trim().isEmpty()) {
            System.err.println("Invalid player data - cannot save");
            return;
        }
        
        System.out.println("Attempting to save player: " + player.getUsername() + 
                          " Score: " + player.getSkor() + " Count: " + player.getCount());
        
        // SQL queries for database operations
        String checkQuery = "SELECT skor, count FROM thasil WHERE username = ?";
        String insertQuery = "INSERT INTO thasil (username, skor, count) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE thasil SET skor = skor + ?, count = count + ? WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            System.out.println("Database connection established");
            
            // Check if player already exists in database
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, player.getUsername());
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    // Player exists - update their scores by adding new values
                    System.out.println("Player exists, updating scores");
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, player.getSkor());
                        updateStmt.setInt(2, player.getCount());
                        updateStmt.setString(3, player.getUsername());
                        int rowsAffected = updateStmt.executeUpdate();
                        System.out.println("Updated " + rowsAffected + " rows");
                    }
                } else {
                    // Player doesn't exist - insert new record
                    System.out.println("New player, inserting record");
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, player.getUsername());
                        insertStmt.setInt(2, player.getSkor());
                        insertStmt.setInt(3, player.getCount());
                        int rowsAffected = insertStmt.executeUpdate();
                        System.out.println("Inserted " + rowsAffected + " rows");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving/updating player: " + e.getMessage());
            // Log the full stack trace for debugging
            e.printStackTrace();
        }
    }    
    
    // Retrieves a player by username from the database
    public Player getPlayer(String username) {
        String query = "SELECT * FROM thasil WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Set parameter and execute query
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            // If player found, create and return Player object
            if (rs.next()) {
                Player player = new Player();
                player.setUsername(rs.getString("username"));
                player.setSkor(rs.getInt("skor"));
                player.setCount(rs.getInt("count"));
                return player;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving player: " + e.getMessage());
        }
        
        // Return null if player not found or error occurred
        return null;
    }
    
}