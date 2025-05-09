package Building;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NPCManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int NPC_COUNT = 15; // Количество NPC
    
    private final List<Visitor> npcs; // Потокобезопасный список
    private final List<Building> buildings;
    private final Random random;
    //Указывает, что поле должно быть исключено из процесса стандартной сериализации
    //При десериализации поле получит значение по умолчанию (null для объектов)
    private transient List<Thread> activeThreads;
    private Building hotel;
    private Building cafe;
    private Building hairdresser;
    private boolean initialized = false;
    private volatile boolean isShutdown = false;
    

    public NPCManager(List<Building> buildings) {
        this.npcs = Collections.synchronizedList(new ArrayList<>());
        this.buildings = buildings;
        this.random = new Random();
        initializeThreads();
        
        // Определяем отель, кафе и парикмахерскую
        for (Building building : buildings) {
            if (building.getName().contains("Отель")) {
                hotel = building;
            } else if (building.getName().contains("Кафе")) {
                cafe = building;
            } else if (building.getName().contains("Парикмахерская")) {
                hairdresser = building;
            }
        }
        
        if (hotel == null || cafe == null) {
            System.err.println("ВНИМАНИЕ: Не удалось найти отель или кафе! NPC не будут корректно размещены.");
            return;
        }
        
        // Создаем NPC
        for (int i = 1; i <= NPC_COUNT; i++) {
            Visitor npc = new Visitor("NPC-" + i, false);
            npcs.add(npc);
        }
        
        System.out.println("Создано " + npcs.size() + " NPC");
        
        // Регистрируемся в TimeManager
        TimeManager.getInstance().addNPCManager(this);
        
        // Обработчик завершения
        //Хук завершения (shutdown hook) — это механизм в Java, который позволяет выполнить определенный код перед завершением работы JVM
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        
        // Запускаем отдельный поток для размещения NPC
        startThread(this::initializeNPCs);
    }

    private void initializeThreads() {
        this.activeThreads = new ArrayList<>();
    }

    private void startThread(Runnable task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true); // Фоновый поток
        thread.start();
        synchronized (activeThreads) {
            activeThreads.add(thread);
        }
    }

    private void initializeNPCs() {
        if (initialized || hotel == null || cafe == null) {
            return;
        }
        
        try {
            // Небольшая задержка для инициализации игры
            Thread.sleep(1000);
            
            // Принудительное размещение всех NPC
            placeAllNPCsDirectly();
            
            initialized = true;
        } catch (Exception e) {
            System.err.println("Ошибка при инициализации NPC: " + e.getMessage());
        }
    }

    private void placeAllNPCsDirectly() {
        if (isShutdown) return;
        
        int hotelCount = 0;
        int cafeCount = 0;
        int hairdresserCount = 0;
        
        // Получаем вместимость зданий
        int hotelCapacity = hotel.getCapacity();
        int cafeCapacity = cafe.getCapacity();
        int hairdresserCapacity = hairdresser != null ? hairdresser.getCapacity() : 0;
        
        // Распределим NPC между отелем, кафе и парикмахерской с учетом вместимости
        synchronized(npcs) {
            for (int i = 0; i < npcs.size(); i++) {
                if (isShutdown) return;
                
                Visitor npc = npcs.get(i);
                
                // Определяем, в какое здание поместить NPC с учетом вместимости
                Building destination;
                
                // Распределение с приоритетами в зависимости от заполненности и случайного фактора
                //генерирует случайное целое число в диапазоне от 0 до 3
                int choice = random.nextInt(3);
                
                if (choice == 0 && hotelCount < hotelCapacity) {
                    destination = hotel;
                    hotelCount++;
                } else if (choice == 1 && cafeCount < cafeCapacity) {
                    destination = cafe;
                    cafeCount++;
                } else if (hairdresser != null && hairdresserCount < hairdresserCapacity) {
                    destination = hairdresser;
                    hairdresserCount++;
                } else if (hotelCount < hotelCapacity) {
                    destination = hotel;
                    hotelCount++;
                } else if (cafeCount < cafeCapacity) {
                    destination = cafe;
                    cafeCount++;
                } else if (hairdresser != null && hairdresserCount < hairdresserCapacity) {
                    destination = hairdresser;
                    hairdresserCount++;
                } else {
                    // Все здания заполнены, пропускаем этого NPC
                    continue;
                }
                
                try {
                    // Сначала явно устанавливаем привязку NPC к зданию
                    npc.setCurrentBuilding(destination);
                    
                    // Затем пытаемся запустить услугу
                    int serviceIndex = random.nextInt(destination.getServicesCount());
                    destination.visit(npc, serviceIndex);
                } catch (Exception e) {
                    // Игнорируем ошибки
                }
            }
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Инициализируем transient поля
        initializeThreads();
        this.isShutdown = false;
        
        // Регистрируемся в TimeManager
        TimeManager.getInstance().addNPCManager(this);
        
        // Определяем отель, кафе и парикмахерскую
        for (Building building : buildings) {
            if (building.getName().contains("Отель")) {
                hotel = building;
            } else if (building.getName().contains("Кафе")) {
                cafe = building;
            } else if (building.getName().contains("Парикмахерская")) {
                hairdresser = building;
            }
        }
        
        // Запускаем процесс инициализации NPC
        initialized = false;
        startThread(this::initializeNPCs);
    }

    public void onMinutePassed() {
        if ((hotel == null && cafe == null) || !initialized || isShutdown) {
            return;
        }
        
        // Если все NPC пропали из зданий, заново инициализируем их
        int npcInBuildings = 0;
        synchronized(npcs) {
            for (Visitor npc : npcs) {
                if (npc.getCurrentBuilding() != null) {
                    npcInBuildings++;
                }
            }
        }
        
        if (npcInBuildings == 0 && npcs.size() > 0) {
            placeAllNPCsDirectly();
            return;
        }
        
        // Проверяем свободных NPC и отправляем их в здания,
        // но делаем это с задержкой и вероятностью, чтобы не все сразу уходили
        synchronized(npcs) {
            for (Visitor npc : npcs) {
                // Проверяем, свободен ли NPC и прошло ли достаточно времени с окончания последней услуги
                if (!npc.isBusy() && random.nextInt(10) == 0) { // 10% шанс перехода в минуту
                    // NPC свободен, создаем задачу для отправки его в здание
                    final Visitor currentNpc = npc;
                    startThread(() -> {
                        try {
                            if (isShutdown) return;
                            
                            // Определяем, в какое здание отправить NPC
                            Building lastBuilding = currentNpc.getCurrentBuilding();
                            Building nextBuilding;
                            
                            // Если NPC уже в здании, вероятнее всего оставляем его там же
                            if (lastBuilding != null && random.nextInt(5) != 0) { // 4/5 вероятность остаться
                                nextBuilding = lastBuilding;
                            } 
                            // Иначе решаем, куда отправить NPC
                            else {
                                // Создаем список доступных зданий
                                List<Building> availableBuildings = new ArrayList<>();
                                if (hotel != null) availableBuildings.add(hotel);
                                if (cafe != null) availableBuildings.add(cafe);
                                if (hairdresser != null) availableBuildings.add(hairdresser);
                                
                                // Исключаем текущее здание, если есть другие варианты
                                if (lastBuilding != null && availableBuildings.size() > 1) {
                                    availableBuildings.remove(lastBuilding);
                                }
                                
                                if (availableBuildings.isEmpty()) return;
                                
                                // Выбираем случайное здание из доступных
                                nextBuilding = availableBuildings.get(random.nextInt(availableBuildings.size()));
                            }
                            
                            // Выбираем случайную услугу и посещаем здание
                            int serviceIndex = random.nextInt(nextBuilding.getServicesCount());
                            
                            nextBuilding.visit(currentNpc, serviceIndex);
                        } catch (Exception e) {
                            // Игнорируем ошибки
                        }
                    });
                }
            }
        }
    }

    public void onHourPassed() {
        // Ничего не делаем
    }

    public List<Visitor> getNPCs() {
        return new ArrayList<>(npcs);
    }

    public void shutdown() {
        isShutdown = true;
        
        synchronized (activeThreads) {
            for (Thread thread : activeThreads) {
                try {
                    if (thread.isAlive()) {
                        thread.interrupt();//вежливо просим поток завершиться
                        thread.join(500); // Ждем завершения максимум 500 мс
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            activeThreads.clear();
        }
        
        // Отменяем регистрацию в TimeManager
        TimeManager timeManager = TimeManager.getInstance();
        if (timeManager != null) {
            timeManager.removeNPCManager(this);
        }
    }
} 