/******************************************
 * Filename		: SkillBall.java
 * Description	: model class representing skill balls (planets) in the game.
 *                handles ball movement, animation, collection mechanics, and different ball types.
 * Project      : Catch the balls game
 * Programmer	: Mochamad Zidan Rusdhiana
 * Date			: 2025-06-17
******************************************/
package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class SkillBall {
    // position and movement properties
    private int x;                          // x coordinate of ball
    private int y;                          // y coordinate of ball
    private int value;                      // point value when collected
    private Color color;                    // ball color for rendering
    private boolean isCollected;            // whether ball has been collected
    private boolean movingRight;            // direction of horizontal movement
    private boolean movingDown;             // direction of vertical movement
    private int speed;                      // horizontal movement speed
    private int verticalSpeed;              // vertical movement speed
    
    // visual and animation properties
    private Image ballImage;                // current displayed image
    private Image[] animationFrames;        // array of animation frames
    private int currentFrame = 0;           // current animation frame index
    private long lastFrameTime = 0;         // timestamp of last frame change
    private static final int FRAME_DELAY = 50; // milliseconds between animation frames
    private String ballType;                // ball type: "earth", "galaxy", "gas_planet", "ice_planet"
      // basket movement properties
    private boolean isMovingToBasket;       // whether ball is moving toward basket
    private int targetX, targetY;           // target coordinates (basket position)
    private double moveSpeedX, moveSpeedY;  // movement speed components toward basket
    private boolean guaranteedCollection = false; // ensures ball reaches basket
    // timing properties for basket movement
    private long basketMovementStartTime = 0;                   // start time of basket movement
      // cutscene animation properties
    private boolean isInCutscene = false;    // whether ball is in cutscene animation
    private boolean isLassoed = false;       // whether ball is being lassoed
    private long cutsceneStartTime;          // start time of cutscene
    private static final long CUTSCENE_DURATION = 800; // 0.8 seconds for faster cutscene
    private double cutsceneStartX, cutsceneStartY; // starting position for cutscene
    private double lassoLength = 0;          // current length of lasso rope
    private boolean readyToScore = false;    // flag to indicate ball is ready to be scored
    private boolean alreadyScored = false;   // flag to prevent double scoring
    // constructor to create ball with initial position and movement direction
    public SkillBall(int x, int y, Color color, boolean movingRight) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.movingRight = movingRight;
        this.movingDown = false;
        this.isCollected = false;
        this.speed = 2;
        this.verticalSpeed = 0;
        this.isMovingToBasket = false;
        
        // randomly assign ball type and corresponding value
        assignRandomBallType();
        loadImage();
    }
    
    // assigns random ball type with different probabilities and values
    private void assignRandomBallType() {
        double random = Math.random();
        if (random < 0.05) { // 5% chance
            ballType = "galaxy";
            value = 200; // highest value
        } else if (random < 0.3) { // 25% chance  
            ballType = "gas_planet";
            value = 0; // gas planets don't give points, they reduce HP
        } else if (random < 0.5) { // 20% chance
            ballType = "ice_planet"; 
            value = 75;
        } else { // 50% chance
            ballType = "earth";
            value = 50; // base value
        }
    }    
    // loads ball images from assets folder (spritesheet preferred, static fallback)
    private void loadImage() {
        try {
            int ballSize = getBallSize();
            
            // try to load animated spritesheet first
            ImageIcon spriteIcon = new ImageIcon("assets/" + ballType + "_sp.png");
            
            if (spriteIcon.getIconWidth() > 0) {
                System.out.println("Successfully loaded " + ballType + " spritesheet from assets/");
                createAnimationFrames(spriteIcon, ballSize);
            } else {
                // fall back to static image if spritesheet not found
                loadStaticImage(ballSize);
            }
        } catch (Exception e) {
            System.err.println("Failed to load " + ballType + " image: " + e.getMessage());
            ballImage = null;
            animationFrames = null;
        }
    }
    
    // creates animation frames from spritesheet
    private void createAnimationFrames(ImageIcon spriteIcon, int ballSize) {
        try {
            Image spriteSheet = spriteIcon.getImage();
            animationFrames = new Image[50]; // 50 frames standard
            
            // calculate frame dimensions
            int frameWidth = spriteSheet.getWidth(null) / 50;
            int frameHeight = spriteSheet.getHeight(null);
            
            System.out.println("Spritesheet dimensions: " + spriteSheet.getWidth(null) + 
                             "x" + frameHeight + ", frame width: " + frameWidth);
            
            // extract each frame from the spritesheet
            for (int i = 0; i < 50; i++) {
                // create buffered image for each frame
                BufferedImage frameImg = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = frameImg.createGraphics();
                
                // draw specific part of spritesheet
                g.drawImage(spriteSheet, 0, 0, frameWidth, frameHeight, 
                           i * frameWidth, 0, (i + 1) * frameWidth, frameHeight, null);
                g.dispose();
                
                // scale frame to ball size
                animationFrames[i] = frameImg.getScaledInstance(ballSize, ballSize, Image.SCALE_SMOOTH);
            }
            
            // set first frame as current image
            ballImage = animationFrames[0];
            System.out.println("Created " + animationFrames.length + " animation frames for " + ballType);
            
        } catch (Exception e) {
            System.err.println("Error creating animation frames: " + e.getMessage());
            loadStaticImage(ballSize);
        }
    }
    
    // loads static image as fallback when spritesheet unavailable
    private void loadStaticImage(int ballSize) {
        try {
            ImageIcon icon = new ImageIcon("assets/" + ballType + ".png");
            
            if (icon.getIconWidth() > 0) {
                ballImage = icon.getImage().getScaledInstance(ballSize, ballSize, Image.SCALE_SMOOTH);
                animationFrames = null; // no animation frames
                System.out.println("Successfully loaded " + ballType + " static image from assets/");
            } else {
                System.err.println("Failed to load " + ballType + " images from assets/");
                ballImage = null;
                animationFrames = null;
            }
        } catch (Exception e) {
            System.err.println("Failed to load static " + ballType + " image: " + e.getMessage());
            ballImage = null;
            animationFrames = null;
        }
    }    
    // updates ball position and handles movement logic
    public void move() {        
        if (isInCutscene) {
            // handle cutscene animation
            long elapsedTime = System.currentTimeMillis() - cutsceneStartTime;
            double progress = Math.min(1.0, (double) elapsedTime / CUTSCENE_DURATION);
            
            if (progress < 0.2) {
                // phase 1: extend lasso (20% of animation)
                double maxDistance = Math.sqrt(Math.pow(targetX - cutsceneStartX, 2) + Math.pow(targetY - cutsceneStartY, 2));
                lassoLength = maxDistance * (progress / 0.2);
            } else if (progress < 0.8) {
                // phase 2: pull ball to basket (60% of animation)
                double pullProgress = (progress - 0.2) / 0.6;
                // smooth easing for pull animation
                double easedProgress = 1 - Math.pow(1 - pullProgress, 3);
                
                x = (int) (cutsceneStartX + (targetX - cutsceneStartX) * easedProgress);
                y = (int) (cutsceneStartY + (targetY - cutsceneStartY) * easedProgress);
                  // Set readyToScore lebih awal untuk mengurangi delay - saat ball 20% menuju keranjang
                if (pullProgress > 0.2 && !readyToScore) {
                    readyToScore = true;
                    System.out.println(ballType + " ball is ready to score!");
                }
            } else {
                // cutscene finished - ball enters basket
                x = targetX;
                y = targetY;
                isInCutscene = false;
                isLassoed = false;
                isCollected = true;
                System.out.println(ballType + " ball successfully entered the basket via cutscene!");
            }
        } else if (isMovingToBasket) {
            // calculate current distance to target
            double currentDistance = Math.sqrt(Math.pow(x - targetX, 2) + Math.pow(y - targetY, 2));
            
            if (guaranteedCollection) {
                // for guaranteed collection, use more precise movement
                if (currentDistance > 5) { // still moving towards target
                    // calculate step size - smaller as we get closer
                    double stepFactor = Math.min(1.0, currentDistance / 50.0);
                    int stepX = (int) (moveSpeedX * stepFactor);
                    int stepY = (int) (moveSpeedY * stepFactor);
                    
                    // move towards target
                    x += stepX;
                    y += stepY;
                } else {
                    // close enough - snap to exact target position
                    x = targetX;
                    y = targetY;
                    isMovingToBasket = false;
                    isCollected = true;
                    System.out.println(ballType + " ball successfully entered the basket at exact position!");
                }
            } else {
                // normal movement for non-guaranteed balls
                x += (int) moveSpeedX;
                y += (int) moveSpeedY;
            
                // check if reached basket with generous detection
                if (currentDistance < 40) { 
                    isMovingToBasket = false;
                    isCollected = true;
                    System.out.println(ballType + " ball successfully entered the basket!");
                }
            }
        } else if (!isCollected) {
            // normal movement - horizontal and/or vertical
            if (movingRight) {
                x += speed;
            } else {
                x -= speed;
            }
        
            if (movingDown) {
                y += verticalSpeed;
            } else if (verticalSpeed != 0) {
                y -= verticalSpeed;
            }
        }

        // update animation frame
        updateAnimation();
    }    
    // initiates cutscene animation when ball is caught
    public void startCutscene(int basketX, int basketY) {
        if (!isCollected && !isInCutscene) {
            isInCutscene = true;
            isLassoed = true;
            cutsceneStartTime = System.currentTimeMillis();
            cutsceneStartX = x;
            cutsceneStartY = y;
            targetX = basketX;
            targetY = basketY;
            lassoLength = 0;
            
            System.out.println("Starting cutscene for " + ballType + " ball");
        }
    }
    // forces ball to complete basket entry when taking too long
    public void forceCompleteBasketEntry() {
        if (isMovingToBasket) {
            // move to exact target position
            x = targetX;
            y = targetY;
            isMovingToBasket = false;
            isCollected = true;
            System.out.println(ballType + " ball forced entry to basket center!");
        }
    }
    
    // starts timer for basket movement timeout tracking
    public void startBasketMovementTimer() {
        basketMovementStartTime = System.currentTimeMillis();
    }     
    
     // checks if ball should be forced to complete basket entry due to timeout
    public boolean shouldForceBasketEntry() {
        if (!isMovingToBasket) return false;

        // shorter timeout for guaranteed collection balls to reduce waiting time
        long timeout = guaranteedCollection ? 500 : 2000; // 0.5s for guaranteed, 2s for others
        return System.currentTimeMillis() - basketMovementStartTime > timeout;
    }
    // getters and setters for ball properties
    
    // position getters and setters
    public int getX() { 
        return x; 
    }
    
    public void setX(int x) { 
        this.x = x; 
    }
    
    public int getY() { 
        return y; 
    }
    
    public void setY(int y) { 
        this.y = y; 
    }
    
    // value and color getters and setters
    public int getValue() { 
        return value; 
    }
    
    public void setValue(int value) { 
        this.value = value; 
    }
    
    public Color getColor() { 
        return color; 
    }
    
    public void setColor(Color color) { 
        this.color = color; 
    }
    
    // state getters and setters
    public boolean isCollected() { 
        return isCollected; 
    }
    
    public void setCollected(boolean collected) { 
        isCollected = collected; 
    }
    
    // movement direction getters and setters
    public boolean isMovingRight() { 
        return movingRight; 
    }
    
    public void setMovingRight(boolean movingRight) { 
        this.movingRight = movingRight; 
    }
    
    public boolean isMovingDown() { 
        return movingDown; 
    }
    
    public void setMovingDown(boolean movingDown) { 
        this.movingDown = movingDown; 
    }
    
    // speed getters and setters
    public int getSpeed() { 
        return speed; 
    }
    
    public void setSpeed(int speed) { 
        this.speed = speed; 
    }
    
    public int getVerticalSpeed() { 
        return verticalSpeed; 
    }
    
    public void setVerticalSpeed(int verticalSpeed) { 
        this.verticalSpeed = verticalSpeed; 
    }
    
    // image and type getters
    public Image getBallImage() { 
        return ballImage; 
    }
    
    public String getBallType() { 
        return ballType; 
    }
    
    public boolean isMovingToBasket() { 
        return isMovingToBasket; 
    }    
    // utility methods
    
    // returns ball size based on type (galaxy balls are larger)
    public int getBallSize() {
        // galaxy balls are 3x larger than normal balls
        if ("galaxy".equals(ballType)) {
            return 210; // increased size for galaxy balls
        }
        return 75; // normal size for other ball types
    }

    // updates animation frame for animated balls
    public void updateAnimation() {
        if (animationFrames != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameTime > FRAME_DELAY) {
                currentFrame = (currentFrame + 1) % animationFrames.length;
                ballImage = animationFrames[currentFrame];
                lastFrameTime = currentTime;
            }
        }
    }
      // guaranteed collection getters and setters
    public boolean isGuaranteedCollection() { 
        return guaranteedCollection; 
    }
    
    public void setGuaranteedCollection(boolean guaranteed) { 
        this.guaranteedCollection = guaranteed; 
    }
    
    // cutscene animation getters
    public boolean isInCutscene() {
        return isInCutscene;
    }
    
    public boolean isLassoed() {
        return isLassoed;
    }
    
    public double getLassoLength() {
        return lassoLength;
    }
    
    public double getCutsceneStartX() {
        return cutsceneStartX;
    }
      public double getCutsceneStartY() {
        return cutsceneStartY;
    }
      public boolean isReadyToScore() {
        return readyToScore;
    }
    
    public boolean isAlreadyScored() {
        return alreadyScored;
    }
    
    public void setAlreadyScored(boolean scored) {
        this.alreadyScored = scored;
    }
}
