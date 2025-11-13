/******************************************
 * Filename		: GameViewModel.java
 * Description	: ViewModel class for managing the game state and logic.
 * Project      : Catch the balls game
 * Programmer	: Mochamad Zidan Rusdhiana
 * Date			: 2025-06-17
******************************************/
package viewmodel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;
import model.*;

public class GameViewModel implements ActionListener {
    // main game objects
    private GameCharacter character;
    private Lasso lasso;
    private final List<SkillBall> skillBalls;
    private Player currentPlayer;
    private final DatabaseService databaseService;
    private Basket basket;
    
    // game control variables
    private Timer gameTimer;
    private final Random random;
    private final int gameWidth;
    private final int gameHeight;
    private long lastBallSpawn;
    private boolean gameRunning;
    private boolean gameOver = false;
    
    // gas planet tracking
    private int gasPlanetCount = 0;
    private static final int MAX_GAS_PLANETS = 3;
    
    // constructor - initialize game with screen dimensions
    public GameViewModel(int gameWidth, int gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.databaseService = new DatabaseService();        
        this.skillBalls = new ArrayList<>();
        this.random = new Random();
        this.lastBallSpawn = System.currentTimeMillis();
        this.gameRunning = false;
        
        initializeGame();
    }
    
    // initialize game objects
    private void initializeGame() {
        character = new GameCharacter(gameWidth / 2, gameHeight / 2);
        lasso = new Lasso(character.getX() + character.getWidth() / 2, 
                         character.getY() + character.getHeight() / 2);
        // enlarged basket size for better visibility
        basket = new Basket(gameWidth - 300, gameHeight / 2 - 140, 280, 280);
        gameTimer = new Timer(16, this); // ~60 fps
    }
    
    // start game with username
    public void startGame(String username) {
        currentPlayer = new Player(username, 0, 0);
        character.setHp(3); // reset hp using character's hp system
        gasPlanetCount = 0; // reset gas planet count
        
        // test database connection at game start
        if (!databaseService.testConnection()) {
            System.err.println("Warning: Database connection failed. Scores may not be saved.");
        }
        
        gameRunning = true;
        gameTimer.start();
    }    
    // stop game and save player data
    public void stopGame() {
        gameRunning = false;
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        // save player data to database
        if (currentPlayer != null && (currentPlayer.getSkor() > 0 || currentPlayer.getCount() > 0)) {
            System.out.println("Saving game data for player: " + currentPlayer.getUsername());
            databaseService.saveOrUpdatePlayer(currentPlayer);
        } else {
            System.out.println("No game data to save");
        }
    }
    
