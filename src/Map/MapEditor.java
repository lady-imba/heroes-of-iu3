package Map;

import Castle.Castle;
import Hero.Hero;
import Oasis.Oasis;
import Player.Player;

//FileNotFoundException - это проверяемое исключение, которое возникает, когда программа пытается открыть файл по указанному пути
import java.io.FileNotFoundException;
//класс используется для считывания данных
import java.util.Scanner;
import java.util.List;

public class MapEditor {
    private Map currentMap;
    private Map.TileType selectedTileType;
    private String currentCreator;
    private Scanner scanner;
    private String playerName;
    private Player dummyPlayer;

    public MapEditor(String playerName) {
        this.scanner = new Scanner(System.in);
        this.playerName = playerName;
        this.dummyPlayer = new Player(new Hero(0, 0,0, "dummy-hero"), "&", false, "Dummy");
    }

    // Публичные методы для тестирования
    public Map createNewMap(String name, String creator, int width, int height) {
        Map map = new Map(name, creator, width, height);
        this.currentMap = map;
        this.currentCreator = creator;
        return map;
    }
    
    public boolean editTile(Map map, int x, int y, Map.TileType type) {
        if (x < 0 || x >= map.getWidth() || y < 0 || y >= map.getHeight()) {
            System.out.println("Ошибка: Координаты тайла находятся за пределами карты!");
            return false;
        }
        map.setTile(x, y, type);
        return true;
    }
    
    public boolean placeCastle(Map map, Castle castle, int index) {
        if (index < 0 || index > 1 || castle == null) {
            System.out.println("Ошибка: Неверный индекс замка или замок не создан!");
            return false;
        }
        
        if (castle.getX() < 0 || castle.getX() >= map.getWidth() || 
            castle.getY() < 0 || castle.getY() >= map.getHeight()) {
            System.out.println("Ошибка: Координаты замка находятся за пределами карты!");
            return false;
        }
        
        map.setCastle(index, castle);
        return true;
    }
    
    public boolean addOasis(Map map, Oasis oasis) {
        if (oasis == null) {
            System.out.println("Ошибка: Оазис не создан!");
            return false;
        }
        
        if (oasis.getOasisX() < 0 || oasis.getOasisX() >= map.getWidth() || 
            oasis.getOasisY() < 0 || oasis.getOasisY() >= map.getHeight()) {
            System.out.println("Ошибка: Координаты оазиса находятся за пределами карты!");
            return false;
        }
        
        map.addOasis(oasis);
        return true;
    }
    
    public boolean removeOasis(Map map, int x, int y) {
        List<Oasis> oases = map.getOases();
        for (Oasis oasis : oases) {
            if (oasis.getOasisX() == x && oasis.getOasisY() == y) {
                map.removeOasis(oasis);
                return true;
            }
        }
        System.out.println("Ошибка: Оазис с указанными координатами не найден!");
        return false;
    }
    
    public boolean saveMap(Map map) throws FileNotFoundException {
        return MapStorage.saveMap(map);
    }
    
    public Map loadMap(String mapName, String creator) {
        return MapStorage.loadMap(mapName, creator);
    }
    
    public boolean deleteMap(String mapName, String creator) {
        return MapStorage.deleteMap(mapName, creator);
    }

    public void start() throws FileNotFoundException {
        while (true) {
            System.out.println("\n=== Редактор карты ===");
            System.out.println("1. Создать новую карту");
            System.out.println("2. Редактировать мои карты");
            System.out.println("3. Открыть список карт");
            System.out.println("4. Посмотреть карту в консоли");
            System.out.println("5. Удалить карту");
            System.out.println("6. Вернуться в главное меню");
            System.out.print("Выберите опцию: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createNewMap();
                    break;
                case 2:
                    editMyMaps();
                    break;
                case 3:
                    viewAllMaps();
                    break;
                case 4:
                    viewMapInConsole();
                    break;
                case 5:
                    deleteMyMap();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Неправильная опция. Пожалуйста, попробуйте снова.");
            }
        }
    }

    private void createNewMap() throws FileNotFoundException {
        System.out.print("Введите имя карты: ");
        String name = scanner.nextLine();
        
        System.out.print("Введите ширину карты: ");
        int width = scanner.nextInt();
        
        System.out.print("Введите высоту карты: ");
        int height = scanner.nextInt();
        
        Map map = new Map(name, playerName, width, height);
        editMapDetails(map);
        
        // Проверяем результат сохранения
        if (MapStorage.saveMap(map)) {
            System.out.println("Карта создана успешно!");
        } else {
            System.out.println("Не удалось создать карту. Убедитесь, что вы разместили ровно два замка!");
        }
    }

