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
        // Inisialisasi musim saat GameClock dibuat
        if (this.time != null) {
            updateSeasonBasedOnDay(this.time.getDay());
        }
    }

    // Mengubah nama agar lebih jelas dan menjadikannya publik
    /**
     * Memperbarui musim berdasarkan hari saat ini.
     * Dapat dipanggil secara eksternal jika hari dimajukan di luar siklus normal run().
     * @param currentDay hari saat ini.
     */
    public void updateSeasonBasedOnDay(int currentDay) {
        int index = ((currentDay - 1) / 10) % 4; // Asumsi 1 musim = 10 hari
        if (index < Season.values().length) {
            currentSeason = Season.values()[index];
        } else {
            // Fallback jika perhitungan indeks salah, default ke SPRING
            currentSeason = Season.SPRING;
            System.err.println("[GameClock] Warning: Season index out of bounds. Defaulting to SPRING.");
        }
        // System.out.println("[GameClock] Season updated to: " + currentSeason.name() + " for Day " + currentDay); // Debug
    }

    @Override
    public void run() {
        while (running) {
            try {
                synchronized (pauseLock) {
                    if (paused) {
                        try {
                            pauseLock.wait();
                        } catch (InterruptedException e) {
                            if (!running) break;
                            Thread.currentThread().interrupt();
                        }
                    }
                }

                if (!running) break;

                Thread.sleep(1000); // Kecepatan game (sesuaikan jika perlu)

                time.advanceTime(5); // Majukan waktu 5 menit game

                if (time.isNewDay()) {
                    time.startNewDay(); // Proses hari baru secara alami
                    updateSeasonBasedOnDay(time.getDay()); // Perbarui musim

                    if ((time.getDay() - 1) % 10 == 0) {
                        weather.resetRainyCount();
                    }
                    weather.generateNewWeather(); // Hasilkan cuaca baru
                }
            } catch (InterruptedException e) {
                if (running) {
                    // Handle interupsi jika perlu
                }
            } catch (Exception e) {
                System.err.println("Error in GameClock run loop: " + e.getMessage());
                e.printStackTrace();
                running = false;
            }
        }
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
            resumeTime();
        }

        if (this.time != null) {
            this.time.resetToDefault();
            updateSeasonBasedOnDay(this.time.getDay()); // Panggil setelah time direset
        } else {
            currentSeason = Season.SPRING; // Default jika time null
        }

        if (this.weather != null) {
            this.weather.resetToDefault();
        }

        System.out.println("GameClock system has been reset. New time: Day " + (this.time != null ? this.time.getDay() : "N/A") +
                ", Season: " + currentSeason.name() +
                ", Time: " + getFormattedTime() +
                ", Weather: " + (this.weather != null ? this.weather.getWeatherName() : "N/A"));
    }
}