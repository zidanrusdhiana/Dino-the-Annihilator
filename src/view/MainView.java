/******************************************
 * Filename		: MainView.java
 * Description	: Main menu view for the game, allowing players to enter their username,
 *                view scores, and start the game.
 * Project      : Catch the balls game
 * Programmer	: Mochamad Zidan Rusdhiana
 * Date			: 2025-06-17
******************************************/
package view;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.AudioService;
import model.Player;
import viewmodel.MainViewModel;

public class MainView extends JFrame {    
    // view model for data operations
    private final MainViewModel viewModel;
    // audio service for background music
    private final AudioService audioService;
    // username input field
    private JTextField usernameField;
    // score display table
    private JTable scoreTable;
    // table model for score data
    private DefaultTableModel tableModel;
    // play game button
    private JButton playButton;
    // quit application button
    private JButton quitButton;
    // background image for ui
    private Image backgroundImage;
    // pixel font for ui text
    private Font pixeloidFont;    // constructor - initializes main menu window
    public MainView() {
        viewModel = new MainViewModel();
        audioService = new AudioService();
        
        loadPixeloidFont();
        loadBackgroundImage();
        initializeComponents();
        setupLayout();
        loadScoreData();
        
        // start playing menu background music
        audioService.playBackgroundMusic("assets/menu.wav");
        
        setVisible(true);
    
    }

