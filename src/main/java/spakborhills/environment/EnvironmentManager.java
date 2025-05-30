package spakborhills.environment;

import java.awt.Graphics2D;

import spakborhills.GamePanel;

public class EnvironmentManager {
    GamePanel gp;
    Lighting lighting;
    private final float MAX_ALPHA_NIGHT = 0.90f;
    private final float MIN_ALPHA_DAY = 0.0f;

    private float currentAlpha = MIN_ALPHA_DAY;

    public enum DayState {
        DAY, DUSK, NIGHT
    }

    public DayState currentState = DayState.DAY;

    public EnvironmentManager(GamePanel gp) {
        this.gp = gp;
    }

    public void setup() {
        lighting = new Lighting(gp, 350);
        update();
    }

    public void update() {
        if (gp.gameClock == null || gp.gameClock.getTime() == null) {
            currentAlpha = MIN_ALPHA_DAY;
            currentState = DayState.DAY;
            if (lighting != null) {
                lighting.update(currentAlpha);
            }
            return;
        }

        int currentHour = gp.gameClock.getTime().getHour();
        int currentMinute = gp.gameClock.getTime().getMinute();

        final int siang_start_hour = 6;
        final int senja_start_hour = 17;
        final int malam_start_hour = 18;

        final float senja_transition_duration_minutes = (malam_start_hour - senja_start_hour) * 60.0f;

        float transitionFraction;

        if (currentHour >= siang_start_hour && currentHour < senja_start_hour) {
            currentState = DayState.DAY;
            currentAlpha = MIN_ALPHA_DAY;
        } else if (currentHour >= senja_start_hour && currentHour < malam_start_hour) {
            currentState = DayState.DUSK;
            float minutesIntoDusk = (currentHour - senja_start_hour) * 60 + currentMinute;
            transitionFraction = Math.min(1.0f, minutesIntoDusk / senja_transition_duration_minutes);
            currentAlpha = MIN_ALPHA_DAY + (MAX_ALPHA_NIGHT - MIN_ALPHA_DAY) * transitionFraction;
        } else if (currentHour >= malam_start_hour) {
            currentState = DayState.NIGHT;
            currentAlpha = MAX_ALPHA_NIGHT;
        }
        currentAlpha = Math.max(MIN_ALPHA_DAY, Math.min(MAX_ALPHA_NIGHT, currentAlpha));

        if (lighting != null) {
            lighting.update(currentAlpha);
        }
    }

    public void draw(Graphics2D g2) {
        if (lighting != null) {
            lighting.draw(g2);
        }
    }

    public DayState getCurrentState() {
        return currentState;
    }

    public float getCurrentAlpha() {
        return currentAlpha;
    }
}
