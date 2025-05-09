package Building;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import Player.Player;
import Hero.Hero;
import static org.junit.jupiter.api.Assertions.*;

public class VisitorTest {
    private Visitor playerVisitor;
    private Visitor npcVisitor;
    private Player testPlayer;
    private Hero testHero;
    private Building testBuilding;
    
    @BeforeEach
    void setUp() {
        // Создаем тестового игрока
        testHero = new Hero(0, 0, 5, "TestHero");
        testPlayer = new Player(testHero, "T", false, "TestPlayer");
        
        // Создаем посетителей
        playerVisitor = new Visitor("TestPlayer", testPlayer);
        npcVisitor = new Visitor("NPC Visitor", false);
        
        // Создаем тестовое здание
        testBuilding = new Building("Test Building", 5) {
            @Override
            protected void applyServiceEffect(Visitor visitor, Service service) {
                // Пустая реализация для тестирования
            }
        };
    }
    
    @Test
    @DisplayName("Проверка инициализации посетителя-игрока")
    void testPlayerVisitorInitialization() {
        assertEquals("TestPlayer", playerVisitor.getName());
        assertTrue(playerVisitor.isPlayer());
        assertNotNull(playerVisitor.getPlayer());
        assertEquals(testPlayer, playerVisitor.getPlayer());
    }
    
    @Test
    @DisplayName("Проверка инициализации посетителя-NPC")
    void testNPCVisitorInitialization() {
        assertEquals("NPC Visitor", npcVisitor.getName());
        assertFalse(npcVisitor.isPlayer());
        assertNull(npcVisitor.getPlayer());
    }
    
    @Test
    @DisplayName("Проверка установки текущего здания")
    void testSetCurrentBuilding() {
        assertNull(playerVisitor.getCurrentBuilding());
        
        playerVisitor.setCurrentBuilding(testBuilding);
        assertEquals(testBuilding, playerVisitor.getCurrentBuilding());
        
        playerVisitor.setCurrentBuilding(null);
        assertNull(playerVisitor.getCurrentBuilding());
    }
    
    @Test
    @DisplayName("Проверка установки и получения времени окончания услуги")
    void testServiceEndTime() {
        // По умолчанию 0
        assertEquals(0, playerVisitor.getServiceEndTime());
        
        // Устанавливаем время через метод setCurrentActivity
        long endTime = 123456L;
        ServiceTask mockTask = new ServiceTask(null);
        playerVisitor.setCurrentActivity(mockTask, testBuilding, endTime);
        assertEquals(endTime, playerVisitor.getServiceEndTime());
    }
    
    @Test
    @DisplayName("Проверка бонуса услуги для посетителя")
    void testBonusService() {
        // По умолчанию бонус отключен
        assertFalse(playerVisitor.hasBonusService());
        
        // Включаем бонус
        playerVisitor.setBonusService(true);
        assertTrue(playerVisitor.hasBonusService());
        
        // Выключаем бонус
        playerVisitor.setBonusService(false);
        assertFalse(playerVisitor.hasBonusService());
    }
} 