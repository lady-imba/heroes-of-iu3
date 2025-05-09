package Building;

import java.io.Serializable;

public class Service implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String name;
    private final String description;
    private final int duration; // в минутах
    private final int effectValue;
    private final ServiceType type;

    public enum ServiceType {
        HEALTH_BOOST,   // Повышение здоровья
        MOVEMENT_BOOST, // Повышение перемещения
        INVASION_BONUS, // Сокращение времени захвата замка
        ATTACK_BOOST,   // Повышение атаки (для парикмахерской)
        NONE            // Отсутствие бонуса
    }

    public Service(String name, String description, int duration, int effectValue, ServiceType type) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.effectValue = effectValue;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getDuration() {
        return duration;
    }

    public int getEffectValue() {
        return effectValue;
    }

    public ServiceType getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + " (" + description + "), длительность: " + duration + " мин.";
    }
} 