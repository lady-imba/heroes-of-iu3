package Building;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import Player.Player;
import Hero.Hero;
import Unit.Unit;

import static org.junit.jupiter.api.Assertions.*;

public class CafeTest {
    private TestCafe cafe;
    private Visitor playerVisitor;
    private Player testPlayer;
    private Hero testHero;
    private Unit testUnit;
    
    // Тестовый класс, расширяющий Cafe для тестирования
    private static class TestCafe extends Cafe {
        public TestCafe() {
            super();
        }
        
        // Добавляем методы, необходимые для тестирования
        public boolean hasActiveVisitor(Visitor visitor) {
            synchronized(activeVisitors) {
                return activeVisitors.containsKey(visitor);
            }
        }
        
        public int countActiveVisitors() {
            synchronized(activeVisitors) {
                return activeVisitors.size();
            }
        }
        
        // Метод для тестирования эффектов услуг
        public void applyServiceEffectForTest(Visitor visitor, Service service) {
            applyServiceEffect(visitor, service);
        }
    }
    
    @BeforeEach
    void setUp() {
        cafe = new TestCafe();
        
        // Создаем тестового игрока и посетителя
        testHero = new Hero(0, 0, 5, "TestHero");
        testPlayer = new Player(testHero, "T", false, "TestPlayer");
        playerVisitor = new Visitor("TestPlayer", testPlayer);
        
        // Добавляем юнита к герою
        testUnit = new Unit("Test Unit", 100, 10, 2, 3);
        testHero.addUnit(testUnit);
    }
    
    @Test
    @DisplayName("Проверка инициализации кафе")
    void testCafeInitialization() {
        assertEquals("Кафе «Сырники от тети Глаши»", cafe.getName());
        assertEquals(12, cafe.getCapacity()); // 3 официанта * 4 клиента
        assertEquals(2, cafe.getServicesCount()); // Должно быть 2 услуги
    }
    
    @Test
    @DisplayName("Проверка услуг кафе")
    void testCafeServices() {
        Service service1 = cafe.getService(0);
        Service service2 = cafe.getService(1);
        
        assertEquals("Просто перекус", service1.getName());
        assertEquals(15, service1.getDuration());
        assertEquals(2, service1.getEffectValue());
        assertEquals(Service.ServiceType.MOVEMENT_BOOST, service1.getType());
        
        assertEquals("Плотный обед", service2.getName());
        assertEquals(30, service2.getDuration());
        assertEquals(3, service2.getEffectValue());
        assertEquals(Service.ServiceType.MOVEMENT_BOOST, service2.getType());
    }
    
    @Test
    @DisplayName("Проверка приема посетителей в кафе")
    void testAcceptVisitor() {
        // Добавляем посетителя
        ServiceTask task = cafe.visit(playerVisitor, 0);
        
        // Проверяем, что посетитель был добавлен
        assertNotNull(task);
        assertTrue(cafe.hasActiveVisitor(playerVisitor));
        assertEquals(1, cafe.countActiveVisitors());
    }
    
    @Test
    @DisplayName("Проверка работы с множеством посетителей")
    void testMultipleVisitors() {
        // Добавляем несколько посетителей
        for (int i = 0; i < 5; i++) {
            Visitor tempVisitor = new Visitor("Visitor" + i, null);
            ServiceTask task = cafe.visit(tempVisitor, 0);
            assertNotNull(task);
        }
        
        // Проверяем счетчики
        assertEquals(5, cafe.countActiveVisitors());
        assertTrue(cafe.countActiveVisitors() <= cafe.getCapacity());
    }
    
    @Test
    @DisplayName("Проверка эффекта услуги на перемещение юнитов")
    void testMovementBoostEffect() {
        // Запоминаем начальное значение
        int initialMovement = testUnit.getMovement();
        
        // Получаем услугу и применяем эффект
        Service service = cafe.getService(0); // "Просто перекус"
        cafe.applyServiceEffectForTest(playerVisitor, service);
        
        // Проверяем, что перемещение увеличилось на 2
        assertEquals(initialMovement + 2, testUnit.getMovement());
    }
} 