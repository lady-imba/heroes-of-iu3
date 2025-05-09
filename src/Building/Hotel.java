package Building;

import Unit.Unit;
import Player.Player;

import java.io.Serializable;

public class Hotel extends Building implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int ROOM_COUNT = 5; // Количество номеров
    

    public Hotel() {
        super("Отель «У погибшего альпиниста»", ROOM_COUNT);
        
        // Добавляем услуги
        services.add(new Service(
            "Короткий отдых",
            "Добавляет +2 к здоровью всех юнитов",
            60, // 1 час = 60 минут
            2,
            Service.ServiceType.HEALTH_BOOST
        ));
        
        services.add(new Service(
            "Длинный отдых",
            "Добавляет +3 к здоровью всех юнитов",
            1440, // 3 дня * 24 часа * 60 минут = 4320 минут, но для тестирования используем 1440
            3,
            Service.ServiceType.HEALTH_BOOST
        ));
    }

    @Override
    protected void applyServiceEffect(Visitor visitor, Service service) {
        if (visitor.isPlayer() && service.getType() == Service.ServiceType.HEALTH_BOOST) {
            System.out.println("Применяем эффект услуги: +" + service.getEffectValue() + 
                               " к здоровью всех юнитов для " + visitor.getName());
            
            // Получаем игрока напрямую
            Player player = visitor.getPlayer();
            
            if (player != null && player.getActiveHero() != null) {
                for (Unit unit : player.getActiveHero().getArmy()) {
                    int currentHealth = unit.getHealth();
                    unit.setHealth(currentHealth + service.getEffectValue());
                    System.out.println(" - " + unit.getType() + ": здоровье увеличено с " +
                                      currentHealth + " до " + unit.getHealth());
                }
            }
        }
    }
} 