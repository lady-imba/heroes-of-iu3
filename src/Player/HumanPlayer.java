package Player;

import UI.GameMap;
import Hero.Hero;
import Building.Building;
import Building.Visitor;
import Building.TimeManager;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import Building.ServiceTask;

public class HumanPlayer  extends Player {
    private GameMap currentMap; // Текущая карта, на которой находится игрок

    public HumanPlayer(Hero initialHero, String terrainSymbol, boolean isAnotherPlayer, String name) {
        super(initialHero, terrainSymbol, isAnotherPlayer, name);
        this.currentMap = null;
    }
    //super — это вызов конструктора родительского класса (Player).

    public void moveHero(Hero hero, ArrayList<Hero> anotherHeroes, Player computer, GameMap map) {
        // Устанавливаем текущую карту
        this.currentMap = map;
        
        map.getCursor().setCursorActive(true);
        while (map.getCursor().isCursorActive()) {
            map.getCursor().selectCursorDirection();
            map.UpdateMapView(this, computer);
            map.Display(hero, anotherHeroes, this, computer);
            int pathCost = map.calculatePathCost(hero.getX(), hero.getY(), map.getCursor().getCursorX(), map.getCursor().getCursorY(), this);
            hero.displayRestMovementRange();
            if (pathCost <= hero.getEnergy()) {
                System.out.println("Перемещение возможно. Стоимость: " + pathCost);
            } else {
                System.out.println("Перемещение невозможно! Выберите другую позицию.");
            }
        }

        int pathCost = map.calculatePathCost(hero.getX(), hero.getY(), map.getCursor().getCursorX(), map.getCursor().getCursorY(), this);
        if (pathCost <= hero.getEnergy()) {
            hero.Move(map.getCursor().getCursorX(), map.getCursor().getCursorY(), pathCost);
            System.out.println("Герой перемещен на новую позицию.");

            if (map.getOasis().canUseOasis(hero)) {
                map.getOasis().applyOasisEffect(hero);
                List<Building> buildings = map.getBuildings(); // Changed from ArrayList to List
                for (Building b: buildings) {
                    if (b.getX() == hero.getX() && b.getY() == hero.getY() && b.getType().equals("OASIS")) {
                        // Восстановление энергии в оазисе
                        int oasisEnergyRestore = 30;
                        restoreHeroEnergy(oasisEnergyRestore);
                    }
                }
            }

            // Проверяем, есть ли в данной позиции здание
            Building building = map.getBuildingAt(hero.getX(), hero.getY());
            if (building != null) {
                System.out.println("Вы находитесь в здании: " + building.getName());
                visitBuilding(building);
            }

            for (Hero otherHero : anotherHeroes) {
                if (hero.getX() == otherHero.getX() && hero.getY() == otherHero.getY()) {
                    System.out.println("Герой игрока и герой компьютера встретились!");
                    map.startBattle(this, computer);
                }
            }
        } else {
            System.out.println("Перемещение невозможно! Герой остается на месте.");
        }

        map.UpdateMapView(this, computer);
        map.Display(hero, anotherHeroes, this, computer);
    }

    public GameMap getMap() {
        return currentMap;
    }

    public void setMap(GameMap map) {
        this.currentMap = map;
    }

