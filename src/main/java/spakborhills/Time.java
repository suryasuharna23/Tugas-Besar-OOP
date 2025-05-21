package spakborhills;

public class Time {
    private int hour;
    private int minute;
    private int day;

    private boolean isPaused = false;
    private boolean isNewDay = false;

    public Time() {
        this.hour = 6;
        this.minute = 0;
        this.day = 1;
    }

    // Memulai hari baru (dipanggil saat sudah hari berikutnya)
    public void startNewDay() {
        isNewDay = false;
    }

    // Getter status hari baru
    public boolean isNewDay() {
        if (isNewDay) {
            isNewDay = false;
            return true;
        }
        return false;
    }

    public void advanceTime(int amount) {
        if (isPaused) return;

        minute += amount;

        if (minute >= 60) {
            minute -= 60;
            hour++;

            if (hour >= 24) {
                hour = 6;
                day++;
                isNewDay = true;
            }
        }
    }

    public int getDay() {
        return day;
    }

    public String getFormattedTime() {
        return String.format("%02d:%02d", hour, minute);
    }

    public void pauseTime() {
        isPaused = true;
    }

    public void resumeTime() {
        isPaused = false;
    }
}