    // timer action handler
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameRunning) return;
        
        updateGame();
    }
    
    // main game update loop
    private void updateGame() {
        // update character animation
        character.updateAnimation();
        
        // update lasso position
        lasso.setStartX(character.getX() + character.getWidth() / 2);
        lasso.setStartY(character.getY() + character.getHeight() / 2);
        lasso.update();
        
        // update basket animation
        if (basket != null) {
            basket.updateAnimation();
        }        // move balls and handle scoring (optimized - move all balls that are not fully processed)
        for (int i = skillBalls.size() - 1; i >= 0; i--) {
            SkillBall ball = skillBalls.get(i);
            
            // move ball if it's not collected or if it's in cutscene or moving to basket
            if (!ball.isCollected() || ball.isInCutscene() || ball.isMovingToBasket()) {
                ball.move(); // this now includes animation update            
                // force complete basket entry if it's taking too long
                if (ball.isMovingToBasket() && ball.shouldForceBasketEntry()) {
                    ball.forceCompleteBasketEntry();
                }
            }
            
            // check every frame for ready to score balls
            if (ball.isReadyToScore() && !ball.isAlreadyScored()) {
                processBallScore(ball);
                ball.setAlreadyScored(true);
            }
            
            // remove balls that completed cutscene
            if (!ball.isInCutscene() && ball.isCollected()) {
                skillBalls.remove(i);
                continue;
            }
            
            // only remove balls that are off screen and not in cutscene and not moving to basket and not collected
            if (!ball.isInCutscene() && !ball.isMovingToBasket() && !ball.isCollected() && 
                (ball.getX() < -100 || ball.getX() > gameWidth + 100 || 
                 ball.getY() < -100 || ball.getY() > gameHeight + 100)) {
                skillBalls.remove(i);
            }
        }
        
        // spawn new balls
        spawnBalls();
        
        // check lasso collisions
        checkLassoCollisions();
    }

    // spawn new skill balls
    private void spawnBalls() {
        long currentTime = System.currentTimeMillis();
        
        // limit maximum number of balls on screen for better performance
        if (skillBalls.size() >= 10) {
            return;
        }
        
        if (currentTime - lastBallSpawn > 1500) { // spawn every 1.5 seconds
            Color color = getRandomColor();
            SkillBall newBall = null;
            
            // spawn direction (top or bottom)
            int spawnDirection = random.nextInt(2); // 0=top, 1=bottom
            
            // define vertical spawn ranges (not too close to edges)
            int topSpawnY = gameHeight / 6; // 1/6 of the screen height from top
            int bottomSpawnY = gameHeight - (gameHeight / 5); // 1/5 of the screen height from bottom
            
            switch (spawnDirection) {
                case 0 -> { // top row - moving left
                    // for top balls, spawn at the right edge
                    newBall = new SkillBall(
                        gameWidth + 50, // start off-screen to the right
                        random.nextInt(topSpawnY / 2) + (topSpawnY / 2), // random y in the top third
                        color, 
                        false // moving left
                    );
                    newBall.setSpeed(2 + random.nextInt(3)); // speed 2-4
                    
                    // no vertical movement
                    newBall.setMovingDown(false);
                    newBall.setVerticalSpeed(0);
                }
                case 1 -> { // bottom row - moving right
                    // for bottom balls, spawn at the left edge
                    newBall = new SkillBall(
                        -50, // start off-screen to the left
                        random.nextInt(topSpawnY / 2) + bottomSpawnY, // random y in the bottom third
                        color, 
                        true // moving right
                    );
                    newBall.setSpeed(2 + random.nextInt(3)); // speed 2-4
                    
                    // no vertical movement
                    newBall.setMovingDown(false);
                    newBall.setVerticalSpeed(0);
                }
            }
            
            if (newBall != null) {
                skillBalls.add(newBall);
            }
            
            lastBallSpawn = currentTime;
        }
    }
      // process ball scoring when it actually reaches the basket
    private void processBallScore(SkillBall ball) {
        // process scoring based on ball type
        if ("gas_planet".equals(ball.getBallType())) {
            gasPlanetCount++;
            character.takeDamage(1);
            System.out.println("Gas planet entered basket! HP reduced to: " + character.getHp());
        
            if (!character.isAlive()) {
                System.out.println("Game over! HP reduced to 0.");
                stopGame();
                gameOver = true;
            }
        } else {
            // add score for non-gas planets
            currentPlayer.setSkor(currentPlayer.getSkor() + ball.getValue());
            System.out.println(ball.getBallType() + " entered basket! Score: +" + ball.getValue() + " = " + currentPlayer.getSkor());
        }
        
        currentPlayer.setCount(currentPlayer.getCount() + 1);
        System.out.println("Ball scored! Total score: " + currentPlayer.getSkor());
    }

    // get random color for skill balls
    private Color getRandomColor() {
        Color[] colors = {
            Color.BLUE, Color.GREEN, Color.RED, Color.ORANGE, 
            Color.MAGENTA, Color.CYAN, Color.PINK, Color.YELLOW
        };
        return colors[random.nextInt(colors.length)];
    }
    // check lasso collisions with skill balls
    private void checkLassoCollisions() {
        if (!lasso.isActive()) return;
        
        int lassoX = lasso.getEndX();
        int lassoY = lasso.getEndY();
        int radiusSquared = 60 * 60;
        
        for (int i = 0; i < skillBalls.size(); i++) {
            SkillBall ball = skillBalls.get(i);
            if (!ball.isCollected() && !ball.isInCutscene()) {
                int dx = lassoX - ball.getX();
                int dy = lassoY - ball.getY();
                int distanceSquared = dx * dx + dy * dy;                  
                if (distanceSquared < radiusSquared) {
                    // ball caught! start cutscene animation
                    ball.startCutscene(basket.getX() + basket.getWidth()/2, basket.getY() + basket.getHeight()/2);
                    // Immediately reset lasso for next throw instead of retracting
                    lasso.setActive(false);
                    lasso.setCurrentLength(0);
                    System.out.println("Ball caught! Starting cutscene animation...");
                    break;
                }
            }        
        }
        // Let lasso extend fully before retracting if missed
        // Only check for missed lasso when it reaches max length
        if (lasso.isActive() && !lasso.isRetracting() && lasso.getCurrentLength() >= lasso.getMaxLength() - 10) {
            boolean ballCaught = false;
            for (SkillBall ball : skillBalls) {
                if (ball.isInCutscene()) {
                    ballCaught = true;
                    break;
                }
            }
            if (!ballCaught) {
                // Give player time to see the lasso at full extension before retracting
                lasso.setRetracting(true);
                System.out.println("Lasso missed targets, now retracting...");
            }
        }
    }
    
    // character movement methods
    public void moveCharacterUp() {
        if (character.getY() > 0) {
            character.moveUp();
        }
    }
    
    public void moveCharacterDown() {
        if (character.getY() < gameHeight - character.getHeight()) {
            character.moveDown();
        }
    }
    
    public void moveCharacterLeft() {
        if (character.getX() > 0) {
            character.moveLeft();
        }
    }
    
    public void moveCharacterRight() {
        if (character.getX() < gameWidth - character.getWidth()) {
            character.moveRight();
        }
    }
    
    public void setCharacterIdle() {
        character.setIdle();
    }
      // throw lasso at target position
    public void throwLasso(int mouseX, int mouseY) {
        System.out.println("throwLasso called with coordinates (" + mouseX + ", " + mouseY + ")");
        System.out.println("Lasso active status: " + lasso.isActive() + ", Lasso retracting: " + lasso.isRetracting());
        
        if (!lasso.isActive()) {
            lasso.throwLasso(mouseX, mouseY);
            System.out.println("Lasso throw command sent to lasso object");
        } else {
            System.out.println("Lasso is already active, cannot throw");
        }
    }
    
    // getter methods
    public Basket getBasket() {
        return basket;
    }
    
    public GameCharacter getCharacter() {
        return character;
    }
    
    public Lasso getLasso() {
        return lasso;
    }
    
    public List<SkillBall> getSkillBalls() {
        return skillBalls;
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getGasPlanetCount() {
        return gasPlanetCount;
    }
    
    public int getPlayerHP() {
        return character.getHp();
    }
}
