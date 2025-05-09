package Save;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import Player.HumanPlayer;
import Player.ComputerPlayer;
import Hero.Hero;
import Map.Map;
import Castle.Castle;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameSaveTest {
    private HumanPlayer humanPlayer;
    private ComputerPlayer computerPlayer;
    private Map map;
    private List<Castle> castles;
    private String timestamp;
    private boolean isAutoSave;
    private int turnsLeft;
    private GameSave gameSave;

    @BeforeEach
    void setUp() {
        // Создаем тестовых игроков
        humanPlayer = new HumanPlayer(new Hero(1, 1, 5, "TestHero"), "&", false, "TestPlayer");
        computerPlayer = new ComputerPlayer(new Hero(8, 8, 5, "ComputerHero"), "$", true, "TestComputer");
        
        // Создаем тестовую карту
        map = new Map("TestMap", "TestCreator", 10, 10);
        
        // Создаем замки
        Castle playerCastle = new Castle(humanPlayer, 1, 1);
        Castle computerCastle = new Castle(computerPlayer, 8, 8);
        humanPlayer.setCastle(playerCastle);
        computerPlayer.setCastle(computerCastle);
        
        castles = new ArrayList<>();
        castles.add(playerCastle);
        castles.add(computerCastle);
        
        // Устанавливаем параметры сохранения
        timestamp = "2023-05-01_12-00-00";
        isAutoSave = false;
        turnsLeft = 50;
        
        // Создаем объект GameSave
        gameSave = new GameSave(humanPlayer, computerPlayer, map, castles, timestamp, isAutoSave, turnsLeft);
    }

    @Test
    @DisplayName("Проверка конструктора и геттеров")
    void testConstructorAndGetters() {
        assertEquals(humanPlayer, gameSave.getPlayer(), "Игрок должен быть правильно установлен");
        assertEquals(computerPlayer, gameSave.getComputer(), "Компьютер должен быть правильно установлен");
        assertEquals(map, gameSave.getMap(), "Карта должна быть правильно установлена");
        assertEquals(castles, gameSave.getCastles(), "Замки должны быть правильно установлены");
        assertEquals(timestamp, gameSave.getTimestamp(), "Временная метка должна быть правильно установлена");
        assertEquals(isAutoSave, gameSave.isAutoSave(), "Флаг автосохранения должен быть правильно установлен");
        assertEquals(turnsLeft, gameSave.getTurnsLeft(), "Количество оставшихся ходов должно быть правильно установлено");
        assertEquals(0, gameSave.getBonus(), "Бонус по умолчанию должен быть 0");
        assertNull(gameSave.getBonusWinner(), "Победитель бонуса по умолчанию должен быть null");
    }

    @Test
    @DisplayName("Проверка установки и получения бонуса")
    void testBonusSetterAndGetter() {
        // Устанавливаем бонус
        int bonus = 150;
        gameSave.setBonus(bonus);
        
        // Проверяем, что бонус правильно установлен
        assertEquals(bonus, gameSave.getBonus(), "Бонус должен быть правильно установлен");
    }

    @Test
    @DisplayName("Проверка установки и получения победителя бонуса")
    void testBonusWinnerSetterAndGetter() {
        // Устанавливаем победителя бонуса
        String winner = "player";
        gameSave.setBonusWinner(winner);
        
        // Проверяем, что победитель бонуса правильно установлен
        assertEquals(winner, gameSave.getBonusWinner(), "Победитель бонуса должен быть правильно установлен");
    }

    @Test
    @DisplayName("Проверка метода toString для сохранения без бонуса")
    void testToStringWithoutBonus() {
        // Формируем ожидаемую строку
        String expected = "Ручное сохранение от " + timestamp;
        
        // Проверяем, что метод toString возвращает правильную строку
        assertEquals(expected, gameSave.toString(), "toString должен вернуть правильную строку для сохранения без бонуса");
    }

    @Test
    @DisplayName("Проверка метода toString для сохранения с бонусом для игрока")
    void testToStringWithPlayerBonus() {
        // Устанавливаем бонус и победителя
        int bonus = 150;
        String winner = "player";
        gameSave.setBonus(bonus);
        gameSave.setBonusWinner(winner);
        
        // Формируем ожидаемую строку
        String expected = "Ручное сохранение от " + timestamp + " (Бонус: " + bonus + " для игрока)";
        
        // Проверяем, что метод toString возвращает правильную строку
        assertEquals(expected, gameSave.toString(), "toString должен вернуть правильную строку для сохранения с бонусом для игрока");
    }

    @Test
    @DisplayName("Проверка метода toString для сохранения с бонусом для компьютера")
    void testToStringWithComputerBonus() {
        // Устанавливаем бонус и победителя
        int bonus = 150;
        String winner = "computer";
        gameSave.setBonus(bonus);
        gameSave.setBonusWinner(winner);
        
        // Формируем ожидаемую строку
        String expected = "Ручное сохранение от " + timestamp + " (Бонус: " + bonus + " для компьютера)";
        
        // Проверяем, что метод toString возвращает правильную строку
        assertEquals(expected, gameSave.toString(), "toString должен вернуть правильную строку для сохранения с бонусом для компьютера");
    }

    @Test
    @DisplayName("Проверка метода toString для автосохранения")
    void testToStringForAutoSave() {
        // Создаем автосохранение
        GameSave autoSave = new GameSave(humanPlayer, computerPlayer, map, castles, timestamp, true, turnsLeft);
        
        // Формируем ожидаемую строку
        String expected = "Автосохранение от " + timestamp;
        
        // Проверяем, что метод toString возвращает правильную строку
        assertEquals(expected, autoSave.toString(), "toString должен вернуть правильную строку для автосохранения");
    }
} 