    private void editMapDetails(Map map) {
        while (true) {
            System.out.println("\n=== Редактор карты ===");
            System.out.println("1. Разместить/Удалить тайл");
            System.out.println("2. Разместить замок");
            System.out.println("3. Разместить оазис");
            System.out.println("4. Посмотреть текущую карту");
            System.out.println("5. Сохранить и выйти");
            System.out.print("Выберите опцию: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    editTile(map);
                    break;
                case 2:
                    placeCastle(map);
                    break;
                case 3:
                    placeOasis(map);
                    break;
                case 4:
                    displayCurrentMap(map);
                    break;
                case 5:
                    Castle[] castles = map.getCastles();
                    if (castles[0] == null || castles[1] == null) {
                        System.out.println("Предупреждение: Карта должна иметь ровно два замка!");
                        System.out.println("Текущие замки: " + 
                            (castles[0] != null ? "Замок 1 в (" + castles[0].getX() + "," + castles[0].getY() + ")" : "Замок 1 отсутствует") + ", " +
                            (castles[1] != null ? "Замок 2 в (" + castles[1].getX() + "," + castles[1].getY() + ")" : "Замок 2 отсутствует"));
                        System.out.print("Хотите продолжить без сохранения? (y/n): ");
                        String answer = scanner.nextLine().toLowerCase();
                        if (answer.equals("y")) {
                            return;
                        }
                    } else {
                        return;
                    }
                    break;
                default:
                    System.out.println("Неправильная опция. Пожалуйста, попробуйте снова.");
            }
        }
    }

    private void editTile(Map map) {
        System.out.print("Введите координату x: ");
        int x = scanner.nextInt();
        
        System.out.print("Введите координату y: ");
        int y = scanner.nextInt();
        
        System.out.println("Типы тайлов:");
        System.out.println("1. Пустой");
        System.out.println("2. Дорога");
        System.out.println("3. Препятствие");
        System.out.println("4. Вода");
        System.out.print("Выберите тип тайла: ");
        
        int typeChoice = scanner.nextInt();
        Map.TileType type;
        
        switch (typeChoice) {
            case 1:
                type = Map.TileType.EMPTY;
                break;
            case 2:
                type = Map.TileType.ROAD;
                break;
            case 3:
                type = Map.TileType.OBSTACLE;
                break;
            case 4:
                type = Map.TileType.WATER;
                break;
            default:
                System.out.println("Неверный тип тайла. Используется EMPTY.");
                type = Map.TileType.EMPTY;
        }
        
        map.setTile(x, y, type);
        System.out.println("Тайл обновлен успешно!");
    }

    private void placeCastle(Map map) {
        System.out.print("Введите координату x: ");
        int x = scanner.nextInt();
        
        System.out.print("Введите координату y: ");
        int y = scanner.nextInt();
        
        System.out.print("Введите индекс замка (0 или 1): ");
        int index = scanner.nextInt();
        
        if (index < 0 || index > 1) {
            System.out.println("Неверный индекс замка. Должен быть 0 или 1.");
            return;
        }
        
        Castle castle = new Castle(dummyPlayer, x, y);
        map.setCastle(index, castle);
        System.out.println("Замок размещен успешно!");
        
        // Показываем текущее состояние замков
        Castle[] castles = map.getCastles();
        System.out.println("Текущие замки: " + 
            (castles[0] != null ? "Замок 1 в (" + castles[0].getX() + "," + castles[0].getY() + ")" : "Замок 1 отсутствует") + ", " +
            (castles[1] != null ? "Замок 2 в (" + castles[1].getX() + "," + castles[1].getY() + ")" : "Замок 2 отсутствует"));
    }

    private void placeOasis(Map map) {
        System.out.print("Введите координату x: ");
        int x = scanner.nextInt();
        
        System.out.print("Введите координату y: ");
        int y = scanner.nextInt();
        
        Oasis oasis = new Oasis(x, y);
        map.addOasis(oasis);
        System.out.println("Оазис размещен успешно!");
    }

    private void editMyMaps() throws FileNotFoundException {
        List<String> myMapNames = MapStorage.getMapsByCreator(playerName);
        if (myMapNames.isEmpty()) {
            System.out.println("У вас нет карт для редактирования.");
            return;
        }

        System.out.println("\nВаши карты:");
        for (int i = 0; i < myMapNames.size(); i++) {
            System.out.println((i + 1) + ". " + myMapNames.get(i));
        }

        System.out.print("Выберите карту для редактирования: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= myMapNames.size()) {
            String mapName = myMapNames.get(choice - 1);
            Map map = MapStorage.loadMap(mapName, playerName);
            if (map != null) {
                editMapDetails(map);
                MapStorage.saveMap(map);
                System.out.println("Карта обновлена успешно!");
            }
        } else {
            System.out.println("Неверный выбор карты.");
        }
    }

