/******************************************
 * Filename		: Lasso.java
 * Description	: model class representing the lasso weapon used to catch skill balls.
 *                handles lasso throwing mechanics, animation, and collision detection.
 * Project      : Catch the balls game
 * Programmer	: Mochamad Zidan Rusdhiana 
 * Date			: 2025-06-17
******************************************/
package model;

public class Lasso {
    // position coordinates
    private int startX;             // starting x position of lasso (character position)
    private int startY;             // starting y position of lasso (character position)
    private int endX;               // current end x position of lasso
    private int endY;               // current end y position of lasso
    
    // lasso state properties
    private boolean isActive;       // whether lasso is currently thrown
    private boolean isRetracting;   // whether lasso is returning to character
    private int maxLength;          // maximum reach distance of lasso
    private double angle;           // angle of lasso throw direction
    private int currentLength;      // current extended length of lasso
    private int speed;              // speed of lasso extension and retraction    

    // constructor to initialize lasso with starting position    
    public Lasso(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = startX;
        this.endY = startY;        
        this.isActive = false;
        this.isRetracting = false;
        this.maxLength = 450; // Maximum lasso length
        this.currentLength = 0;
        this.speed = 25; // Increased extension speed
    }
      // throws lasso towards target coordinates
    public void throwLasso(int targetX, int targetY) {
        if (!isActive) {
            isActive = true;
            isRetracting = false;
            angle = Math.atan2(targetY - startY, targetX - startX);
            currentLength = 0;
            System.out.println("Lasso thrown towards (" + targetX + ", " + targetY + ") at angle " + Math.toDegrees(angle));
        } else {
            System.out.println("Lasso already active, cannot throw again");
        }
    }    // updates lasso position and state each frame
    public void update() {
        if (isActive) {
            if (!isRetracting && currentLength < maxLength) {
                // extend lasso towards target
                currentLength += speed;
                endX = startX + (int)(Math.cos(angle) * currentLength);
                endY = startY + (int)(Math.sin(angle) * currentLength);
                
                // check if lasso reached maximum length
                if (currentLength >= maxLength) {
                    isRetracting = true;
                    System.out.println("Lasso reached max length, starting retraction");
                }

            } else if (isRetracting) {
                // retract lasso back to character
                currentLength -= speed;
                
                if (currentLength <= 0) {
                    // lasso fully retracted
                    isActive = false;
                    isRetracting = false;
                    currentLength = 0;
                    endX = startX;
                    endY = startY;
                    System.out.println("Lasso fully retracted and deactivated");
                } else {
                    // update end position during retraction
                    endX = startX + (int)(Math.cos(angle) * currentLength);
                    endY = startY + (int)(Math.sin(angle) * currentLength);
                }
            }
        }
    }
    // getters and setters for lasso properties
    
    // position getters and setters
    public int getStartX() { 
        return startX; 
    }
    
    public void setStartX(int startX) { 
        this.startX = startX; 
    }
    
    public int getStartY() { 
        return startY; 
    }
    
    public void setStartY(int startY) { 
        this.startY = startY; 
    }
    
    public int getEndX() { 
        return endX; 
    }
    
    public void setEndX(int endX) { 
        this.endX = endX; 
    }
    
    public int getEndY() { 
        return endY; 
    }
    
    public void setEndY(int endY) { 
        this.endY = endY; 
    }
    
    // state getters and setters
    public boolean isActive() { 
        return isActive; 
    }
    
    public void setActive(boolean active) { 
        isActive = active; 
    }
    
    public boolean isRetracting() { 
        return isRetracting; 
    }
    
    public void setRetracting(boolean retracting) { 
        isRetracting = retracting; 
    }
    
    public int getCurrentLength() { 
        return currentLength; 
    }
    
    public void setCurrentLength(int currentLength) { 
        this.currentLength = currentLength; 
    }    public int getMaxLength() { 
        return maxLength; 
    }
}

