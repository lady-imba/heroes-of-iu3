package Building;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class Building implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected final String name;
    protected final int capacity;
    protected Map<Visitor, ServiceTask> activeVisitors;
    protected final List<Service> services;
    protected int x;
    protected int y;
    protected volatile boolean isShutdown = false;
    protected volatile List<Thread> serviceThreads;

    public Building(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.activeVisitors = new HashMap<>();
        //Все методы списка становятся потокобезопасными
        this.services = Collections.synchronizedList(new ArrayList<>());
        this.x = 0;
        this.y = 0;
        this.serviceThreads = Collections.synchronizedList(new ArrayList<>());
        
        // Регистрируемся в TimeManager как здание
        TimeManager.getInstance().addBuilding(this);
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.activeVisitors = new HashMap<>(); //
        this.serviceThreads = Collections.synchronizedList(new ArrayList<>());
        this.isShutdown = false;
        
        // Повторно регистрируемся в TimeManager
        TimeManager.getInstance().addBuilding(this);
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String getType() {
        if (name.contains("Отель")) {
            return "HOTEL";
        } else if (name.contains("Кафе")) {
            return "CAFE";
        } else if (name.contains("Оазис")) {
            return "OASIS";
        } else if (name.contains("Парикмахерская")) {
            return "HAIRDRESSER";
        }
        return "BUILDING";
    }

    public int getServicesCount() {
        return services.size();
    }

    public Service getService(int index) {
        return services.get(index);
    }

    public synchronized void printStatus() {
        System.out.println("=== " + name + " ===");
        
        // Получаем список всех посетителей
        List<Visitor> allVisitors = getAllVisitorsInBuilding();
        
        // Получаем активных посетителей и посетителей, отдыхающих в здании
        Map<Visitor, ServiceTask> activeVisitorsMap = getActiveVisitorsMap();
        List<Visitor> loiteringNPCs = getLoiteringNPCs(allVisitors);
        
        // Подсчитываем количество игроков и NPC
        int[] counts = countVisitorTypes(activeVisitorsMap);
        int playerCount = counts[0];
        int npcCount = counts[1];
        
        // Общее количество посетителей в здании
        int totalVisitors = playerCount + npcCount + loiteringNPCs.size();
        
        // Выводим информацию о занятости здания
        printOccupancyInfo(totalVisitors);
        
        if (totalVisitors > 0) {
            // Отображаем информацию о посетителях
            printVisitorsInfo(activeVisitorsMap, loiteringNPCs);
        } else {
            System.out.println(" Здание пусто.");
        }
        
        System.out.println("===================");
    }

    //Конструкция synchronized(this.activeVisitors) означает блокировку доступа к объекту activeVisitors на время выполнения кода в фигурных скобках.
    private Map<Visitor, ServiceTask> getActiveVisitorsMap() {
        Map<Visitor, ServiceTask> visitors;
        synchronized(this.activeVisitors) {
            visitors = new HashMap<>(this.activeVisitors);
        }
        return visitors;
    }

    private int[] countVisitorTypes(Map<Visitor, ServiceTask> visitors) {
        int playerCount = 0;
        int npcCount = 0;
        
        for (Visitor visitor : visitors.keySet()) {
            if (visitor.isPlayer()) {
                playerCount++;
            } else {
                npcCount++;
            }
        }
        
        return new int[] {playerCount, npcCount};
    }

    private List<Visitor> getLoiteringNPCs(List<Visitor> allVisitors) {
        List<Visitor> loiteringNPCs = new ArrayList<>();
        for (Visitor visitor : allVisitors) {
            // Проверяем по имени здания, а не по ссылке
            if (!visitor.isPlayer() && visitor.getCurrentBuilding() != null && 
                visitor.getCurrentBuilding().getName().equals(this.name)) {
                synchronized(this.activeVisitors) {
                    if (!activeVisitors.containsKey(visitor)) {
                        loiteringNPCs.add(visitor);
                    }
                }
            }
        }
        return loiteringNPCs;
    }

    protected void printOccupancyInfo(int totalVisitors) {
        // Для кафе показываем число официантов
        if (getType().equals("CAFE")) {
            int maxStaff = 3;
            int occupiedStaff = 2;
            System.out.println("Занято официантов: " + occupiedStaff + "/" + maxStaff);
        }
        
        System.out.println("Занято мест: " + Math.min(totalVisitors, capacity) + "/" + capacity);
        System.out.println("Активные посетители:");
    }

    private void printVisitorsInfo(Map<Visitor, ServiceTask> visitors, List<Visitor> loiteringNPCs) {
        TimeManager timeManager = TimeManager.getInstance();
        
        // Отображаем посетителей, получающих услуги
        //visitors.entrySet() Возвращает набор всех пар "ключ-значение" из мапы
        for (Map.Entry<Visitor, ServiceTask> entry : visitors.entrySet()) {
            printActiveVisitorInfo(entry.getKey(), timeManager);
        }
        
        // Затем добавляем NPC, которые привязаны к этому зданию, но не получают услугу
        // Показываем столько NPC, сколько их есть, но не больше чем вместимость здания - активные посетители
        int remainingToShow = capacity - visitors.size();
        int loiteringToShow = Math.min(loiteringNPCs.size(), remainingToShow);
        
        for (int i = 0; i < loiteringToShow; i++) {
            Visitor v = loiteringNPCs.get(i);
            System.out.println(" - " + v.getName() + " (NPC): отдыхает в " + name);
        }
    }

    private void printActiveVisitorInfo(Visitor visitor, TimeManager timeManager) {
        long endTime = visitor.getServiceEndTime();
        long currentTime = timeManager.getTotalGameMinutes();
        long remainingMinutes = endTime - currentTime;
        String endTimeStr = timeManager.getTimeAfterMinutes(remainingMinutes);
        
        System.out.println(" - " + visitor.getName() + 
                           " (" + (visitor.isPlayer() ? "игрок" : "NPC") + "): освободится в " + 
                           endTimeStr + " (через " + remainingMinutes + " мин.)");
    }

    private List<Visitor> getAllVisitorsInBuilding() {
        // Собираем посетителей от NPCManager
        List<Visitor> allVisitors = new ArrayList<>();
        try {
            List<NPCManager> npcManagers = TimeManager.getInstance().getNpcManagers();
            
            for (NPCManager manager : npcManagers) {
                List<Visitor> npcs = manager.getNPCs();
                allVisitors.addAll(npcs);
            }
        } catch (Exception e) {
            // Игнорируем ошибки
        }
        return allVisitors;
    }

    public synchronized ServiceTask visit(Visitor visitor, int serviceIndex) {
        // Проверка входных данных и возможности посещения
        if (!validateVisitRequest(visitor, serviceIndex)) {
            return null;
        }
        
        // Получаем выбранную услугу и рассчитываем длительность
        Service service = services.get(serviceIndex);
        int duration = calculateServiceDuration(service, visitor);
        
        // Создаем задачу услуги и регистрируем посетителя
        ServiceTask serviceTask = registerVisitor(visitor, duration);
        if (serviceTask == null) {
            return null;
        }
        
        // Выводим сообщения для игрока
        if (visitor.isPlayer()) {
            printServiceStartMessage(visitor, service, duration);
        }
        
        // Запускаем поток для отслеживания времени услуги
        startServiceThread(visitor, service, serviceTask, duration);
        
        return serviceTask;
    }

    private boolean validateVisitRequest(Visitor visitor, int serviceIndex) {
        // Проверяем, что посетитель существует
        if (visitor == null) {
            return false;
        }
        
        // Получаем общее количество посетителей в здании
        int totalVisitors = countTotalVisitors();
        
        synchronized(activeVisitors) {
            // Проверяем, не получает ли посетитель уже услугу
            if (activeVisitors.containsKey(visitor)) {
                return false;
            }
            
            // Если посетитель не игрок (NPC)
            if (!visitor.isPlayer()) {
                // Проверяем, не превышена ли вместимость
                if (totalVisitors < capacity) {
                    // Присваиваем NPC текущее здание, но без активной услуги
                    visitor.setCurrentBuilding(this);
                } else {
                    // Здание уже заполнено
                    return false;
                }
            }
            
            // Проверяем, есть ли свободные места в здании для новой услуги
            if (totalVisitors >= capacity) {
                // Здание полностью заполнено
                return false;
            }
        }
        
        // Проверяем, что у нас есть услуги
        if (services == null || services.isEmpty()) {
            return false;
        }
        
        // Проверяем валидность индекса услуги
        return serviceIndex >= 0 && serviceIndex < services.size();
    }

    private int calculateServiceDuration(Service service, Visitor visitor) {
        int duration = service.getDuration();
        if (!visitor.isPlayer()) {
            // Для NPC уменьшаем длительность услуг для более динамичной смены посетителей
            duration = Math.min(duration, 15 + new Random().nextInt(16)); // 15-30 минут для NPC
        }
        return duration;
    }

    private ServiceTask registerVisitor(Visitor visitor, int duration) {
        // Вычисляем время окончания услуги
        TimeManager timeManager = TimeManager.getInstance();
        long currentTime = timeManager.getTotalGameMinutes();
        long endTime = currentTime + duration;
        
        // Создаем новый объект ServiceTask
        ServiceTask serviceTask = new ServiceTask(null);
        
        // Добавляем посетителя в список активных
        synchronized(activeVisitors) {
            activeVisitors.put(visitor, serviceTask);
        }
        
        // Проверяем, был ли действительно добавлен посетитель
        synchronized(activeVisitors) {
            if (!activeVisitors.containsKey(visitor)) {
                return null;
            }
        }
        
        // Обновляем информацию о занятости посетителя
        visitor.setCurrentActivity(serviceTask, this, endTime);
        
        return serviceTask;
    }

    private void printServiceStartMessage(Visitor visitor, Service service, int duration) {
        TimeManager timeManager = TimeManager.getInstance();
        System.out.println(visitor.getName() + " начал пользоваться услугой '" + 
                         service.getName() + "' в " + name + ".");
        System.out.println("Услуга будет предоставлена в " + timeManager.getTimeAfterMinutes(duration) + 
                         " (через " + duration + " мин.)");
    }

    private void startServiceThread(Visitor visitor, Service service, ServiceTask serviceTask, int duration) {
        TimeManager timeManager = TimeManager.getInstance();
        long endTime = timeManager.getTotalGameMinutes() + duration;
        
        // Создаем и запускаем поток для отслеживания времени
        Thread serviceThread = new Thread(() -> {
            try {
                while (timeManager.getTotalGameMinutes() < endTime && !isShutdown) {
                    Thread.sleep(100); // Проверяем состояние каждые 100 мс
                }
                if (!isShutdown) {
                    completeService(visitor, service, serviceTask);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        serviceThread.setDaemon(true);
        serviceThread.start();
        
        // Устанавливаем поток в ServiceTask
        serviceTask.setServiceThread(serviceThread);
        
        // Добавляем поток в список активных потоков здания
        synchronized (serviceThreads) {
            serviceThreads.add(serviceThread);
        }
    }

    protected synchronized void completeService(Visitor visitor, Service service, ServiceTask serviceTask) {
        // Проверяем, есть ли посетитель в списке активных
        synchronized(activeVisitors) {
            if (!activeVisitors.containsKey(visitor)) {
                return;
            }
        }
        
        ServiceTask task;
        synchronized(activeVisitors) {
            task = activeVisitors.remove(visitor);
        }
        
        if (task != null && !task.isDone()) {
            // Выводим сообщения только для игрока
            if (visitor.isPlayer()) {
                System.out.println(visitor.getName() + " закончил пользоваться услугой '" + 
                                 service.getName() + "' в " + name + ".");
                
                // Для игрока сразу очищаем ссылку на здание
                visitor.clearCurrentBuilding();
            } else {
                // Для NPC не очищаем ссылку на здание немедленно,
                // это позволит им "задержаться" в здании
                // Они уйдут, только когда NPCManager решит их переместить
            }
            
            // Применяем эффект услуги
            boolean hadBonus = visitor.hasBonusService();
            applyServiceEffect(visitor, service);
            
            // Сбрасываем флаг бонуса после применения эффекта
            if (hadBonus) {
                visitor.setBonusService(false);
            }
            
            // Завершаем задачу
            task.complete();
        }
    }

    protected abstract void applyServiceEffect(Visitor visitor, Service service);

    public void onMinutePassed() {
        // По умолчанию ничего не делаем, но подклассы могут переопределить
    }

    public void onHourPassed() {
        // По умолчанию ничего не делаем, но подклассы могут переопределить
    }

    public void shutdown() {
        isShutdown = true;
        
        // Прерываем все активные потоки услуг
        synchronized (serviceThreads) {
            for (Thread thread : serviceThreads) {
                if (thread.isAlive()) {
                    thread.interrupt();
                }
            }
            serviceThreads.clear();
        }
        
        // Завершаем все незавершенные Future
        synchronized(activeVisitors) {
            for (Map.Entry<Visitor, ServiceTask> entry : activeVisitors.entrySet()) {
                if (!entry.getValue().isDone()) {
                    entry.getValue().complete();
                }
            }
            activeVisitors.clear();
        }
        
        // Отмена регистрации в TimeManager
        TimeManager.getInstance().removeBuilding(this);
    }

    public int countTotalVisitors() {
        // Получаем всех посетителей
        List<Visitor> allVisitors = getAllVisitorsInBuilding();
        
        // Подсчитываем активных посетителей
        int activeCount;
        synchronized(activeVisitors) {
            activeCount = activeVisitors.size();
        }
        
        // Подсчитываем "отдыхающих" NPC (привязанных к этому зданию, но не получающих услугу)
        int loiteringCount = 0;
        for (Visitor visitor : allVisitors) {
            if (!visitor.isPlayer() && visitor.getCurrentBuilding() != null && 
                visitor.getCurrentBuilding().getName().equals(this.name)) {
                synchronized(activeVisitors) {
                    if (!activeVisitors.containsKey(visitor)) {
                        loiteringCount++;
                    }
                }
            }
        }
        
        return activeCount + loiteringCount;
    }
}