    private void deleteMyMap() {
        List<String> myMapNames = MapStorage.getMapsByCreator(playerName);
        if (myMapNames.isEmpty()) {
            System.out.println("У вас нет карт для удаления.");
            return;
        }

        System.out.println("\nВаши карты:");
        for (int i = 0; i < myMapNames.size(); i++) {
            System.out.println((i + 1) + ". " + myMapNames.get(i));
        }

        System.out.print("Выберите карту для удаления: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= myMapNames.size()) {
            String mapName = myMapNames.get(choice - 1);
            MapStorage.deleteMap(mapName, playerName);
            System.out.println("Карта удалена успешно!");
        } else {
            System.out.println("Неверный выбор карты.");
        }
    }

    private void viewAllMaps() {
        List<String> mapNames = MapStorage.getAllMapNames();
        if (mapNames.isEmpty()) {
            System.out.println("Нет доступных карт.");
            return;
        }

        System.out.println("\nДоступные карты:");
        for (String mapInfo : mapNames) {
            String[] parts = mapInfo.split(":");
            if (parts.length == 2) {
                String creator = parts[0];
                String mapName = parts[1];
                System.out.println("Карта: " + mapName + " ( " + creator + ")");
            }
        }
    }

    private void displayCurrentMap(Map map) {
        // Выводим информацию о карте
        System.out.println("\nКарта: " + map.getName());
        System.out.println("Создатель: " + map.getCreator());
        System.out.println("Размер: " + map.getWidth() + "x" + map.getHeight());
        System.out.println();

        System.out.print("+");
        for (int x = 0; x < map.getWidth(); x++) {
            System.out.print("-");
        }
        System.out.println("+");
        
        // Выводим карту
        for (int y = 0; y < map.getHeight(); y++) {
            System.out.print("|");
            for (int x = 0; x < map.getWidth(); x++) {
                char symbol = getTileSymbol(map, x, y);
                System.out.print(symbol);
            }
            System.out.println("|");
        }
        
        // Выводим нижнюю границу
        System.out.print("+");
        for (int x = 0; x < map.getWidth(); x++) {
            System.out.print("-");
        }
        System.out.println("+");
        
        // Выводим легенду
        System.out.println("\nЛегенда:");
        System.out.println("  . - Пустой тайл");
        System.out.println("  # - Дорога");
        System.out.println("  X - Препятствие");
        System.out.println("  ~ - Вода");
        System.out.println("  C - Замок");
        System.out.println("  O - Оазис");
        
        // Выводим информацию о замках и оазисах
        System.out.println("\nЗамки:");
        Castle[] castles = map.getCastles();
        for (int i = 0; i < castles.length; i++) {
            if (castles[i] != null) {
                System.out.println("  " + i + ": Позиция (" + castles[i].getX() + ", " + castles[i].getY() + ")");
            }
        }
        
        System.out.println("\nОазисы:");
        List<Oasis> oases = map.getOases();
        for (int i = 0; i < oases.size(); i++) {
            Oasis oasis = oases.get(i);
            System.out.println("  " + i + ": Позиция (" + oasis.getOasisX() + ", " + oasis.getOasisY() + ")");
        }
    }

    private char getTileSymbol(Map map, int x, int y) {
        // Проверяем, есть ли объекты на этой позиции
        Castle[] castles = map.getCastles();
        for (Castle castle : castles) {
            if (castle != null && castle.getX() == x && castle.getY() == y) {
                return 'C';
            }
        }
        
        List<Oasis> oases = map.getOases();
        for (Oasis oasis : oases) {
            if (oasis != null && oasis.getOasisX() == x && oasis.getOasisY() == y) {
                return 'O';
            }
        }
        
        // Если объектов нет, возвращаем символ тайла
        Map.TileType tileType = map.getTileAt(x, y);
        switch (tileType) {
            case EMPTY:
                return '.';
            case ROAD:
                return '#';
            case OBSTACLE:
                return 'X';
            case WATER:
                return '~';
            default:
                return '?';
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число.");
            }
        }
    }

    public Map getCurrentMap() {
        return currentMap;
    }

    private void viewMapInConsole() {
        List<String> mapNames = MapStorage.getAllMapNames();
        if (mapNames.isEmpty()) {
            System.out.println("Нет доступных карт для просмотра.");
            return;
        }

        System.out.println("\nДоступные карты:");
        for (int i = 0; i < mapNames.size(); i++) {
            String[] parts = mapNames.get(i).split(":");
            if (parts.length == 2) {
                String creator = parts[0];
                String mapName = parts[1];
                System.out.println((i + 1) + ". " + mapName + " ( " + creator + ")");
            }
        }

        System.out.print("Выберите карту для просмотра: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= mapNames.size()) {
            String[] parts = mapNames.get(choice - 1).split(":");
            if (parts.length == 2) {
                String creator = parts[0];
                String mapName = parts[1];
                MapStorage.displayMapInConsole(mapName, creator);
            }
        } else {
            System.out.println("Неверный выбор карты.");
        }
    }
} 