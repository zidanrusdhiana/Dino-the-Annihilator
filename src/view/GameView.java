/******************************************
 * Filename		: GameView.java
 * Description	: Main game view class that handles rendering the game state
 * Project      : Catch the balls game
 * Programmer	: Mochamad Zidan Rusdhiana
 * Date			: 2025-06-17
******************************************/
package view;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import model.AudioService;
import model.GameCharacter;
import model.Lasso;
import model.SkillBall;
import viewmodel.GameViewModel;

public class GameView extends JFrame {
    // game window dimensions
    private static final int GAME_WIDTH = 1280;
    private static final int GAME_HEIGHT = 720;    // view model for game logic
    private final GameViewModel viewModel;
    // audio service for background music
    private final AudioService audioService;
    // reference to main menu
    private final MainView mainView;
    // main game rendering panel
    private GamePanel gamePanel;
    // set of currently pressed keys
    private final Set<Integer> pressedKeys;
    // timer for ui updates
    private Timer uiTimer;
    // pixel font for ui text
    private Font pixeloidFont;    // constructor - initializes game window with username
    public GameView(String username, MainView mainView) {
        this.mainView = mainView;
        this.pressedKeys = new HashSet<>();
        this.viewModel = new GameViewModel(GAME_WIDTH, GAME_HEIGHT);
        this.audioService = new AudioService();
        
        loadPixeloidFont();
        initializeComponents();
        setupLayout();
        setupKeyListeners();
        
        // start playing battle background music
        audioService.playBackgroundMusic("assets/battle.wav");
        
        viewModel.startGame(username);
        setVisible(true);
    }

    private void initializeComponents() {
        setTitle("Dino the annihilator - Game");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(GAME_WIDTH, GAME_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        
        gamePanel = new GamePanel();
        
        // update timer for ui - ~60 fps
        uiTimer = new Timer(16, e -> updateUI());
        uiTimer.start();
    }
    
    // setup main layout
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
    }
    
