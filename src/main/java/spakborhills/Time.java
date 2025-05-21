// Asumsi ini ada di kelas Time.java Anda
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
        System.out.println("Time reset to Day " + day + ", " + getFormattedTime());
    }

    public void advanceTime(int minutes) {
        this.newDay = false; // Reset flag setiap kali waktu maju
        this.minute += minutes;
        while (this.minute >= 60) {
            this.minute -= 60;
            this.hour++;
            if (this.hour >= 24) {
                this.hour = 0; // Tengah malam, hari baru akan dihandle setelah ini
                // Sebenarnya, jika jam 24:00 adalah akhir hari, hari baru dimulai saat jam 00:00
                // Logika isNewDay() akan menangani penambahan hari
                this.newDay = true; // Tandai bahwa hari baru telah tiba
            }
        }
    }

    public boolean isNewDay() {
        // Hari dianggap baru jika flag newDay diset oleh advanceTime()
        // atau jika logika tidur pemain memajukan hari.
        return newDay;
    }

    public void startNewDay() {
        // Dipanggil setelah isNewDay() true atau saat pemain tidur
        if (newDay) { // Hanya tambah hari jika memang sudah waktunya
            this.day++;
        }
        // Waktu default bangun tidur atau awal hari baru
        this.hour = DEFAULT_START_HOUR; // Jam 6 pagi
        this.minute = DEFAULT_START_MINUTE;
        this.newDay = false; // Reset flag setelah hari baru dimulai
        System.out.println("Started new day: Day " + this.day);
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