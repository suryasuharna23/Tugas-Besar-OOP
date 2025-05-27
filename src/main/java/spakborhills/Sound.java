package spakborhills;


import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
import java.util.ArrayList;

public class Sound {
    Clip clip;
    ArrayList<URL> soundURL = new ArrayList<>();

    public Sound(){
        soundURL.add(getClass().getResource("/sound/littleroottown.wav"));
        soundURL.add(getClass().getResource("/sound/coin.wav"));
        soundURL.add(getClass().getResource("/sound/powerup.wav"));
        soundURL.add(getClass().getResource("/sound/fanfare.wav"));
        soundURL.add(getClass().getResource("/sound/unlock.wav"));
    }

    public void setFile(int i){
        try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL.get(i));
            clip = AudioSystem.getClip();
            clip.open(ais);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void play(){
        clip.start();
    }

    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop(){
        clip.stop();
    }
}