    private void visitBuilding(Building building) {
        System.out.println("\n=== " + building.getName() + " ===");
        
        // Выводим информацию о текущей занятости здания
        System.out.println("\n=== СТАТУС ЗДАНИЯ ===");
        building.printStatus();
        System.out.println("===================");
        
        // Получаем доступные услуги
        int servicesCount = building.getServicesCount();
        if (servicesCount == 0) {
            System.out.println("В здании нет доступных услуг.");
            return;
        }
        
        // Проверяем, есть ли свободные места в здании
        boolean waited = false;
        
        if (building.countTotalVisitors() >= building.getCapacity()) {
            System.out.println("В данный момент все места в здании заняты.");
            System.out.println("Хотите подождать, пока освободится место? (1-Да, 2-Нет)");
            
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim();
            int waitChoice;
            
            try {
                waitChoice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Неверный ввод. Вы покинули здание.");
                return;
            }
            
            if (waitChoice != 1) {
                System.out.println("Вы решили не ждать и покинули здание.");
                return;
            }
            
            System.out.println("Вы решили подождать. Как только освободится место, вы получите услугу с бонусом.");
            
            // Активно ждем, пока не освободится место
            while (building.countTotalVisitors() >= building.getCapacity()) {
                try {
                    Thread.sleep(500); // Проверяем каждые 500 мс
                    waited = true;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            
            if (waited) {
                System.out.println("В здании освободилось место!");
                System.out.println("\n=== ОБНОВЛЕННЫЙ СТАТУС ===");
                building.printStatus();
                System.out.println("===================");
            }
        }
        
        System.out.println("\nДоступные услуги:");
        for (int i = 0; i < servicesCount; i++) {
            System.out.println((i + 1) + ". " + building.getService(i).getName() + 
                               " - " + building.getService(i).getDescription() + 
                               " (Время: " + building.getService(i).getDuration() + " мин.)");
        }
        
        // Создаем посетителя
        Visitor visitor = new Visitor(getName(), this);
        if (waited) {
            visitor.setBonusService(true); // Устанавливаем бонус, если игрок ждал
        }
        
        // Просим выбрать услугу
        System.out.println("\nВыберите услугу (0 для выхода):");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        int choice;
        
        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Неверный ввод. Вы покинули здание без использования услуг.");
            return;
        }
        
        if (choice > 0 && choice <= servicesCount) {
            // Проверяем еще раз, не заполнилось ли здание, пока выбирали услугу
            if (!waited && building.countTotalVisitors() >= building.getCapacity()) {
                System.out.println("Пока вы выбирали услугу, все места в здании были заняты.");
                System.out.println("Хотите подождать, пока освободится место? (1-Да, 2-Нет)");
                
                input = scanner.nextLine().trim();
                try {
                    int waitChoice = Integer.parseInt(input);
                    if (waitChoice != 1) {
                        System.out.println("Вы решили не ждать и покинули здание.");
                        return;
                    }
                    
                    System.out.println("Вы решили подождать. Как только освободится место, вы получите услугу с бонусом.");
                    
                    // Активно ждем, пока не освободится место
                    while (building.countTotalVisitors() >= building.getCapacity()) {
                        try {
                            Thread.sleep(500); // Проверяем каждые 500 мс
                            visitor.setBonusService(true); // Устанавливаем бонус
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    
                    System.out.println("В здании освободилось место!");
                } catch (NumberFormatException e) {
                    System.out.println("Неверный ввод. Вы покинули здание.");
                    return;
                }
            }
            
            // Используем услугу и получаем ServiceTask
            ServiceTask serviceTask = building.visit(visitor, choice - 1);
            
            // Если serviceTask не null и не выполнен, значит услуга была успешно заказана
            if (serviceTask != null && !serviceTask.isDone()) {
                TimeManager timeManager = TimeManager.getInstance();
                long currentTime = timeManager.getTotalGameMinutes();
                long endTime = visitor.getServiceEndTime();
                long waitTime = endTime - currentTime;
                
                // Эмуляция ожидания (ускоряем время)
                System.out.println("Ожидание завершения услуги...");
                System.out.println("Этот процесс займет " + waitTime + " минут игрового времени.");
                System.out.println("Пожалуйста, подождите...");
                
                // Показываем прогресс только в начале и в конце
                for (int i = 0; i < waitTime; i++) {
                    timeManager.advanceTime(1); // Продвигаем время на 1 минуту
                    
                    // Показываем прогресс только в виде точек каждые 100 минут
                    if (i % 100 == 0 && i > 0) {
                        System.out.print(".");
                    }
                }
                
                // Переход на новую строку после всех точек
                if (waitTime > 100) {
                    System.out.println();
                }
                
                System.out.println("Услуга успешно завершена за " + waitTime + " минут игрового времени!");
                
                // Дожидаемся завершения услуги
                while (!serviceTask.isDone()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("Вы покинули " + building.getName() + ".");
            } else {
                System.out.println("Не удалось получить услугу. Возможно, все места в здании уже заняты.");
            }
        } else {
            System.out.println("Вы покинули здание без использования услуг.");
        }
    }
}