    // initialize all ui components
    private void initializeComponents() {
        setTitle("DINO THE ANNIHILATOR");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        // add window closing listener to stop music
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                audioService.stopMusic();
                System.exit(0);
            }
        });
        
        // use custom content pane with background
        setContentPane(new BackgroundPanel());
        
        // username input - make it smaller
        usernameField = new JTextField(12);
        usernameField.setFont(pixeloidFont.deriveFont(Font.PLAIN, 12));
        usernameField.setBackground(new Color(40, 40, 40));
        usernameField.setForeground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        // play button configuration
        playButton = new JButton("PLAY");
        playButton.setFont(pixeloidFont.deriveFont(Font.BOLD, 16));
        playButton.setBackground(Color.WHITE);
        playButton.setForeground(Color.BLACK);
        playButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        playButton.setFocusPainted(false);
        playButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        playButton.setOpaque(true);
        playButton.setContentAreaFilled(true);

        // quit button configuration
        quitButton = new JButton("QUIT");
        quitButton.setFont(pixeloidFont.deriveFont(Font.BOLD, 16));
        quitButton.setBackground(Color.WHITE);
        quitButton.setForeground(Color.BLACK);
        quitButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        quitButton.setFocusPainted(false);
        quitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        quitButton.setOpaque(true);
        quitButton.setContentAreaFilled(true);
        
        // score table configuration
        String[] columnNames = {"Username", "Score", "Count"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scoreTable = new JTable(tableModel);
        scoreTable.setFont(pixeloidFont.deriveFont(Font.PLAIN, 12));
        scoreTable.getTableHeader().setFont(pixeloidFont.deriveFont(Font.BOLD, 14));
        scoreTable.setBackground(Color.WHITE);
        scoreTable.setForeground(new Color(33, 37, 41));
        scoreTable.getTableHeader().setBackground(new Color(220, 220, 220));
        scoreTable.getTableHeader().setForeground(Color.BLACK);
        scoreTable.setSelectionBackground(new Color(0, 123, 255));
        scoreTable.setSelectionForeground(Color.WHITE);
        scoreTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scoreTable.setRowHeight(28);
        scoreTable.setGridColor(new Color(108, 117, 125));
        scoreTable.setShowGrid(true);
        scoreTable.setIntercellSpacing(new Dimension(2, 2));

        // add alternating row colors
        scoreTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {            
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(248, 249, 250)); // light gray for even rows
                    } else {
                        c.setBackground(Color.WHITE); // white for odd rows
                    }
                }
                return c;
            }
        });
        
        // set column widths
        scoreTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        scoreTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        scoreTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        
        // add hover effects to buttons
        playButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                playButton.setBackground(new Color(240, 240, 240)); // light gray on hover
                playButton.setForeground(Color.BLACK);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                playButton.setBackground(Color.WHITE);
                playButton.setForeground(Color.BLACK);
            }
        });
        
        quitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                quitButton.setBackground(new Color(240, 240, 240)); // light gray on hover
                quitButton.setForeground(Color.BLACK);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                quitButton.setBackground(Color.WHITE);
                quitButton.setForeground(Color.BLACK);
            }
        });
          // add action listeners
        playButton.addActionListener(e -> startGame());        
        quitButton.addActionListener(e -> {
            audioService.stopMusic();
            System.exit(0);
        });
    }    
    // setup main layout structure
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // title panel
        JPanel titlePanel = createTransparentPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        JLabel titleLabel = new JLabel("DINO THE ANNIHILATOR", SwingConstants.CENTER);
        titleLabel.setFont(pixeloidFont.deriveFont(Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // main content panel
        JPanel mainPanel = createTransparentPanel();
        mainPanel.setLayout(new GridLayout(1, 2, 20, 0));
        
        // left panel - game info
        JPanel leftPanel = createGameInfoPanel();
        mainPanel.add(leftPanel);
        
        // right panel - score table and input
        JPanel rightPanel = createScorePanel();
        mainPanel.add(rightPanel);
        
        add(mainPanel, BorderLayout.CENTER);

        // add proper margins to the main content
        JPanel paddedMainPanel = createTransparentPanel();
        paddedMainPanel.setLayout(new BorderLayout());
        paddedMainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));
        paddedMainPanel.add(mainPanel, BorderLayout.CENTER);
        add(paddedMainPanel, BorderLayout.CENTER);
        
        // bottom panel
        JPanel bottomPanel = createTransparentPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        bottomPanel.add(quitButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    // create transparent panel helper
    private JPanel createTransparentPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }
    
    // create game information panel
    private JPanel createGameInfoPanel() {
        JPanel panel = createTransparentPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));        
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "GAME INFORMATION",
                0, 0,
                pixeloidFont.deriveFont(Font.BOLD, 16),
                Color.WHITE
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // ball information
        JLabel ballInfoLabel = new JLabel("SKILL BALLS:");
        ballInfoLabel.setFont(pixeloidFont.deriveFont(Font.BOLD, 12));
        ballInfoLabel.setForeground(Color.YELLOW);
        ballInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(ballInfoLabel);
        panel.add(Box.createVerticalStrut(8));
        
        // earth ball
        panel.add(createBallInfoRow("earth", "Earth Ball: +50 points", Color.WHITE));
        panel.add(Box.createVerticalStrut(5));
        
        // ice planet
        panel.add(createBallInfoRow("ice_planet", "Ice Planet: +75 points", Color.CYAN));
        panel.add(Box.createVerticalStrut(5));
        
        // galaxy ball
        panel.add(createBallInfoRow("galaxy", "Galaxy Ball: +200 points", Color.MAGENTA));
        panel.add(Box.createVerticalStrut(5));
        
        // gas planet
        panel.add(createBallInfoRow("gas_planet", "Gas Planet: -1 HP", Color.RED));
        panel.add(Box.createVerticalStrut(15));
        
        // controls information
        JLabel controlsLabel = new JLabel("CONTROLS:");
        controlsLabel.setFont(pixeloidFont.deriveFont(Font.BOLD, 12));
        controlsLabel.setForeground(Color.YELLOW);
        controlsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(controlsLabel);
        panel.add(Box.createVerticalStrut(8));
        
        String[] controls = {
            "Arrow Keys / WASD: Move character",
            "Mouse Click: Throw lasso",
            "SPACE: Quit game",
            "",
            "OBJECTIVE:",
            "Catch skill balls with your lasso!",
            "Avoid gas planets - they reduce HP!",
            "Game ends when HP reaches 0."
        };
        
        for (String control : controls) {
            JLabel controlLabel = new JLabel(control);
            controlLabel.setFont(pixeloidFont.deriveFont(Font.PLAIN, 9));
            if (control.equals("OBJECTIVE:")) {
                controlLabel.setForeground(Color.YELLOW);
                controlLabel.setFont(pixeloidFont.deriveFont(Font.BOLD, 10));
            } else {
                controlLabel.setForeground(Color.WHITE);
            }
            controlLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(controlLabel);
            panel.add(Box.createVerticalStrut(2));
        }
        
        return panel;
    }
    
    // create info row for each ball type
    private JPanel createBallInfoRow(String ballType, String description, Color textColor) {
        JPanel row = createTransparentPanel();
        row.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        // ball image
        ImageIcon ballIcon = new ImageIcon("assets/" + ballType + ".png");
        JLabel imageLabel;
        if (ballIcon.getIconWidth() > 0) {
            Image scaledImage = ballIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(scaledImage));
        } else {
            imageLabel = new JLabel("●");
            imageLabel.setForeground(textColor);
            imageLabel.setFont(pixeloidFont.deriveFont(Font.BOLD, 16));
        }
        
        // description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(pixeloidFont.deriveFont(Font.PLAIN, 9));
        descLabel.setForeground(textColor);
        
        row.add(imageLabel);
        row.add(descLabel);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        return row;
    }
    
    // create score panel with table and input
    private JPanel createScorePanel() {        
        JPanel panel = createTransparentPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "SCORE BOARD",
                0, 0,
                pixeloidFont.deriveFont(Font.BOLD, 16),
                Color.WHITE
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // input panel - better layout
        JPanel inputPanel = createTransparentPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 5));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(pixeloidFont.deriveFont(Font.BOLD, 12));
        usernameLabel.setForeground(Color.WHITE);

        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(playButton); // button right next to input
        
        // table panel
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scrollPane.setPreferredSize(new Dimension(480, 380));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(108, 117, 125), 2),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // load score data from database into table
    private void loadScoreData() {
        tableModel.setRowCount(0);
        List<Player> players = viewModel.getAllPlayers();
        
        for (Player player : players) {
            Object[] row = {
                player.getUsername(),
                player.getSkor(),
                player.getCount()
            };
            tableModel.addRow(row);
        }
    }
    
    // start game with entered username
    private void startGame() {
        String username = usernameField.getText().trim();
        
        if (!viewModel.isValidUsername(username)) {
            // create custom dialog with pixeloid font
            JDialog dialog = new JDialog(this, "Invalid Username", true);
            dialog.setSize(300, 150);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            JLabel messageLabel = new JLabel("<html><center>Please enter a valid username<br>(1-50 characters)</center></html>");
            messageLabel.setFont(pixeloidFont.deriveFont(Font.PLAIN, 12));
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
            
            JButton okButton = new JButton("OK");
            okButton.setFont(pixeloidFont.deriveFont(Font.BOLD, 12));
            okButton.addActionListener(e -> dialog.dispose());
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(okButton);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
            
            dialog.add(messageLabel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
            return;
        }        // hide main window and start game
        setVisible(false);
        // stop menu music before starting game
        audioService.stopMusic();
        // create new game view window - intentionally not stored
        new GameView(username, this);
    }    
    // refresh score data and show main window
    public void refreshScoreData() {
        loadScoreData();
        // restart menu music when returning from game
        audioService.playBackgroundMusic("assets/menu.wav");
        setVisible(true);
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
                System.out.println("Pixeloid font not found in system, checking available fonts...");
                
                // debug: print available font names that might be pixeloid
                for (Font font : systemFonts) {
                    String fontName = font.getName().toLowerCase();
                    if (fontName.contains("pixel") || fontName.contains("mono")) {
                        System.out.println("Potential pixel font found: " + font.getName());
                    }
                }
                
                // fallback to monospaced font
                System.err.println("Could not find Pixeloid font, using system monospaced font");
                pixeloidFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
            }
        } catch (Exception e) {
            System.err.println("Error loading font from system: " + e.getMessage());
            pixeloidFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        }
    }
    
    // load background image from assets folder only
    private void loadBackgroundImage() {
        try {
            ImageIcon icon = new ImageIcon("assets/background.png");
            if (icon.getIconWidth() > 0) {
                backgroundImage = icon.getImage();
                System.out.println("Successfully loaded background from assets/background.png");
            } else {
                System.err.println("Failed to load background.png from assets folder");
                backgroundImage = null;
            }
        } catch (Exception e) {
            System.err.println("Failed to load background image: " + e.getMessage());
            backgroundImage = null;
        }
    }
     
    // custom jpanel with background image
    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                
                // add dark overlay for better text readability
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            } else {
                // fallback gradient background
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(20, 20, 40),
                    0, getHeight(), new Color(40, 20, 60)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
      // cleanup resources when window is closed
    @Override
    public void dispose() {
        audioService.dispose();
        super.dispose();
    }
}
