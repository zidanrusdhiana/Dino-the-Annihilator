/******************************************
 * Filename		: Basket.java
 * Description	: Model class representing the game basket (blackhole) that collects skill balls (planets and galaxy).
 *                Handles blackhole images with smooth animation cycling.
 * Project      : Catch the balls game
 * Programmer	: Mochamad Zidan Rusdhiana
 * Date			: 2025-06-17
******************************************/
package model;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class Basket {
    // Position and size properties
    private int x;                              // x coordinate of the basket
    private int y;                              // y coordinate of the basket
    private int width;                          // width of the basket
    private int height;                         // height of the basket
    
    // Image and animation properties
    private Image blackholeImage;               // current displayed image
    private Image[] animationFrames;            // array storing all animation frames
    private int currentFrame = 0;               // current animation frame index
    private long lastFrameTime = 0;             // timestamp of last frame change
    private static final int FRAME_DELAY = 50;  // milliseconds between animation frames
    
    public Basket(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        loadImage();
    }

    private void loadImage() {
        try {
            // load animated spritesheet first
            ImageIcon spriteIcon = new ImageIcon("assets/blackhole_sp.png");
            
            // Check if spritesheet loaded successfully
            if (spriteIcon.getIconWidth() > 0) {
                System.out.println("Successfully loaded blackhole spritesheet from: assets/blackhole_sp.png");
                createAnimationFrames(spriteIcon);
            } else {
                // Fall back to static image if spritesheet fails
                loadStaticImage();
            }
        } catch (Exception e) {
            System.err.println("Failed to load blackhole spritesheet, trying static image...");
            loadStaticImage();
        }
    }
    
    private void createAnimationFrames(ImageIcon spriteIcon) {
        try {
            Image spriteSheet = spriteIcon.getImage();
            animationFrames = new Image[50]; // standard 50 frames for blackhole animation

            // calculate frame dimensions
            int frameWidth = spriteSheet.getWidth(null) / 50;
            int frameHeight = spriteSheet.getHeight(null);
            
            System.out.println("Blackhole spritesheet dimensions: " + spriteSheet.getWidth(null) + 
                             "x" + frameHeight + ", frame width: " + frameWidth);
            
            // extract and scale each frame from the spritesheet
            for (int i = 0; i < 50; i++) {
                // Create buffered image for each frame
                BufferedImage frameImg = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = frameImg.createGraphics();
                
                // Extract specific frame from spritesheet
                g.drawImage(spriteSheet, 0, 0, frameWidth, frameHeight, 
                           i * frameWidth, 0, (i + 1) * frameWidth, frameHeight, null);
                g.dispose();
                
                // Scale frame to basket size with smooth scaling
                animationFrames[i] = frameImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            }
            
            // set first frame as initial display image
            blackholeImage = animationFrames[0];
            System.out.println("Created " + animationFrames.length + " animation frames for blackhole");
            
        } catch (Exception e) {
            System.err.println("Error creating animation frames: " + e.getMessage());
            loadStaticImage();
        }
    }

    private void loadStaticImage() {
        try {
            ImageIcon icon = new ImageIcon("assets/blackhole.png");
            
            if (icon.getIconWidth() > 0) {
                blackholeImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                animationFrames = null; // No animation frames for static image
                System.out.println("Successfully loaded blackhole static image from: assets/blackhole.png");
            } else {
                System.err.println("Failed to load blackhole.png from assets/ directory");
                blackholeImage = null;
                animationFrames = null;
            }
        } catch (Exception e) {
            System.err.println("Failed to load static blackhole image: " + e.getMessage());
            blackholeImage = null;
            animationFrames = null;
        }
    }    

    // check if a point is within the basket's area
    public boolean contains(int pointX, int pointY) {
        return pointX >= x && pointX <= x + width && 
               pointY >= y && pointY <= y + height;
    }
    
    public void updateAnimation() {
        if (animationFrames != null) {
            long currentTime = System.currentTimeMillis();
            // Check if enough time has passed for next frame
            if (currentTime - lastFrameTime > FRAME_DELAY) {
                // Cycle to next frame (loops back to 0 after last frame)
                currentFrame = (currentFrame + 1) % animationFrames.length;
                blackholeImage = animationFrames[currentFrame];
                lastFrameTime = currentTime;
            }
        }
    }
    
    // getters and setters

    // getter and setter for x and y coordinates
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
    
    // getters and setters for width and height
    public int getWidth() { 
        return width; 
    }
    
    public void setWidth(int width) { 
        this.width = width; 
    }
    
    public int getHeight() { 
        return height; 
    }
    
    public void setHeight(int height) { 
        this.height = height; 
    }
    
    // getter for the current blackhole image
    public Image getBlackholeImage() { 
        return blackholeImage; 
    }
}
