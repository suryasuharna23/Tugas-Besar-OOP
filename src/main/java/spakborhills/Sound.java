package spakborhills;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import java.net.URL;
import java.util.ArrayList;

public class Sound {
    Clip clip;
    ArrayList<URL> soundURL = new ArrayList<>();
    FloatControl volumeControl;
    private float currentVolume = 0.5f;

    public Sound() {
        soundURL.add(getClass().getResource("/sound/littleroottown.wav"));
        soundURL.add(getClass().getResource("/sound/coin.wav"));
        soundURL.add(getClass().getResource("/sound/powerup.wav"));
        soundURL.add(getClass().getResource("/sound/fanfare.wav"));
        soundURL.add(getClass().getResource("/sound/unlock.wav"));
        soundURL.add(getClass().getResource("/sound/shippingbinprocess.wav"));
        soundURL.add(getClass().getResource("/sound/eat.wav"));
        soundURL.add(getClass().getResource("/sound/tilling.wav"));

    }

    public void setFile(int i) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL.get(i));
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void play() {
        clip.start();
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        clip.stop();
    }

    public void setVolume(float volume) {

        volume = Math.max(0.0f, Math.min(1.0f, volume));
        this.currentVolume = volume;

        if (volumeControl != null) {

            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();

            if (volume == 0.0f) {

                volumeControl.setValue(min);
            } else {

                float gain = min + (max - min) * volume;
                volumeControl.setValue(gain);
            }

            System.out.println(
                    "[Sound] Volume set to: " + (volume * 100) + "% (Gain: " + volumeControl.getValue() + " dB)");
        }
    }

    public float getVolume() {
        return currentVolume;
    }

    public void increaseVolume(float amount) {
        setVolume(currentVolume + amount);
    }

    public void decreaseVolume(float amount) {
        setVolume(currentVolume - amount);
    }

    public void mute() {
        setVolume(0.0f);
    }

    public void unmute() {
        if (currentVolume == 0.0f) {
            setVolume(0.5f);
        }
    }
}