    // setup keyboard input handling
    private void setupKeyListeners() {
        setFocusable(true);
        requestFocusInWindow();
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
                
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    exitGame();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });
        
        // window closing handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitGame();
            }
        });
    }

    // exit game and return to main menu
    private void exitGame() {        
        if (uiTimer != null && uiTimer.isRunning()) {
            uiTimer.stop();
        }        if (viewModel != null) {
            viewModel.stopGame();
        }
        // stop battle music
        audioService.stopMusic();
        dispose();
        mainView.setVisible(true);
        mainView.refreshScoreData();
    }    

    // update ui and handle input
    private void updateUI() {
        // don't allow movement if game is over
        if (viewModel != null && viewModel.isGameOver()) {
            gamePanel.repaint();
            return;
        }
        
        boolean hasMovement = false;
        
        // handle movement based on pressed keys
        if (pressedKeys.contains(KeyEvent.VK_UP) || pressedKeys.contains(KeyEvent.VK_W)) {
            viewModel.moveCharacterUp();
            hasMovement = true;
        }
        if (pressedKeys.contains(KeyEvent.VK_DOWN) || pressedKeys.contains(KeyEvent.VK_S)) {
            viewModel.moveCharacterDown();
            hasMovement = true;
        }
        if (pressedKeys.contains(KeyEvent.VK_LEFT) || pressedKeys.contains(KeyEvent.VK_A)) {
            viewModel.moveCharacterLeft();
            hasMovement = true;
        }
        if (pressedKeys.contains(KeyEvent.VK_RIGHT) || pressedKeys.contains(KeyEvent.VK_D)) {
            viewModel.moveCharacterRight();
            hasMovement = true;
        }
        
        // if no movement keys are pressed, set character to idle
        if (!hasMovement) {
            viewModel.setCharacterIdle();
        }
        
        // always repaint to ensure smooth animation
        gamePanel.repaint();
    }    

    // inner class for game rendering
    private class GamePanel extends JPanel {
        private Image backgroundImage;
        
        // constructor - setup panel and load background
        public GamePanel() {
            setBackground(Color.BLACK);
            setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
            setDoubleBuffered(true); // enable double buffering for smoother rendering
            loadBackgroundImage();
            
            // mouse click handler for lasso throwing
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // don't allow lasso throwing if game is over
                    if (viewModel != null && !viewModel.isGameOver()) {
                        viewModel.throwLasso(e.getX(), e.getY());
                    }
                }
            });
        }
        
        // load background image from assets folder only
        private void loadBackgroundImage() {
            try {
                ImageIcon icon = new ImageIcon("assets/background.png");
                if (icon.getIconWidth() > 0) {
                    backgroundImage = icon.getImage().getScaledInstance(GAME_WIDTH, GAME_HEIGHT, Image.SCALE_SMOOTH);
                    System.out.println("Successfully loaded background image from assets/background.png");
                } else {
                    System.err.println("Failed to load background.png from assets folder");
                    backgroundImage = null;
                }
            } catch (Exception e) {
                System.err.println("Failed to load background image: " + e.getMessage());
                backgroundImage = null;
            }
        }        
        // main paint method for game rendering
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (viewModel == null) return;
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // draw background
            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, GAME_WIDTH, GAME_HEIGHT, this);
            }
            
            // draw score and count display at top-left corner
            g2d.setFont(pixeloidFont.deriveFont(Font.BOLD, 20));
            g2d.setColor(new Color(0, 191, 255)); // bright blue color
            g2d.drawString("SCORE: " + viewModel.getCurrentPlayer().getSkor(), 20, 35);
            g2d.drawString("COUNT: " + viewModel.getCurrentPlayer().getCount(), 20, 65);

            // draw hp as large paw images
            drawPlayerHearts(g2d, 20, 85, viewModel.getPlayerHP());            
            // draw character
            GameCharacter character = viewModel.getCharacter();
            if (character != null && character.getPlayerImage() != null) {
                g2d.drawImage(character.getPlayerImage(), character.getX(), character.getY(), 
                             character.getWidth(), character.getHeight(), this);
            } else if (character != null) {
                g2d.setColor(Color.BLUE);
                g2d.fillRect(character.getX(), character.getY(), character.getWidth(), character.getHeight());
                g2d.setColor(Color.WHITE);
                g2d.drawRect(character.getX(), character.getY(), character.getWidth(), character.getHeight());
            }
            
            // draw lasso
            Lasso lasso = viewModel.getLasso();
            if (lasso != null && lasso.isActive()) {
                g2d.setColor(Color.YELLOW);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(lasso.getStartX(), lasso.getStartY(), lasso.getEndX(), lasso.getEndY());
                g2d.fillOval(lasso.getEndX() - 5, lasso.getEndY() - 5, 10, 10);
            }
              // draw skill balls (show both normal balls and balls in cutscene)
            for (SkillBall ball : viewModel.getSkillBalls()) {
                // show ball if it's not collected or if it's in cutscene or moving to basket
                if (!ball.isCollected() || ball.isInCutscene() || ball.isMovingToBasket()) {
                    int ballSize = ball.getBallSize();
                    int bx = ball.getX() - ballSize/2;
                    int by = ball.getY() - ballSize/2;                    // draw lasso rope if ball is in cutscene
                    if (ball.isInCutscene() && ball.isLassoed() && character != null) {
                        g2d.setColor(Color.ORANGE);
                        g2d.setStroke(new BasicStroke(4));
                        
                        // draw rope from character to ball
                        int charCenterX = character.getX() + character.getWidth() / 2;
                        int charCenterY = character.getY() + character.getHeight() / 2;
                        int ballCenterX = ball.getX();
                        int ballCenterY = ball.getY();
                        
                        g2d.drawLine(charCenterX, charCenterY, ballCenterX, ballCenterY);
                        
                        // draw lasso loop around ball
                        g2d.setColor(Color.YELLOW);
                        g2d.setStroke(new BasicStroke(2));
                        int loopSize = ballSize + 10;
                        g2d.drawOval(ballCenterX - loopSize/2, ballCenterY - loopSize/2, loopSize, loopSize);
                    }
                
                    // draw the ball itself
                    if (ball.getBallImage() != null) {
                        g2d.drawImage(ball.getBallImage(), bx, by, ballSize, ballSize, this);
                    } else {
                        g2d.setColor(ball.getColor());
                        g2d.fillOval(bx, by, ballSize, ballSize);
                        g2d.setColor(Color.WHITE);
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawOval(bx, by, ballSize, ballSize);
                    }
                }
            }
            // draw basket (blackhole)
            if (viewModel.getBasket() != null) {
                if (viewModel.getBasket().getBlackholeImage() != null) {
                    g2d.drawImage(viewModel.getBasket().getBlackholeImage(), 
                                  viewModel.getBasket().getX(), 
                                  viewModel.getBasket().getY(), 
                                  viewModel.getBasket().getWidth(), 
                                  viewModel.getBasket().getHeight(), this);
                } else {
                    // draw fallback rectangle for basket
                    g2d.setColor(Color.BLACK);
                    g2d.fillOval(viewModel.getBasket().getX(), 
                                viewModel.getBasket().getY(), 
                                viewModel.getBasket().getWidth(), 
                                viewModel.getBasket().getHeight());
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawOval(viewModel.getBasket().getX(), 
                                viewModel.getBasket().getY(), 
                                viewModel.getBasket().getWidth(), 
                                viewModel.getBasket().getHeight());
                }
            }
            
            // draw game over screen when needed
            if (viewModel.isGameOver()) {
                drawGameOverScreen(g2d);
            }
        }
        
        // draw enhanced game over screen
        private void drawGameOverScreen(Graphics2D g2d) {
            // dark overlay with fade effect
            g2d.setColor(new Color(0, 0, 0, 220));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // game over panel background
            int panelWidth = 550;
            int panelHeight = 350;
            int panelX = (getWidth() - panelWidth) / 2;
            int panelY = (getHeight() - panelHeight) / 2;
            
            // panel background with gradient-like effect
            g2d.setColor(new Color(15, 15, 35, 250));
            g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 25, 25);
            g2d.setColor(new Color(255, 255, 255, 200));
            g2d.setStroke(new BasicStroke(4));
            g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 25, 25);
            
            // inner border for extra depth
            g2d.setColor(new Color(100, 100, 150, 150));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(panelX + 10, panelY + 10, panelWidth - 20, panelHeight - 20, 15, 15);
            
            // game over title with shadow effect
            g2d.setColor(new Color(100, 0, 0, 150)); // shadow
            g2d.setFont(pixeloidFont.deriveFont(Font.BOLD, 52));
            String gameOverText = "GAME OVER!";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(gameOverText);
            g2d.drawString(gameOverText, panelX + (panelWidth - textWidth) / 2 + 3, panelY + 90 + 3);
            
            g2d.setColor(Color.RED); // main text
            g2d.drawString(gameOverText, panelX + (panelWidth - textWidth) / 2, panelY + 90);
            
            // reason text
            g2d.setColor(Color.YELLOW);
            g2d.setFont(pixeloidFont.deriveFont(Font.BOLD, 22));
            String reasonText = "You lost all your HP!";
            fm = g2d.getFontMetrics();
            textWidth = fm.stringWidth(reasonText);
            g2d.drawString(reasonText, panelX + (panelWidth - textWidth) / 2, panelY + 150);
            
            // final score with emphasis
            g2d.setColor(Color.WHITE);
            g2d.setFont(pixeloidFont.deriveFont(Font.BOLD, 28));
            String scoreText = "Final Score: " + viewModel.getCurrentPlayer().getSkor();
            fm = g2d.getFontMetrics();
            textWidth = fm.stringWidth(scoreText);
            g2d.drawString(scoreText, panelX + (panelWidth - textWidth) / 2, panelY + 200);
            
            // exit instruction
            g2d.setColor(Color.CYAN);
            g2d.setFont(pixeloidFont.deriveFont(Font.PLAIN, 18));
            String exitText = "Press SPACE to return to main menu";
            fm = g2d.getFontMetrics();
            textWidth = fm.stringWidth(exitText);
            g2d.drawString(exitText, panelX + (panelWidth - textWidth) / 2, panelY + 250);
            
            // decorative elements
            g2d.setColor(new Color(255, 215, 0)); // gold color
            g2d.setStroke(new BasicStroke(3));
            // top decorative line
            g2d.drawLine(panelX + 75, panelY + 110, panelX + panelWidth - 75, panelY + 110);
            // bottom decorative line
            g2d.drawLine(panelX + 75, panelY + 220, panelX + panelWidth - 75, panelY + 220);
            
            // corner decorations
            g2d.setColor(Color.RED);
            g2d.fillOval(panelX + 50, panelY + 105, 10, 10);
            g2d.fillOval(panelX + panelWidth - 60, panelY + 105, 10, 10);
            g2d.fillOval(panelX + 50, panelY + 215, 10, 10);
            g2d.fillOval(panelX + panelWidth - 60, panelY + 215, 10, 10);
        }        
        // draw player hp as large paw images
        private void drawPlayerHearts(Graphics g, int x, int y, int hp) {
            GameCharacter character = viewModel.getCharacter();
            if (character != null && character.getPawImage() != null) {
                int pawX = x;
                for (int i = 0; i < hp; i++) {
                    g.drawImage(character.getPawImage(), pawX, y, 40, 40, this);
                    pawX += 45;
                }
            } else {
                // fallback to larger heart symbols if paw image is not available
                int heartX = x;
                for (int i = 0; i < hp; i++) {
                    g.setColor(Color.RED);
                    // larger heart shape
                    g.fillOval(heartX, y, 20, 20);
                    g.fillOval(heartX + 12, y, 20, 20);
                    g.fillPolygon(
                        new int[] {heartX, heartX + 16, heartX + 32},
                        new int[] {y + 12, y + 32, y + 12},
                        3
                    );
                    heartX += 40;
                }
            }
        }
    }    
    // load pixeloid font for ui text
    private void loadPixeloidFont() {
        try {
            // try to get pixeloid mono font from system fonts
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Font[] systemFonts = ge.getAllFonts();
            
            // look for pixeloid font variations
            String[] pixeloidNames = {
                "Pixeloid Mono",
                "PixeloidMono", 
                "Pixeloid-Mono",
                "Pixeloid Sans Mono",
                "PixeloidSansMono",
                "Pixeloid-Sans-Mono"
            };
            
            Font foundFont = null;
            for (Font font : systemFonts) {
                String fontName = font.getName();
                for (String pixeloidName : pixeloidNames) {
                    if (fontName.equalsIgnoreCase(pixeloidName) || 
                        fontName.toLowerCase().contains(pixeloidName.toLowerCase())) {
                        foundFont = font;
                        System.out.println("Found Pixeloid font in system: " + fontName);
                        break;
                    }
                }
                if (foundFont != null) break;
            }
            
            if (foundFont != null) {
                pixeloidFont = foundFont.deriveFont(Font.PLAIN, 12);
                System.out.println("Successfully loaded Pixeloid font from system");
            } else {
                // fallback to monospaced font
                System.err.println("Could not find Pixeloid font, using system monospaced font");
                pixeloidFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
            }
        } catch (Exception e) {
            System.err.println("Error loading font from system: " + e.getMessage());
            pixeloidFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        }
    }
      // cleanup resources when window is closed
    @Override
    public void dispose() {
        // Don't dispose the singleton service, just stop current music
        audioService.stopMusic();
        super.dispose();
    }
}
