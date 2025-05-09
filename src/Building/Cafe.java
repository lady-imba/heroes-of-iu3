package Building;

import Unit.Unit;
import Player.Player;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Cafe extends Building implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int WAITERS_COUNT = 3; // Количество официантов
    private static final int CLIENTS_PER_WAITER = 4; // Количество клиентов на одного официанта

    public Cafe() {
        super("Кафе «Сырники от тети Глаши»", WAITERS_COUNT * CLIENTS_PER_WAITER);
        //добавляем услуги
        services.add(new Service(
            "Просто перекус",
            "Добавляет +2 к перемещению всех юнитов",
            15, // 15 минут
            2,
            Service.ServiceType.MOVEMENT_BOOST
        ));
        
        services.add(new Service(
            "Плотный обед",
            "Добавляет +3 к перемещению всех юнитов",
            30, // 30 минут
            3,
            Service.ServiceType.MOVEMENT_BOOST
        ));
    }

    @Override
    protected void applyServiceEffect(Visitor visitor, Service service) {
        if (visitor.isPlayer() && service.getType() == Service.ServiceType.MOVEMENT_BOOST) {
            System.out.println("Применяем эффект услуги: +" + service.getEffectValue() + 
                               " к перемещению всех юнитов для " + visitor.getName());
            
            // Получаем игрока напрямую
            Player player = visitor.getPlayer();
            
            if (player != null && player.getActiveHero() != null) {
                for (Unit unit : player.getActiveHero().getArmy()) {
                    int currentMovement = unit.getMovement();
                    unit.setMovement(currentMovement + service.getEffectValue());
                    System.out.println(" - " + unit.getType() + ": перемещение увеличено с " +
                                      currentMovement + " до " + unit.getMovement());
                }
            }
        }
    }

    @Override
    public synchronized void printStatus() {
        System.out.println("=== " + getName() + " ===");
        
        // Получаем список всех посетителей
        List<Visitor> allVisitors = getAllVisitorsInBuilding();
        
        // Подсчитываем количество активных посетителей
        int activeCount = activeVisitors.size();
        
        // Подсчитываем "отдыхающих" NPC
        List<Visitor> loiteringNPCs = new ArrayList<>();
        for (Visitor visitor : allVisitors) {
            if (!visitor.isPlayer() && visitor.getCurrentBuilding() != null && 
                visitor.getCurrentBuilding().getName().equals(this.getName()) && 
                !activeVisitors.containsKey(visitor)) {
                loiteringNPCs.add(visitor);
            }
        }
        
        // Общее количество посетителей в здании
        int totalVisitors = activeCount + loiteringNPCs.size();
        
        // Количество занятых официантов
        int busyWaiters = Math.min((int) Math.ceil((double) totalVisitors / CLIENTS_PER_WAITER), WAITERS_COUNT);
        System.out.println("Занято официантов: " + busyWaiters + "/" + WAITERS_COUNT);
        System.out.println("Занято мест: " + Math.min(totalVisitors, getCapacity()) + "/" + getCapacity());
        
        if (totalVisitors > 0) {
            System.out.println("Активные посетители:");
            TimeManager timeManager = TimeManager.getInstance();
            
            // Отображаем посетителей, получающих услуги
            activeVisitors.forEach((visitor, future) -> {
                long endTime = visitor.getServiceEndTime();
                long currentTime = timeManager.getTotalGameMinutes();
                long remainingMinutes = endTime - currentTime;
                String endTimeStr = timeManager.getTimeAfterMinutes(remainingMinutes);
                
                System.out.println(" - " + visitor.getName() + 
                                   " (" + (visitor.isPlayer() ? "игрок" : "NPC") + "): освободится в " + 
                                   endTimeStr + " (через " + remainingMinutes + " мин.)");
            });
            
            // Затем добавляем NPC, которые привязаны к этому зданию, но не получают услугу
            // Показываем столько NPC, сколько их есть, но не больше чем вместимость здания - активные посетители
            int remainingToShow = getCapacity() - activeVisitors.size();
            int loiteringToShow = Math.min(loiteringNPCs.size(), remainingToShow);
            
            for (int i = 0; i < loiteringToShow; i++) {
                Visitor v = loiteringNPCs.get(i);
                System.out.println(" - " + v.getName() + " (NPC): отдыхает в " + getName());
            }
        } else {
            System.out.println("Кафе пусто.");
        }
        
        System.out.println("===================");
    }

    private List<Visitor> getAllVisitorsInBuilding() {
        // Собираем посетителей от NPCManager
        List<Visitor> allVisitors = new ArrayList<>();
        try {
            List<NPCManager> managers = TimeManager.getInstance().getNpcManagers();
            
            for (NPCManager manager : managers) {
                List<Visitor> npcs = manager.getNPCs();
                allVisitors.addAll(npcs);
            }
        } catch (Exception e) {
            // Игнорируем ошибки
        }
        return allVisitors;
    }
} 