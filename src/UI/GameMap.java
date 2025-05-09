package UI;

import Battle.Battle;
import Castle.Castle;
import Hero.Hero;
import Oasis.Oasis;
import Player.Player;
import Map.Map;
import Map.MapStorage;
import Building.Building;
import Building.Cafe;
import Building.Hotel;
import Building.Hairdresser;

import java.io.Serializable;
import java.util.*;

public class GameMap implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int height;
    private int width;
    private Cell[][] map;
    private Cursor cursor;
    private Oasis oasis;
    private Castle[] castles;
    private Map customMap;
    private List<Building> buildings;
    private static final String BUILDING_SYMBOL = "Ы"; // Символ для отображения здания на карте

    // implements - класс реализует методы объявленные в интерфейсе Comparable<>
    public static class Node implements Comparable<Node> {
        public int x;
        public int y;
        public int cost;

        public Node(int x, int y, int cost) {
            this.x = x;
            this.y = y;
            this.cost = cost;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.cost, other.cost);
        }
    }

    public void startBattle(Player player, Player computer) {
        Battle battle = new Battle(player, computer);
        battle.start();
    }

    public GameMap(int height, int width, Castle[] castles, Player player, Player computerPlayer) {
        this.height = height;
        this.width = width;
        this.map = new Cell[height][width];
        this.castles = castles;
        this.oasis = new Oasis(width/2-4,height/2+2);
        this.cursor = new Cursor(1,1, false,width,height);
        this.customMap = null;
        this.buildings = new ArrayList<>();
        InitializeMap(player, computerPlayer);
    }
    
    public GameMap(Map customMap, Castle[] castles, Player player, Player computerPlayer) {
        this.customMap = customMap;
        this.height = customMap.getHeight();
        this.width = customMap.getWidth();
        this.map = new Cell[height][width];
        this.castles = castles;
        this.cursor = new Cursor(1, 1, false, width, height);
        this.oasis = null;
        this.buildings = new ArrayList<>();
        InitializeCustomMap(player, computerPlayer);
    }

    private void InitializeMap(Player player, Player computerPlayer) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                if (i == j) {
                    this.map[i][j] = new Cell(Cell.road, i, j);
                } else {
                    if (i < 3 && j < 4) {
                        this.map[i][j] = new Cell(player.getTerrainSymbol(), i, j);
                    } else if (i > height - 4 && j > width - 5) {
                        this.map[i][j] = new Cell(computerPlayer.getTerrainSymbol(), i, j);
                    } else if (i == 3 && j < 5 || i < 4 && j == 4){
                        this.map[i][j] = new Cell(Cell.barrier, i, j);
                    } else if (i == height - 4 && j > width - 4 || i > height - 5 && j == width - 5){
                        this.map[i][j] = new Cell(Cell.barrier, i, j);
                    } else {
                        this.map[i][j] = new Cell(Cell.empty, i, j);
                    }
                }
            }
        }

        for (int i = 0; i < this.castles.length; i++) {
            int x = this.castles[i].getX();
            int y = this.castles[i].getY();

            if (i == 0) {
                this.map[y][x] = new Cell("И", x, y);
            } else {
                this.map[y][x] = new Cell("К", x, y);
            }
        }
        map[this.oasis.getOasisY()][this.oasis.getOasisX()] = new Cell(Cell.oasis, this.oasis.getOasisX(), this.oasis.getOasisY());
        
        // Размещаем здания на стандартной карте
        placeDefaultBuildings();
    }
    
    private void InitializeCustomMap(Player player, Player computerPlayer) {
        // Инициализация карты на основе пользовательской карты
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Map.TileType tileType = customMap.getTileAt(x, y);
                String symbol;
                
                switch (tileType) {
                    case EMPTY:
                        symbol = Cell.empty;
                        break;
                    case ROAD:
                        symbol = Cell.road;
                        break;
                    case OBSTACLE:
                        symbol = Cell.barrier;
                        break;
                    case WATER:
                        symbol = "~";
                        break;
                    case HUMAN:
                        symbol = player.getTerrainSymbol();
                        break;
                    case COMPUTER:
                        symbol = computerPlayer.getTerrainSymbol();
                        break;
                    default:
                        symbol = Cell.empty;
                }
                
                this.map[y][x] = new Cell(symbol, x, y);
                
                // Устанавливаем владельца клетки
                if (tileType == Map.TileType.HUMAN) {
                    this.map[y][x].setPlayer(player);
                } else if (tileType == Map.TileType.COMPUTER) {
                    this.map[y][x].setPlayer(computerPlayer);
                }
            }
        }
        
        // Размещение замков
        for (int i = 0; i < this.castles.length; i++) {
            int x = this.castles[i].getX();
            int y = this.castles[i].getY();
            
            if (i == 0) {
                this.map[y][x] = new Cell("И", x, y);
            } else {
                this.map[y][x] = new Cell("К", x, y);
            }
        }
        
        // Размещение оазисов из пользовательской карты
        for (Oasis customOasis : customMap.getOases()) {
            int x = customOasis.getOasisX();
            int y = customOasis.getOasisY();
            this.map[y][x] = new Cell(Cell.oasis, x, y);
            
            // Если это первый оазис, сохраняем его как основной
            if (this.oasis == null) {
                this.oasis = customOasis;
            }
        }
        
        // Если оазисов нет в пользовательской карте, создаем стандартный
        if (this.oasis == null) {
            this.oasis = new Oasis(width/2-4, height/2+2);
            this.map[this.oasis.getOasisY()][this.oasis.getOasisX()] = new Cell(Cell.oasis, this.oasis.getOasisX(), this.oasis.getOasisY());
        }
        
        // Если список зданий пуст, размещаем их по умолчанию
        if (buildings.isEmpty()) {
            placeDefaultBuildings();
        }
    }

    public void UpdateMapView(Player player, Player computerPlayer) {
        if (customMap != null) {
            InitializeCustomMap(player, computerPlayer);
        } else {
            InitializeMap(player, computerPlayer);
        }
        
        // Отображаем здания на карте (если они есть)
        for (int i = 0; i < buildings.size(); i++) {
            Building building = buildings.get(i);
            // В реальной реализации здесь нужно получать координаты зданий
            // Для примера просто отобразим их в соответствующих позициях
            if (i == 0 && customMap == null) { // Отель в правом верхнем углу для стандартной карты
                int x = width - 3;
                int y = 2;
                map[y][x] = new Cell(Cell.hotel, x, y);
            } else if (i == 1 && customMap == null) { // Кафе в левом нижнем углу для стандартной карты
                int x = 2;
                int y = height - 3;
                map[y][x] = new Cell(Cell.cafe, x, y);
            }
        }
    }

    // humanHero - активный герой человека.
    // anotherHeroes - активные герои ботов (по умолчанию массив из 1 элемента)
    public void Display(Hero humanHero, ArrayList<Hero> anotherHeroes, Player player, Player computerPlayer) {
        this.UpdateMapView(player, computerPlayer);

        // Отображаем героев и курсор
        this.map[humanHero.getY()][humanHero.getX()] = new Cell("Г", humanHero.getX(), humanHero.getY());

        for (int i = 0; i < anotherHeroes.size(); i++) {
            int x = anotherHeroes.get(i).getX();
            int y = anotherHeroes.get(i).getY();

            if (!Objects.equals(this.map[y][x].getType(), "Г")) {
                this.map[y][x].setType("" + i);
            }
        }

        if (this.cursor.isCursorActive()) {
            cursor.setTerrainUnderCursor(this.map[this.cursor.getCursorY()][this.cursor.getCursorX()].getType());
            this.map[this.cursor.getCursorY()][this.cursor.getCursorX()] = new Cell("+", this.cursor.getCursorX(), this.cursor.getCursorY());
        }

        // Отображаем карту
        System.out.println("\n==== КАРТА ИГРЫ ====");
        System.out.println("Условные обозначения:");
        System.out.println("Г - Герой игрока, 0 - Герой компьютера");
        System.out.println(Cell.hotel + " - Отель, " + Cell.cafe + " - Кафе");
        System.out.println("И - Замок игрока, К - Замок компьютера, О - Оазис");
        System.out.println("=====================");
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(this.map[i][j].getSymbol() + " ");
            }
            System.out.println();
        }
    }

    public int calculatePathCost(int startX, int startY, int endX, int endY, Player player) {
        int[][] distances = new int[height][width]; // distances - хранит стоимость достижения каждой клетки
        //на каждой итерации переменная row получает одну строку этого массива (т.е. одномерный массив int[]).
        //.fill - заполняет все элементы массива row указанным значением
        for (int[] row : distances) Arrays.fill(row, Integer.MAX_VALUE);
        distances[startY][startX] = 0;//стартовая клетка получает стоимость 0

        //создаётся очередь с приоритетом для обработки клеток в порядке возрастания стоимости.
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(new Node(startX, startY, 0));

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        while (!queue.isEmpty()) {
            Node current = queue.poll(); //из очереди извлекается клетка с наименьшей стоимостью
            if (current.x == endX && current.y == endY) return current.cost;
            if (current.cost > distances[current.y][current.x]) continue;

            //цикл по всем возможным направлениям движения
            for (int[] dir : directions) {
                //координаты соседней клетки
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                // проверка границ карты
                // continue - пропуска текущей итерации и перехода к следующей
                if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;

                // Проверка диагональных препятствий
                // проверка является ли данное направление диагональным
                if (dir[0] != 0 && dir[1] != 0) {
                    //проверка проходимости двух ключевых клеток (справа/слева и сверху/снизу)
                    if (map[current.y][current.x + dir[0]].getMovementCost(player) == Integer.MAX_VALUE ||
                            map[current.y + dir[1]][current.x].getMovementCost(player) == Integer.MAX_VALUE) {
                        continue;
                    }
                }

                // Получаем текущую клетку
                Cell cell = map[ny][nx];

                // Если клетка содержит курсор (+), используем terrainUnderCursor
                if (cell.getType().equals("+")) {
                    cell = new Cell(cursor.getTerrainUnderCursor(), nx, ny);
                }

                int moveCost = cell.getMovementCost(player);//узнаем стоимость клетки
                if (moveCost == Integer.MAX_VALUE) continue;

                // диагональное движение дороже в 1.4 раза
                if (Math.abs(dir[0]) + Math.abs(dir[1]) == 2) {
                    //Math.ceil() - округляет результат вверх до целого числа.
                    moveCost = (int) Math.ceil(moveCost * 1.4);
                }

                int newCost = current.cost + moveCost;
                if (newCost < distances[ny][nx]) {
                    distances[ny][nx] = newCost;
                    queue.add(new Node(nx, ny, newCost));
                }
            }
        }
        return Integer.MAX_VALUE; // Путь не найден
    }

