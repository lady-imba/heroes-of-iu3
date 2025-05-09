package Building;

import java.util.ArrayList;
import java.util.List;

public class TimeManager {
    private static final long REAL_MS_PER_GAME_MINUTE = 100; // 1 игровая минута = 100 мс реального времени
    private static TimeManager instance;
    private long gameMinutes = 0;
    private int gameHours = 8; // Начинаем в 8:00
    private int gameDays = 1;
    private volatile boolean running = false;
    private Thread timeThread;

    private final List<Building> buildings = new ArrayList<>();
    private final List<NPCManager> npcManagers = new ArrayList<>();
    
    private TimeManager() {
        // Приватный конструктор для синглтона
    }
    
    public static synchronized TimeManager getInstance() {
        if (instance == null) {
            instance = new TimeManager();
        }
        return instance;
    }

    public synchronized void reset() {
        // Останавливаем текущее выполнение
        stop();
        
        // Сбрасываем все переменные состояния
        gameMinutes = 0;
        gameHours = 8; // Начинаем в 8:00
        gameDays = 1;
        running = false;
        
        // Очищаем списки
        buildings.clear();
        npcManagers.clear();
    }
    

    public synchronized int getTotalDays() {
        return gameDays;
    }
    

    public synchronized int getTotalHours() {
        return gameHours;
    }
    

    public synchronized int getTotalMinutes() {
        return (int)gameMinutes;
    }
    
    public void start() {
        if (!running) {
            running = true;
            
            // Создаем и запускаем новый поток
            timeThread = new Thread(() -> {
                try {
                    while (running) {
                        updateTime();
                        Thread.sleep(REAL_MS_PER_GAME_MINUTE);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            
            timeThread.setDaemon(true); // Делаем поток демоном, чтобы он не блокировал завершение приложения
            timeThread.start();
            
            System.out.println("Игровое время запущено. Текущее время: " + getTimeString());
        }
    }
    
    public void stop() {
        if (running) {
            running = false;
            
            // Останавливаем поток, если он активен
            if (timeThread != null && timeThread.isAlive()) {
                timeThread.interrupt();
                try {
                    timeThread.join(1000); // Ждем завершения потока максимум 1 секунду
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                timeThread = null;
            }
            
            System.out.println("Игровое время остановлено. Текущее время: " + getTimeString());
        }
    }
    
    private synchronized void updateTime() {
        gameMinutes++;
        if (gameMinutes >= 60) {
            gameMinutes = 0;
            gameHours++;
            if (gameHours >= 24) {
                gameHours = 0;
                gameDays++;
            }
            // Уведомляем об изменении часа
            notifyHourPassed();
        }
        // Уведомляем об изменении минуты
        notifyMinutePassed();
    }
    
    public synchronized String getTimeString() {
        return String.format("День %d, %02d:%02d", gameDays, gameHours, gameMinutes);
    }
    
    public synchronized long getTotalGameMinutes() {
        return gameDays * 24 * 60 + gameHours * 60 + gameMinutes;
    }
    
    // Методы для добавления и удаления зданий
    public void addBuilding(Building building) {
        if (building != null && !buildings.contains(building)) {
            buildings.add(building);
        }
    }
    
    public void removeBuilding(Building building) {
        buildings.remove(building);
    }
    
    // Методы для добавления и удаления NPCManager
    public void addNPCManager(NPCManager npcManager) {
        if (npcManager != null && !npcManagers.contains(npcManager)) {
            npcManagers.add(npcManager);
        }
    }
    
    public void removeNPCManager(NPCManager npcManager) {
        npcManagers.remove(npcManager);
    }
    
    // Уведомление о прошедшей минуте
    private void notifyMinutePassed() {
        // Уведомляем здания
        for (Building building : new ArrayList<>(buildings)) {
            building.onMinutePassed();
        }
        
        // Уведомляем NPCManager
        for (NPCManager manager : new ArrayList<>(npcManagers)) {
            manager.onMinutePassed();
        }
    }
    
    // Уведомление о прошедшем часе
    private void notifyHourPassed() {
        // Уведомляем здания
        for (Building building : new ArrayList<>(buildings)) {
            building.onHourPassed();
        }
        
        // Уведомляем NPCManager
        for (NPCManager manager : new ArrayList<>(npcManagers)) {
            manager.onHourPassed();
        }
    }
    
    // Получаем прогноз времени через указанное количество минут
    public synchronized String getTimeAfterMinutes(long minutes) {
        long totalMinutes = getTotalGameMinutes() + minutes;
        int days = (int)(totalMinutes / (24 * 60));
        totalMinutes %= (24 * 60);
        int hours = (int)(totalMinutes / 60);
        totalMinutes %= 60;
        int mins = (int)totalMinutes;
        
        return String.format("День %d, %02d:%02d", days, hours, mins);
    }

    public synchronized void advanceTime(int minutes) {
        for (int i = 0; i < minutes; i++) {
            gameMinutes++;
            if (gameMinutes >= 60) {
                gameMinutes = 0;
                gameHours++;
                if (gameHours >= 24) {
                    gameHours = 0;
                    gameDays++;
                }
                // Уведомляем об изменении часа
                notifyHourPassed();
            }
            // Уведомляем об изменении минуты
            notifyMinutePassed();
        }
    }

    public List<NPCManager> getNpcManagers() {
        return new ArrayList<>(npcManagers);
    }
} 