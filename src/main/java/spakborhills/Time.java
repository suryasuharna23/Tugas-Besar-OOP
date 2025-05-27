// File: spakborhills/Time.java
package spakborhills;

public class Time {
    private int day;
    private int hour;
    private int minute;
    private boolean newDay; // Flag untuk menandakan hari baru

    // Nilai awal default
    private static final int DEFAULT_START_DAY = 1;
    private static final int DEFAULT_START_HOUR = 6; // Jam 6 pagi
    private static final int DEFAULT_START_MINUTE = 0;

    public Time() {
        resetToDefault();
    }

    public void resetToDefault() {
        this.day = DEFAULT_START_DAY;
        this.hour = DEFAULT_START_HOUR;
        this.minute = DEFAULT_START_MINUTE;
        this.newDay = false; // Reset flag hari baru
        System.out.println("[Time] Time reset to Day " + day + ", " + getFormattedTime());
    }

    public void advanceTime(int minutes) {
        this.newDay = false; // Reset flag setiap kali waktu maju
        this.minute += minutes;
        while (this.minute >= 60) {
            this.minute -= 60;
            this.hour++;
            if (this.hour >= 24) {
                this.hour = 0;
                this.newDay = true; // Tandai bahwa hari baru telah tiba
            }
        }
    }

    public boolean isNewDay() {
        return newDay;
    }

    public void startNewDay() {
        // Dipanggil setelah isNewDay() true
        if (newDay) {
            this.day++;
            this.newDay = false;
            System.out.println("[Time] Naturally started new day: Day " + this.day + ", " + getFormattedTime());
        }
    }
    /**
     * Mengatur waktu game ke jam dan menit spesifik pada hari yang sedang berjalan.
     * Flag newDay akan direset menjadi false.
     * @param newHour Jam baru (0-23).
     * @param newMinute Menit baru (0-59).
     */
    public void setCurrentTime(int newHour, int newMinute) {
        if (newHour < 0 || newHour >= 24 || newMinute < 0 || newMinute >= 59) {
            System.err.println("[Time] Invalid time provided to setCurrentTime: " + newHour + ":" + newMinute);
            return;
        }
        this.hour = newHour;
        this.minute = newMinute;
        this.newDay = false; // Setelah waktu di-set manual, anggap bukan transisi hari alami via advanceTime()
        System.out.println("[Time] Clock explicitly set to: Day " + this.day + ", " + getFormattedTime());
    }


    /**
     * Memaksa sistem waktu untuk memulai hari baru, biasanya dipanggil saat pemain tidur.
     * Ini akan menaikkan hari dan mengatur ulang jam ke waktu pagi default.
     */
    public void forceStartNewDay() {
        this.day++; // Langsung increment hari
        this.hour = DEFAULT_START_HOUR; // Atur ke jam 6 pagi
        this.minute = DEFAULT_START_MINUTE; // Atur ke menit 0
        this.newDay = false; // Pastikan flag newDay direset
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