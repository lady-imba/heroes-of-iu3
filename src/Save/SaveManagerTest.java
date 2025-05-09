package Save;

import Map.Map;
import Player.*;
import Castle.Castle;
import Hero.Hero;
import UI.GameMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Unit.Unit;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SaveManagerTest {
    
    private HumanPlayer humanPlayer;
    private ComputerPlayer computerPlayer;
    private Map map;
    private GameMap gameMap;
    private List<Castle> castles;
    private String playerName = "TestPlayer";
    private String savesPath;
    
    @BeforeEach
    public void setUp() {
        // Создаем тестовых игроков
        Hero humanHero = new Hero(1, 1, 10, "HumanHero");
        humanHero.addUnit(new Unit("Копейщик", 20, 2, 10, 2));
        humanPlayer = new HumanPlayer(humanHero, "&", false, playerName);
        
        Hero computerHero = new Hero(8, 8, 10, "ComputerHero");
        computerHero.addUnit(new Unit("Копейщик", 20, 2, 10, 2));
        computerPlayer = new ComputerPlayer(computerHero, "$", true, "TestComputer");
        
        // Создаем карту
        map = new Map("TestMap", "TestCreator", 10, 10);
        
        // Создаем замки
        Castle humanCastle = new Castle(humanPlayer, 0, 0);
        Castle computerCastle = new Castle(computerPlayer, 9, 9);
        humanPlayer.setCastle(humanCastle);
        computerPlayer.setCastle(computerCastle);
        castles = Arrays.asList(humanCastle, computerCastle);
        
        // Создаем игровую карту
        gameMap = new GameMap(map, castles.toArray(new Castle[0]), humanPlayer, computerPlayer);
        
        // Запоминаем путь к сохранениям
        savesPath = "saves" + File.separator + playerName;
        
        // Создаем директорию для сохранений, если её нет
        new File(savesPath).mkdirs();
    }
    
    @AfterEach
    public void tearDown() {
        // Удаляем тестовые сохранения
        File dir = new File(savesPath);
        if (dir.exists()) {
            File[] files = dir.listFiles((d, name) -> name.startsWith("test_") && name.endsWith(".sav"));
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }
    
    @Test
    public void testAutoSave() {
        // Тестируем автоматическое сохранение
        String result = SaveManager.autoSave(humanPlayer, computerPlayer, gameMap, castles, 50);
        
        // Проверяем успешность сохранения
        assertFalse(result.isEmpty(), "Автосохранение должно быть успешным");
        
        // Проверяем список сохранений
        List<String> saves = SaveManager.getSavesByPlayer(playerName);
        assertFalse(saves.isEmpty(), "Список сохранений не должен быть пустым после сохранения");
        
        // Проверяем что хотя бы одно сохранение - автосохранение
        boolean hasAutoSave = false;
        for (String save : saves) {
            if (save.startsWith("auto_")) {
                hasAutoSave = true;
                break;
            }
        }
        assertTrue(hasAutoSave, "Должно быть хотя бы одно автосохранение");
    }
    
    @Test
    public void testManualSave() {
        // Тестируем ручное сохранение
        SaveManager.setTestMode(true);
        String result = SaveManager.manualSave(humanPlayer, computerPlayer, gameMap, castles, 50);
        
        // Проверяем успешность сохранения
        assertFalse(result.isEmpty(), "Ручное сохранение должно быть успешным");
        
        // Проверяем список сохранений
        List<String> saves = SaveManager.getSavesByPlayer(playerName);
        assertFalse(saves.isEmpty(), "Список сохранений не должен быть пустым после сохранения");
        
        // Проверяем что хотя бы одно сохранение - ручное сохранение
        boolean hasManualSave = false;
        for (String save : saves) {
            if (save.startsWith("manual_")) {
                hasManualSave = true;
                break;
            }
        }
        assertTrue(hasManualSave, "Должно быть хотя бы одно ручное сохранение");
    }
    

    @Test
    public void testCleanupOldAutoSaves() {
        // Создаем несколько автосохранений
        for (int i = 0; i < 10; i++) {
            SaveManager.autoSave(humanPlayer, computerPlayer, gameMap, castles, 50 - i);
            
            // Небольшая пауза чтобы убедиться в разных временах модификации
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // Получаем список сохранений до очистки
        List<String> savesBefore = SaveManager.getSavesByPlayer(playerName);
        
        // Очищаем старые автосохранения, оставляя 2
        SaveManager.cleanupOldAutoSaves(playerName, 2);
        
        // Получаем список сохранений после очистки
        List<String> savesAfter = SaveManager.getSavesByPlayer(playerName);
        
        // Считаем количество автосохранений
        long autoSavesCount = savesAfter.stream()
                .filter(name -> name.startsWith("auto_"))
                .count();
        
        // Проверяем что осталось ровно 2 автосохранения
        assertEquals(2, autoSavesCount, "После очистки должно остаться ровно 3 автосохранения");
    }
    
    @Test
    public void testSaveAndLoadWithTerritory() {
        // Модифицируем карту, чтобы добавить территории игроков
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                map.setTile(x, y, Map.TileType.HUMAN);
            }
        }
        
        for (int y = 7; y < 10; y++) {
            for (int x = 7; x < 10; x++) {
                map.setTile(x, y, Map.TileType.COMPUTER);
            }
        }
        
        // Создаем новую игровую карту с обновленной картой
        gameMap = new GameMap(map, castles.toArray(new Castle[0]), humanPlayer, computerPlayer);
        
        // Сохраняем игру
        String saveName = SaveManager.manualSave(humanPlayer, computerPlayer, gameMap, castles, 50);
        
        // Получаем список сохранений
        List<String> saves = SaveManager.getSavesByPlayer(playerName);
        assertTrue(saves.contains(saveName), "Список сохранений должен содержать созданное сохранение");
        
        
        // Загружаем сохранение
        GameSave gameSave = SaveManager.loadGame(playerName, saveName);

        // Проверяем территории на карте
        assert gameSave != null;
        Map loadedMap = gameSave.getMap();
        
        // Проверяем территорию человека
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                assertEquals(Map.TileType.HUMAN, loadedMap.getTileAt(x, y),
                        "Клетка (" + x + "," + y + ") должна быть территорией человека");
            }
        }
        
        // Проверяем территорию компьютера
        for (int y = 7; y < 10; y++) {
            for (int x = 7; x < 10; x++) {
                assertEquals(Map.TileType.COMPUTER, loadedMap.getTileAt(x, y),
                        "Клетка (" + x + "," + y + ") должна быть территорией компьютера");
            }
        }
    }
} 