package Building;

import java.io.Serializable;
import java.util.Random;
import Player.Player;

public class Visitor implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final boolean isPlayer;
    private final Player player; // Ссылка на объект Player (null для NPC)
    private transient ServiceTask currentActivity; // Текущее занятие посетителя (было CompletableFuture<Void>)
    private Building currentBuilding; // Здание, в котором сейчас находится посетитель
    private long serviceEndTime; // Время окончания текущей услуги
    private boolean bonusService; // Флаг для обозначения бонусной услуги
    

    public Visitor(String name, boolean isPlayer) {
        this.name = name;
        this.isPlayer = isPlayer;
        this.player = null;
        this.currentActivity = null;
        this.currentBuilding = null;
        this.serviceEndTime = 0;
        this.bonusService = false;
    }

    public Visitor(String name, Player player) {
        this.name = name;
        this.isPlayer = true;
        this.player = player;
        this.currentActivity = null;
        this.currentBuilding = null;
        this.serviceEndTime = 0;
        this.bonusService = false;
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Инициализируем transient поля
        this.currentActivity = null;
    }

    public String getName() {
        return name;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isBusy() {
        return currentActivity != null && !currentActivity.isDone();
    }

    public Building getCurrentBuilding() {
        return currentBuilding;
    }

    public void setCurrentBuilding(Building building) {
        this.currentBuilding = building;
    }

    public long getServiceEndTime() {
        return serviceEndTime;
    }

    public void setBonusService(boolean bonus) {
        this.bonusService = bonus;
    }

    public boolean hasBonusService() {
        return bonusService;
    }

    public void setCurrentActivity(ServiceTask activity, Building building, long serviceEndTime) {
        // Сохраняем предыдущую активность
        ServiceTask previousActivity = this.currentActivity;
        Building previousBuilding = this.currentBuilding;
        
        // Устанавливаем новую активность
        this.currentActivity = activity;
        this.currentBuilding = building;
        this.serviceEndTime = serviceEndTime;
        
        // Если есть предыдущая незавершенная активность, завершаем ее
        if (previousActivity != null && !previousActivity.isDone()) {
            previousActivity.complete();
        }
        
        // Когда услуга завершится, ПОМЕЧАЕМ ТОЛЬКО активность как завершенной, но НЕ сбрасываем здание
        if (activity != null) {
            activity.setOnCompleteCallback(() -> {
                // Только помечаем активность как завершенную
                // НЕ сбрасываем ссылку на здание, это сделает Building.completeService
                this.serviceEndTime = 0;
            });
        }
    }

    public void clearCurrentBuilding() {
        this.currentBuilding = null;
    }

    @Override
    public String toString() {
        return name + (isPlayer ? " (игрок)" : " (NPC)");
    }
} 