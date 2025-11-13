/******************************************
 * Filename		: Main.java
 * Description	: Main entry point for the game application.
 * Project      : Catch the balls game
 * Programmer	: Mochamad Zidan Rusdhiana
 * Date			: 2025-06-17
******************************************/

/*
Saya Mochamad Zidan Rusdhiana mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah 
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya 
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

/******************************************
 * Assets Source
 * - Planets, Galaxy, and Blackhole assets  : https://deep-fold.itch.io/pixel-planet-generator
 * - Paw assets                             : https://pin.it/5xQH1NE2D
 * - Background assets                      : https://wall.alphacoders.com/big.php?i=885542
 * - Player character assets                : https://judgemon21.itch.io/super-flying-cat-popo-48x48
 * - Audio assets                           : https://gooseninja.itch.io/space-music-pack   
 * - Pixeloid Font assets                   : https://ggbot.itch.io/pixeloid-font
******************************************/

import view.MainView;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set system look and feel
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    System.out.println("Could not set system look and feel: " + e.getMessage());
                }
                
                // Create and display main view
                new MainView();
            }
        });
    }
}