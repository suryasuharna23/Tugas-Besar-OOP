
package spakborhills;

public class Time {
    private int day;
    private int hour;
    private int minute;
    private boolean newDay;

    
    private static final int DEFAULT_START_DAY = 1;
    private static final int DEFAULT_START_HOUR = 6;
    private static final int DEFAULT_START_MINUTE = 0;

    public Time() {
        resetToDefault();
    }

    public void resetToDefault() {
        this.day = DEFAULT_START_DAY;
        this.hour = DEFAULT_START_HOUR;
        this.minute = DEFAULT_START_MINUTE;
        this.newDay = false;
        System.out.println("[Time] Time reset to Day " + day + ", " + getFormattedTime());
    }

    public void advanceTime(int minutes) {
        this.newDay = false;
        this.minute += minutes;

        while (this.minute < 0) {
            this.minute += 60;
            this.hour--;
            if (this.hour < 0) {
                this.hour = 23;
                
            }
        }
        while (this.minute >= 60) {
            this.minute -= 60;
            this.hour++;
            if (this.hour >= 24) {
                this.hour = 0;
                this.newDay = true;
            }
        }
    }


    public boolean isNewDay() {
        return newDay;
    }

    public void startNewDay() {
        if (newDay) {
            this.day++;
            this.newDay = false;
            System.out.println("[Time] Naturally started new day: Day " + this.day + ", " + getFormattedTime());
        }
    }
    public void setCurrentTime(int newHour, int newMinute) {
        if (newHour < 0 || newHour >= 24 || newMinute < 0 || newMinute >= 59) {
            System.err.println("[Time] Invalid time provided to setCurrentTime: " + newHour + ":" + newMinute);
            return;
        }
        this.hour = newHour;
        this.minute = newMinute;
        this.newDay = false; 
        System.out.println("[Time] Clock explicitly set to: Day " + this.day + ", " + getFormattedTime());
    }

    public void forceStartNewDay() {
        this.day++; 
        this.hour = DEFAULT_START_HOUR; 
        this.minute = DEFAULT_START_MINUTE; 
        this.newDay = false; 
        System.out.println("[Time] Forced to start new day: Day " + this.day + ", " + getFormattedTime());
    }

    public String getFormattedTime() {
        return String.format("%02d:%02d", hour, minute);
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}