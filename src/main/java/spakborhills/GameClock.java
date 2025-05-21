// File: Tugas-Besar-OOP/src/main/java/spakborhills/GameClock.java
package spakborhills;

import spakborhills.enums.Season;

public class GameClock extends Thread {
    private final Time time;
    private final Weather weather;
    private Season currentSeason = Season.SPRING;
    private volatile boolean running = true; // Pastikan 'running' adalah volatile
    private volatile boolean paused = false; // Flag untuk status pause, volatile
    private final Object pauseLock = new Object(); // Objek untuk sinkronisasi pause/resume

    public GameClock(Time time, Weather weather) {
        this.time = time;
        this.weather = weather;
    }

    private void updateSeason(int day) {
        int index = ((day - 1) / 10) % 4; // Asumsi 1 musim = 10 hari
        currentSeason = Season.values()[index];
    }

    @Override
    public void run() {
        while (running) {
            try {
                synchronized (pauseLock) {
                    if (paused) {
                        try {
                            // System.out.println("[GameClock] Time is paused. Waiting..."); // Debug
                            pauseLock.wait(); // Thread akan menunggu di sini sampai notify() dipanggil
                            // System.out.println("[GameClock] Time resumed."); // Debug
                        } catch (InterruptedException e) {
                            // System.err.println("GameClock pause wait interrupted"); // Debug
                            if (!running) { // Jika stopClock() dipanggil saat paused
                                break; // Keluar dari loop while(running)
                            }
                            // Jika diinterupsi karena alasan lain, setel ulang status interupsi
                            Thread.currentThread().interrupt();
                            // Anda mungkin ingin melanjutkan atau menghentikan tergantung kasusnya
                            // Untuk saat ini, kita lanjutkan saja jika 'running' masih true
                        }
                    }
                }

                // Jika tidak di-pause atau baru saja di-resume, lanjutkan logika waktu
                if (!running) break; // Cek lagi karena bisa diubah saat pause

                Thread.sleep(1000); // Kecepatan game: 1 detik nyata = 5 menit game

                // Logika utama pembaruan waktu
                // Tidak perlu lagi cek time.isPaused() di sini karena GameClock sendiri yang di-pause
                time.advanceTime(5);

                if (time.isNewDay()) {
                    time.startNewDay();
                    updateSeason(time.getDay());

                    if ((time.getDay() - 1) % 10 == 0) { // Setiap awal musim baru
                        weather.resetRainyCount();
                    }
                    weather.generateNewWeather();
                }

                // Output ini bisa dipindahkan ke UI atau di-toggle untuk debug
                // System.out.printf("[Time] %s | Day %d | Season: %s | Weather: %s%n",
                //         time.getFormattedTime(),
                //         time.getDay(),
                //         currentSeason.name(),
                //         weather.getWeatherName());

            } catch (InterruptedException e) {
                // Jika sleep utama diinterupsi
                if (running) { // Hanya log jika kita masih seharusnya berjalan
                    // System.err.println("GameClock run sleep interrupted: " + e.getMessage()); // Debug
                }
                // Thread.currentThread().interrupt(); // Setel ulang status interupsi jika perlu
            } catch (Exception e) {
                System.err.println("Error in GameClock run loop: " + e.getMessage());
                e.printStackTrace();
                running = false; // Hentikan clock jika ada error tak terduga
            }
        }
        // System.out.println("GameClock thread has stopped."); // Debug
    }

    public Time getTime() {
        return time;
    }

    /**
     * Menghentikan thread GameClock secara permanen.
     */
    public void stopClock() {
        running = false;
        // Bangunkan thread jika sedang di-pause atau sleep agar bisa keluar dari loop
        resumeTime(); // Ini akan memanggil notify dan membiarkan loop memeriksa 'running'
        this.interrupt(); // Interupsi thread utama jika sedang dalam Thread.sleep()
    }

    /**
     * Menjeda laju waktu game.
     * GameClock akan berhenti memajukan waktu sampai resumeTime() dipanggil.
     */
    public void pauseTime() {
        if (!paused) {
            paused = true;
            // System.out.println("[GameClock] Attempting to pause time..."); // Debug
        }
    }

    /**
     * Melanjutkan laju waktu game setelah di-pause.
     */
    public void resumeTime() {
        if (paused) {
            synchronized (pauseLock) {
                paused = false;
                pauseLock.notifyAll(); // Bangunkan thread yang sedang menunggu di pauseLock.wait()
            }
            // System.out.println("[GameClock] Attempting to resume time..."); // Debug
        }
    }

    /**
     * Mengecek apakah GameClock sedang di-pause.
     * @return true jika di-pause, false jika berjalan.
     */
    public boolean isPaused() {
        return paused;
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public String getFormattedTime() {
        if (time != null) {
            return time.getFormattedTime();
        }
        return "00:00"; // Default jika time belum terinisialisasi
    }

    public Weather getWeather() {
        return weather;
    }

    public void resetTime() {
        boolean wasPaused = this.paused;
        if (wasPaused) {
            resumeTime(); // Pastikan tidak dalam state wait saat mereset
        }

        if (this.time != null) {
            this.time.resetToDefault();
        }
        if (this.weather != null) {
            this.weather.resetToDefault();
        }
        if (this.time != null) {
            updateSeason(this.time.getDay());
        } else {
            currentSeason = Season.SPRING;
        }

        // Tidak perlu set ulang paused di sini karena objek Time tidak lagi mengelola pause
        // Jika GameClock perlu di-pause lagi setelah reset (misalnya game dimulai dalam keadaan pause),
        // itu harus dilakukan oleh pemanggil resetTime().
        // Untuk reset ke kondisi awal game, biasanya waktu berjalan, jadi tidak perlu pauseTime() di sini.

        System.out.println("GameClock system has been reset. New time: Day " + (this.time != null ? this.time.getDay() : "N/A") +
                ", Season: " + currentSeason.name() +
                ", Time: " + getFormattedTime() +
                ", Weather: " + (this.weather != null ? this.weather.getWeatherName() : "N/A"));
    }
}