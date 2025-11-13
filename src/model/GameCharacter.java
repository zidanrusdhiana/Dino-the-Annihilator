/******************************************
 * Filename		: GameCharacter.java
 * Description	: model class representing the game character with animation system and HP management.
 *                handles character movement, sprite animations, and health points.
 * Project      : Catch the balls game
 * Programmer	: Mochamad Zidan Rusdhiana 
 * Date			: 2025-06-17
******************************************/
package model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class GameCharacter {
    // animation states for different character movements
    public enum AnimationState {
        IDLE, FORWARD, BACKWARD, UP, DOWN
    }
    
    // position and size properties
    private int x;                      // x coordinate of character
    private int y;                      // y coordinate of character
    private int width;                  // width of character sprite
    private int height;                 // height of character sprite
    private int speed;                  // movement speed in pixels
    private int hp;                     // current health points
    private int maxHp;                  // maximum health points
    
    // animation system properties  
    private Map<AnimationState, Image[]> animations;    // stores animation frames for each state
    private AnimationState currentState;                // current animation state
    private int currentFrame;                          // current frame index
    private long lastFrameTime;                        // timestamp of last frame change
    private int frameDelay;                            // milliseconds between frames
    
    // hp display image
    private Image pawImage;                            // paw image for HP display    
    // constructor to initialize character with position and default values
    public GameCharacter(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 70;
        this.height = 70; 
        this.speed = 5;
        this.hp = 3; // default HP
        this.maxHp = 3;
        
        // animation setup
        this.animations = new HashMap<>();
        this.currentState = AnimationState.IDLE;
        this.currentFrame = 0;
        this.lastFrameTime = System.currentTimeMillis();
        this.frameDelay = 100; // 100ms between frames
        
        loadAnimations();
        loadPawImage();
    }
    
    // loads character animations from sprite sheet
    private void loadAnimations() {
        try {
            BufferedImage spriteSheet = loadSpriteSheet();
            if (spriteSheet != null) {
                System.out.println("Loaded character spritesheet: " + spriteSheet.getWidth() + "x" + spriteSheet.getHeight());
                
                // Sprite sheet dimensions: 640x240
                // 5 rows, each row has different number of frames
                // All frames are 64px wide x 48px tall

                int rowHeight = 48; // 240/5 = 48 pixels per row

                // row 0: idle (10 frames) - each frame is 64px wide
                Image[] idleFrames = extractFramesFromRow(spriteSheet, 0, 10, rowHeight);
                animations.put(AnimationState.IDLE, idleFrames);
                System.out.println("Loaded IDLE animation: " + idleFrames.length + " frames");
                
                // row 1: forward (2 frames) - each frame is 64px wide
                Image[] forwardFrames = extractFramesFromRow(spriteSheet, 1, 2, rowHeight);
                animations.put(AnimationState.FORWARD, forwardFrames);
                System.out.println("Loaded FORWARD animation: " + forwardFrames.length + " frames");
                
                // row 2: backward (3 frames) - each frame is 64px wide
                Image[] backwardFrames = extractFramesFromRow(spriteSheet, 2, 3, rowHeight);
                animations.put(AnimationState.BACKWARD, backwardFrames);
                System.out.println("Loaded BACKWARD animation: " + backwardFrames.length + " frames");
                
                // row 3: down (4 frames) - each frame is 64px wide
                Image[] downFrames = extractFramesFromRow(spriteSheet, 3, 4, rowHeight);
                animations.put(AnimationState.DOWN, downFrames);
                System.out.println("Loaded DOWN animation: " + downFrames.length + " frames");
                
                // row 4: up (3 frames) - each frame is 64px wide
                Image[] upFrames = extractFramesFromRow(spriteSheet, 4, 3, rowHeight);
                animations.put(AnimationState.UP, upFrames);
                System.out.println("Loaded UP animation: " + upFrames.length + " frames");
                
            } else {
                System.err.println("Could not load sprite sheet, using fallback");
                loadFallbackImage();
            }
        } catch (Exception e) {
            System.err.println("Error loading animations: " + e.getMessage());
            e.printStackTrace();
            loadFallbackImage();
        }    
    }
      // loads sprite sheet from assets directory
    private BufferedImage loadSpriteSheet() {
        try {
            File file = new File("assets/player.png");
            if (file.exists()) {
                return ImageIO.read(file);
            } else {
                System.err.println("Failed to load sprite sheet from: assets/player.png");
            }
        } catch (Exception e) {
            System.err.println("Error loading sprite sheet: " + e.getMessage());
        }
        return null;
    }
    
    // extracts animation frames from a specific row in the sprite sheet
    private Image[] extractFramesFromRow(BufferedImage spriteSheet, int row, int frameCount, int rowHeight) {        Image[] frames = new Image[frameCount];
        
        // all frames are 64 pixels wide, regardless of animation type
        int frameWidth = 64; // fixed frame width for all animations
        int startY = row * rowHeight;
        
        for (int i = 0; i < frameCount; i++) {
            int startX = i * frameWidth;
            
            // make sure we don't go out of bounds
            if (startX + frameWidth > spriteSheet.getWidth()) {
                frameWidth = spriteSheet.getWidth() - startX;
            }
            
            BufferedImage frameImage = spriteSheet.getSubimage(startX, startY, frameWidth, rowHeight);
            frames[i] = frameImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        }
          return frames;
    }
      // loads fallback image when sprite sheet is not available
    private void loadFallbackImage() {
        try {
            ImageIcon icon = new ImageIcon("assets/player.png");
            
            if (icon.getIconWidth() > 0) {
                Image fallbackImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                // use fallback image for all states
                for (AnimationState state : AnimationState.values()) {
                    animations.put(state, new Image[]{fallbackImage});
                }
            } else {
                System.err.println("Failed to load fallback image from: assets/player.png");
            }
        } catch (Exception e) {
            System.err.println("Failed to load fallback image: " + e.getMessage());
        }
    }
      // loads paw image for HP display
    private void loadPawImage() {
        try {
            ImageIcon icon = new ImageIcon("assets/paw.png");
            
            if (icon.getIconWidth() > 0) {
                pawImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH); // increased from 30x30 to 40x40
            } else {
                System.err.println("Failed to load paw image from: assets/paw.png");
            }
        } catch (Exception e) {
            System.err.println("Failed to load paw image: " + e.getMessage());
        }
    }
    
    // updates animation frame based on time and current state
    public void updateAnimation() {        
        long currentTime = System.currentTimeMillis();
        
        // set different frame delays for different animations
        int currentFrameDelay = getFrameDelayForState(currentState);
        
        if (currentTime - lastFrameTime > currentFrameDelay) {
            Image[] frames = animations.get(currentState);
            if (frames != null && frames.length > 0) {
                currentFrame = (currentFrame + 1) % frames.length;
                lastFrameTime = currentTime;
            }
        }
    }
    
    // returns frame delay specific to animation state
    private int getFrameDelayForState(AnimationState state) {        
        switch (state) {
            case IDLE:
                return 150; // slower for idle animation
            case FORWARD:
            case BACKWARD:
                return 200; // medium speed for directional movement
            case UP:
            case DOWN:
                return 120; // slightly faster for vertical movement
            default:
                return frameDelay; // default frame delay
        }
    }
    
    // gets current animation frame for rendering
    public Image getCurrentFrame() {
        Image[] frames = animations.get(currentState);
        if (frames != null && frames.length > 0 && currentFrame < frames.length) {
            return frames[currentFrame];
        }        return null;
    }
    
    // sets animation state and resets frame if state changes
    public void setState(AnimationState state) {
        if (this.currentState != state) {
            this.currentState = state;
            this.currentFrame = 0; // reset to first frame of new animation
        }
    }
    
    // returns current player image for rendering
    public Image getPlayerImage() { 
        return getCurrentFrame(); 
    }
    
    // movement methods that update position and animation state
    public void moveUp() {
        y -= speed;
        setState(AnimationState.UP);
    }
    
    public void moveDown() {
        y += speed;
        setState(AnimationState.DOWN);
    }
    
    public void moveLeft() {
        x -= speed;
        setState(AnimationState.BACKWARD);
    }
    
    public void moveRight() {
        x += speed;
        setState(AnimationState.FORWARD);
    }
    
    // sets character to idle state
    public void setIdle() {
        setState(AnimationState.IDLE);
    }
    
    // getters and setters for character properties
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    
    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }
      public int getHp() { return hp; }
    public void setHp(int hp) { 
        this.hp = Math.max(0, Math.min(hp, maxHp)); 
    }
    
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { 
        this.maxHp = maxHp; 
        if (this.hp > maxHp) {
            this.hp = maxHp;
        }
    }
    
    public Image getPawImage() { return pawImage; }
    
    // health management methods
    public void takeDamage(int damage) {
        hp = Math.max(0, hp - damage);
    }
    
    public void heal(int healAmount) {
        hp = Math.min(maxHp, hp + healAmount);
    }
    
    // checks if character is still alive
    public boolean isAlive() {
        return hp > 0;
    }
}
