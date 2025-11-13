/******************************************
 * Filename		: MainViewModel.java
 * Description	: ViewModel class for managing player data operations in the main menu.
 * Project      : Catch the balls game
 * Programmer	: Mochamad Zidan Rusdhiana
 * Date			: 2025-06-17
******************************************/
package viewmodel;

import java.util.List;
import model.DatabaseService;
import model.Player;

public class MainViewModel {
    // database service for player data operations
    private final DatabaseService databaseService;
    
    // constructor - initialize database service
    public MainViewModel() {
        this.databaseService = new DatabaseService();
    }
    
    // get all players from database
    public List<Player> getAllPlayers() {
        return databaseService.getAllPlayers();
    }
    
    // get specific player by username
    public Player getPlayer(String username) {
        return databaseService.getPlayer(username);
    }
    
    // save or update player data
    public void saveOrUpdatePlayer(Player player) {
        databaseService.saveOrUpdatePlayer(player);
    }
    
    // validate username format and length
    public boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty() && username.length() <= 50;
    }
}