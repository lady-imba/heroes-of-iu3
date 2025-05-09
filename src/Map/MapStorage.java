package Map;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import Castle.Castle;
import Oasis.Oasis;
import Player.Player;
import Hero.Hero;

public class MapStorage {
    private static final String MAPS_DIRECTORY = "maps";

    //выполняется один раз при загрузке класса в JVM
    static {
        File mapsDir = new File(MAPS_DIRECTORY);
        if (!mapsDir.exists()) {
            mapsDir.mkdir(); //cоздаёт папку "maps" в корне проекта
        }
    }

    public static boolean saveMap(Map map) throws FileNotFoundException {
        Castle[] castles = map.getCastles();
        if (castles[0] == null || castles[1] == null) {
            System.out.println("Ошибка: карта должна содержать ровно два замка!");
            return false;
        }

        //Формируем путь к папке пользователя
        String userDir = MAPS_DIRECTORY + File.separator + map.getCreator();
        File userDirectory = new File(userDir);
        if (!userDirectory.exists()) {
            userDirectory.mkdir();
        }

        //сохранение карты в файл
        //PrintWriter — это класс, используемый для записи в файл. Создаёт файл с указанным именем и записывает в него текстовые данные.
        try (PrintWriter writer = new PrintWriter(new File(userDir, map.getName() + ".txt"))) {
            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    char symbol = getTileSymbol(map, x, y);
                    writer.print(symbol);
                }
                writer.println();
            }
        } catch (IOException e) {
            System.err.println("Ошибка сохранения карты: " + e.getMessage());
            return false;
        }

        saveMapMetadata(map.getName(), map.getCreator());
        
        return true;
    }
    
    
    public static char getTileSymbol(Map map, int x, int y) {
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

    //загружает карту из файла
    public static Map loadMap(String mapName, String creator) {
        String userDir = MAPS_DIRECTORY + File.separator + creator;
        // File представляет абстрактный путь к файлу
        File mapFile = new File(userDir, mapName + ".txt");
        
        if (!mapFile.exists()) {
            System.err.println("Файл карты не найден: " + mapFile.getPath());
            return null;
        }

        //try-with-resources - специальная форма try-catch, которая автоматически закрывает ресурсы
        //FileReader читает файл посимвольно
        //
        //BufferedReader добавляет буферизацию для эффективности
        //
        //mapFile - объект File, представляющий файл карты
        try (BufferedReader reader = new BufferedReader(new FileReader(mapFile))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            
            if (lines.isEmpty()) {
                return null;
            }

            //height - количество строк (высота карты) width - длина первой строки (ширина карты)
            
            int height = lines.size();
            int width = lines.getFirst().length();
            
            String mapCreator = loadMapCreator(mapName, creator);
            
            Map map = new Map(mapName, mapCreator, width, height);
            
            for (int y = 0; y < height; y++) {
                String currentLine = lines.get(y);
                for (int x = 0; x < width; x++) {
                    char symbol = currentLine.charAt(x);
                    processSymbol(map, symbol, x, y);
                }
            }
            
            return map;
        } catch (IOException e) {
            System.err.println("Ошибка загрузки карты: " + e.getMessage());
            return null;
        }
    }
    
    private static String loadMapCreator(String mapName, String creator) {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(MAPS_DIRECTORY + File.separator + creator, mapName + ".meta")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("creator=")) {
                    //извлекаем часть подстроки
                    return line.substring(8);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки метаданных карты: " + e.getMessage());
        }
        return "Unknown";
    }
    
    private static void processSymbol(Map map, char symbol, int x, int y) {
        switch (symbol) {
            case '#':
                map.setTile(x, y, Map.TileType.ROAD);
                break;
            case 'X':
                map.setTile(x, y, Map.TileType.OBSTACLE);
                break;
            case '~':
                map.setTile(x, y, Map.TileType.WATER);
                break;
            case 'C':
                Player dummyPlayer = new Player(new Hero(0, 0, 0, "dummy-hero"), "&", false, "Dummy");
                Castle castle = new Castle(dummyPlayer, x, y);
                Castle[] castles = map.getCastles();
                int castleIndex = (castles[0] == null) ? 0 : 1;
                map.setCastle(castleIndex, castle);
                break;
            case 'O':
                Oasis oasis = new Oasis(x, y);
                map.addOasis(oasis);
                break;
            default:
                map.setTile(x, y, Map.TileType.EMPTY);
                break;
        }
    }

    public static List<String> getAllMapNames() {
        List<String> mapNames = new ArrayList<>();
        File mapsDir = new File(MAPS_DIRECTORY);
        
        File[] userDirs = mapsDir.listFiles(File::isDirectory);
        
        if (userDirs != null) {
            for (File userDir : userDirs) {
                String creator = userDir.getName();
                File[] mapFiles = userDir.listFiles((dir, name) -> name.endsWith(".txt"));
                
                if (mapFiles != null) {
                    for (File mapFile : mapFiles) {
                        String mapName = mapFile.getName().replace(".txt", "");
                        mapNames.add(creator + ":" + mapName);
                    }
                }
            }
        }
        
        return mapNames;
    }
    
    public static List<String> getMapsByCreator(String creator) {
        List<String> mapNames = new ArrayList<>();
        String userDir = MAPS_DIRECTORY + File.separator + creator;
        File userDirectory = new File(userDir);
        
        if (userDirectory.exists() && userDirectory.isDirectory()) {
            File[] mapFiles = userDirectory.listFiles((dir, name) -> name.endsWith(".txt"));
            
            if (mapFiles != null) {
                for (File mapFile : mapFiles) {
                    mapNames.add(mapFile.getName().replace(".txt", ""));
                }
            }
        }
        
        return mapNames;
    }
    
    public static boolean isMapCreatedBy(String mapName, String creator) {
        String userDir = MAPS_DIRECTORY + File.separator + creator;
        File mapFile = new File(userDir, mapName + ".txt");
        return mapFile.exists();
    }

    public static boolean deleteMap(String mapName, String creator) {
        String userDir = MAPS_DIRECTORY + File.separator + creator;
        File mapFile = new File(userDir, mapName + ".txt");
        if (mapFile.exists()) {
            return mapFile.delete();
        }
        
        File metaFile = new File(MAPS_DIRECTORY, mapName + ".meta");
        if (metaFile.exists()) {
            return metaFile.delete();
        }
        return false;
    }
    
    public static void displayMapInConsole(String mapName, String creator) {
        Map map = loadMap(mapName, creator);
        if (map == null) {
            System.out.println("Map not found: " + mapName + " by " + creator);
            return;
        }
        
        System.out.println("\nКарта: " + map.getName());
        System.out.println("Автор: " + map.getCreator());
        System.out.println("Размер: " + map.getWidth() + "x" + map.getHeight());
        System.out.println();
        
        System.out.print("+");
        for (int x = 0; x < map.getWidth(); x++) {
            System.out.print("-");
        }
        System.out.println("+");
        
        for (int y = 0; y < map.getHeight(); y++) {
            System.out.print("|");
            for (int x = 0; x < map.getWidth(); x++) {
                char symbol = getTileSymbol(map, x, y);
                System.out.print(symbol);
            }
            System.out.println("|");
        }
        
        System.out.print("+");
        for (int x = 0; x < map.getWidth(); x++) {
            System.out.print("-");
        }
        System.out.println("+");
        
        System.out.println("\nЛегенда:");
        System.out.println("  . - Пустой тайл");
        System.out.println("  # - Дорога");
        System.out.println("  X - Препятствие");
        System.out.println("  ~ - Вода");
        System.out.println("  C - Замок");
        System.out.println("  O - Oasis");
    }
    
    public static void saveMapMetadata(String mapName, String creator) throws FileNotFoundException {
        String userDir = MAPS_DIRECTORY + File.separator + creator;
        File mapFile = new File(userDir, mapName + ".meta");
        if (mapFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new File(userDir, mapName + ".meta"))) {
                writer.println("creator=" + creator);
            }
        } else {
            try (PrintWriter writer = new PrintWriter(new File(userDir, mapName + ".meta"))) {
                writer.println("creator=" + creator);
            }
        }
    }
    
        
    public static Map.TileType getTileTypeFromSymbol(char symbol) {
        switch (symbol) {
            case '.':
                return Map.TileType.EMPTY;
            case '#':
                return Map.TileType.ROAD;
            case 'X':
                return Map.TileType.OBSTACLE;
            case '~':
                return Map.TileType.WATER;
            case '&':
                return Map.TileType.HUMAN;
            case '$':
                return Map.TileType.COMPUTER;
            default:
                return Map.TileType.EMPTY;
        }
    }
}