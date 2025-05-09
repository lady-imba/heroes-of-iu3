package Building;

import Unit.Unit;
import Player.Player;
import Castle.Castle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Hairdresser extends Building implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int SPECIALISTS_COUNT = 2; // Количество мастеров
    

    public Hairdresser() {
        super("Парикмахерская «Отрезанное ухо»", SPECIALISTS_COUNT);
        
        // Добавляем услуги
        services.add(new Service(
            "Просто стрижка",
            "Без дополнительных бонусов",
            10, // 10 минут
            0,
            Service.ServiceType.NONE
        ));
        
        services.add(new Service(
            "Модная стрижка",
            "Сокращает время захвата замка с 2 до 1 хода",
            30, // 30 минут
            1,
            Service.ServiceType.INVASION_BONUS
        ));
    }

    @Override
    protected void applyServiceEffect(Visitor visitor, Service service) {
        if (visitor.isPlayer() && service.getType() == Service.ServiceType.INVASION_BONUS) {
            System.out.println("Применяем эффект услуги: сокращение времени захвата замка для " + visitor.getName());
            
            // Получаем игрока напрямую
            Player player = visitor.getPlayer();
            
            if (player != null) {
                // Устанавливаем флаг бонуса захвата замка для игрока
                player.setInvasionBonus(true);
                System.out.println(" - Теперь вам потребуется только 1 ход для захвата замка!");
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
        
        // Количество занятых специалистов
        int busySpecialists = Math.min(totalVisitors, SPECIALISTS_COUNT);
        System.out.println("Занято специалистов: " + busySpecialists + "/" + SPECIALISTS_COUNT);
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
            System.out.println("Парикмахерская пуста.");
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