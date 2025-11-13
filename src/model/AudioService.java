/******************************************
 * Filename		: AudioService.java
 * Description	: model class for managing background music and sound effects.
 *                handles loading, playing, stopping, and looping audio files.
 * Author		: Mochamad Zidan Rusdhiana  
 * Date			: 2025-06-20
******************************************/
package model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioService {
    private Clip currentClip;
    private boolean isMuted = false;
    private float volume = 0.6f; // default volume
    
    // play background music with looping
    public void playBackgroundMusic(String audioFilePath) {
        try {
            // stop current music if playing
            stopMusic();
            
            // load audio file
            File audioFile = new File(audioFilePath);
            if (!audioFile.exists()) {
                System.err.println("Audio file not found: " + audioFilePath);
                return;
            }
            
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            currentClip = AudioSystem.getClip();
            currentClip.open(audioInputStream);
            
            // set volume
            setVolume(volume);
            
            // loop continuously
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);
            currentClip.start();
            
            System.out.println("Playing background music: " + audioFilePath);
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio file format: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading audio file: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable: " + e.getMessage());
        }
    }
    
    // play sound effect once (no looping)
    public void playSoundEffect(String audioFilePath) {
        try {
            File audioFile = new File(audioFilePath);
            if (!audioFile.exists()) {
                System.err.println("Audio file not found: " + audioFilePath);
                return;
            }
            
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            Clip soundClip = AudioSystem.getClip();
            soundClip.open(audioInputStream);
            
            // set volume for sound effect
            FloatControl gainControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
            float gain = 20f * (float) Math.log10(volume);
            gainControl.setValue(gain);
            
            soundClip.start();
            
            // close clip when finished to free memory
            soundClip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        soundClip.close();
                    }
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error playing sound effect: " + e.getMessage());
        }
    }
    
    // stop current background music
    public void stopMusic() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
            System.out.println("Background music stopped");
        }
    }
    
    // pause current music
    public void pauseMusic() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            System.out.println("Background music paused");
        }
    }
    
    // resume paused music
    public void resumeMusic() {
        if (currentClip != null && !currentClip.isRunning()) {
            currentClip.start();
            System.out.println("Background music resumed");
        }
    }
    
    // set volume (0.0 to 1.0)
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        
        if (currentClip != null && currentClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
            float gain = 20f * (float) Math.log10(this.volume);
            // clamp gain to valid range
            gain = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), gain));
            gainControl.setValue(gain);
        }
    }
    
    // get current volume
    public float getVolume() {
        return volume;
    }
    
    // mute/unmute
    public void setMuted(boolean muted) {
        this.isMuted = muted;
        if (muted) {
            setVolume(0.0f);
        } else {
            setVolume(volume);
        }
    }
    
    // check if muted
    public boolean isMuted() {
        return isMuted;
    }
    
    // check if music is playing
    public boolean isPlaying() {
        return currentClip != null && currentClip.isRunning();
    }
    
    // cleanup resources
    public void dispose() {
        stopMusic();
    }
}