//    public ArrayList<Node> getReachableCells(int startX, int startY, int maxCost, Player player) {
//        int[][] distances = new int[height][width];
//        for (int[] row : distances) Arrays.fill(row, Integer.MAX_VALUE);
//        distances[startY][startX] = 0;
//
//        PriorityQueue<Node> queue = new PriorityQueue<>();
//        queue.add(new Node(startX, startY, 0));
//        ArrayList<Node> reachable = new ArrayList<>();
//
//        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};
//
//        while (!queue.isEmpty()) {
//            Node current = queue.poll();
//            if (current.cost > maxCost) continue;
//
//            if (distances[current.y][current.x] == current.cost) {
//                reachable.add(current);
//
//                for (int[] dir : directions) {
//                    int nx = current.x + dir[0];
//                    int ny = current.y + dir[1];
//
//                    if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
//
//                    // Проверка диагональных препятствий
//                    if (dir[0] != 0 && dir[1] != 0) {
//                        if (map[current.y][current.x + dir[0]].getMovementCost(player) == Integer.MAX_VALUE ||
//                                map[current.y + dir[1]][current.x].getMovementCost(player) == Integer.MAX_VALUE) {
//                            continue;
//                        }
//                    }
//
//                    Cell cell = map[ny][nx];
//                    int moveCost = cell.getMovementCost(player);
//                    if (moveCost == Integer.MAX_VALUE) continue;
//
//                    if (Math.abs(dir[0]) + Math.abs(dir[1]) == 2) {
//                        moveCost = (int) Math.ceil(moveCost * 1.4);
//                    }
//
//                    int newCost = current.cost + moveCost;
//                    if (newCost <= maxCost && newCost < distances[ny][nx]) {
//                        distances[ny][nx] = newCost;
//                        queue.add(new Node(nx, ny, newCost));
//                    }
//                }
//            }
//        }
//        return reachable;
//    }

    public ArrayList<Node> getReachableCells(int startX, int startY, int maxCost, Player player) {
        int[][] distances = new int[height][width];
        for (int[] row : distances) Arrays.fill(row, Integer.MAX_VALUE);
        distances[startY][startX] = 0;

        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(new Node(startX, startY, 0));
        ArrayList<Node> reachable = new ArrayList<>();

        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Пропускаем если стоимость превышает лимит или клетка - барьер
            if (current.cost > maxCost ||
                    map[current.y][current.x].getMovementCost(player) == Integer.MAX_VALUE) {
                continue;
            }

            // Добавляем только если это новый оптимальный путь
            if (distances[current.y][current.x] == current.cost) {
                reachable.add(current);

                for (int[] dir : directions) {
                    int nx = current.x + dir[0];
                    int ny = current.y + dir[1];

                    // Проверка границ
                    if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;

                    // Проверка диагональных препятствий
                    if (dir[0] != 0 && dir[1] != 0) {
                        if (map[current.y][current.x + dir[0]].getMovementCost(player) == Integer.MAX_VALUE ||
                                map[current.y + dir[1]][current.x].getMovementCost(player) == Integer.MAX_VALUE) {
                            continue;
                        }
                    }

                    Cell cell = map[ny][nx];
                    int moveCost = cell.getMovementCost(player);
                    if (moveCost == Integer.MAX_VALUE) continue;

                    // Учет диагонального перемещения
                    if (Math.abs(dir[0]) + Math.abs(dir[1]) == 2) {
                        moveCost = (int) Math.ceil(moveCost * 1.4);
                    }

                    int newCost = current.cost + moveCost;
                    if (newCost <= maxCost && newCost < distances[ny][nx]) {
                        distances[ny][nx] = newCost;
                        queue.add(new Node(nx, ny, newCost));
                    }
                }
            }
        }

        // Фильтруем возможные дубликаты
        return new ArrayList<>(new HashSet<>(reachable));
    }


    public Castle getCrossedCastle(Hero hero) {
        for (Castle castle : this.castles) {
            if (castle.getX() == hero.getX() && castle.getY() == hero.getY()) {
                return castle;
            }
        }
        return null;
    }

    //добавила для тесттов
    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Cell[][] getMap() {
        return map;
    }

    public Castle[] getCastles() {
        return castles;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public Oasis getOasis() {
        return oasis;
    }

    public Map getCustomMap() {
        return customMap;
    }

    public String getName() {
        // Если есть пользовательская карта, возвращаем ее название
        if (customMap != null) {
            return customMap.getName();
        }
        // Иначе возвращаем стандартное название
        return "Standard Map";
    }

    public String getCreator() {
        // Если есть пользовательская карта, возвращаем имя ее создателя
        if (customMap != null) {
            return customMap.getCreator();
        }
        // Иначе возвращаем стандартное имя создателя
        return "System";
    }

    //silent если true, не выводить сообщения о добавлении
    public boolean addBuilding(Building building, int x, int y, boolean silent) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            if (!silent) {
                System.out.println("Ошибка: координаты здания находятся за пределами карты!");
            }
            return false;
        }
        
        // Проверяем, что позиция свободна
        Cell cell = map[y][x];
        if (!cell.getType().equals(Cell.empty) && !cell.getType().equals(Cell.road)) {
            if (!silent) {
                System.out.println("Ошибка: позиция (" + x + "," + y + ") уже занята!");
            }
            return false;
        }
        
        // Добавляем здание
        building.setPosition(x, y); // Устанавливаем позицию здания
        buildings.add(building);
        
        // Обновляем карту с соответствующим символом
        String buildingSymbol = Cell.building; // По умолчанию
        
        // Определяем тип здания
        if (building.getName().contains("Отель")) {
            buildingSymbol = Cell.hotel;
        } else if (building.getName().contains("Кафе")) {
            buildingSymbol = Cell.cafe;
        } else if (building.getName().contains("Парикмахерская")) {
            buildingSymbol = Cell.hairdresser;
        }
        
        map[y][x] = new Cell(buildingSymbol, x, y);
        
        if (!silent) {
            System.out.println("Здание " + building.getName() + " добавлено на карту в позиции (" + x + "," + y + ")");
        }
        return true;
    }


    public boolean addBuilding(Building building, int x, int y) {
        return addBuilding(building, x, y, false);
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public Building getBuildingAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }
        
        Cell cell = map[y][x];
        if (cell != null && (cell.getType().equals(Cell.hotel) || cell.getType().equals(Cell.cafe) || 
                            cell.getType().equals(Cell.hairdresser) || cell.getType().equals(Cell.building))) {
            // Поиск здания по координатам
            for (Building building : buildings) {
                // Проверяем, совпадают ли координаты здания с запрошенными
                if (building.getX() == x && building.getY() == y) {
                    return building;
                }
            }
        }
        return null;
    }

    public void closeAllBuildings() {
        for (Building building : buildings) {
            building.shutdown();
        }
    }

    private void placeDefaultBuildings() {
        // Очищаем существующие здания
        buildings.clear();
        
        // Создаем отель в правом верхнем углу
        Hotel hotel = new Hotel();
        int hotelX = width - 3;
        int hotelY = 2;
        boolean hotelAdded = addBuilding(hotel, hotelX, hotelY, true);
        
        // Создаем кафе в левом нижнем углу
        Cafe cafe = new Cafe();
        int cafeX = 2;
        int cafeY = height - 3;
        boolean cafeAdded = addBuilding(cafe, cafeX, cafeY, true);
        
        // Создаем парикмахерскую в точке (9,1)
        Hairdresser hairdresser = new Hairdresser();
        int hairdresserX = 9;
        int hairdresserY = 1;
        boolean hairdresserAdded = addBuilding(hairdresser, hairdresserX, hairdresserY, true);
        
        // Проверяем, что здания были успешно добавлены
        if (!hotelAdded || !cafeAdded || !hairdresserAdded) {
            System.err.println("ОШИБКА: Не удалось разместить все здания на карте!");
            if (!hotelAdded) System.err.println("- Отель не был добавлен");
            if (!cafeAdded) System.err.println("- Кафе не было добавлено");
            if (!hairdresserAdded) System.err.println("- Парикмахерская не была добавлена");
        }
        
        // Выводим информацию о зданиях
        if (!buildings.isEmpty()) {
            System.out.println("На карте размещено " + buildings.size() + " зданий:");
            for (Building building : buildings) {
                System.out.println("- " + building.getName() + " на позиции (" + 
                                 building.getX() + "," + 
                                 building.getY() + "), вместимость: " + 
                                 building.getCapacity());
            }
        } else {
            System.err.println("ОШИБКА: Список зданий пуст!");
        }
    }

    public boolean playerPlaceBuilding(String buildingType, int x, int y) {
        Building building = null;
        
        if ("Hotel".equalsIgnoreCase(buildingType)) {
            building = new Hotel();
        } else if ("Cafe".equalsIgnoreCase(buildingType)) {
            building = new Cafe();
        } else if ("Hairdresser".equalsIgnoreCase(buildingType)) {
            building = new Hairdresser();
        } else {
            System.out.println("Неизвестный тип здания: " + buildingType);
            return false;
        }
        
        if (building != null) {
            return addBuilding(building, x, y);
        }
        
        return false;
    }

    public void showAvailableBuildings() {
        System.out.println("\n=== Доступные здания для размещения ===");
        System.out.println("1. Отель «У погибшего альпиниста»");
        System.out.println("2. Кафе «Сырники от тети Глаши»");
        System.out.println("3. Парикмахерская «Отрезанное ухо»");
        System.out.println("=======================================");
    }
}