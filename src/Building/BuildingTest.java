package Building;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import Player.Player;
import Hero.Hero;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BuildingTest {
    private TestBuilding testBuilding;
    private Visitor playerVisitor;
    private Player testPlayer;
    private Hero testHero;
    
    // Тестовый класс, расширяющий Building для тестирования
    private static class TestBuilding extends Building {
        public TestBuilding(String name, int capacity) {
            super(name, capacity);
            // Добавляем тестовые услуги напрямую в services
            services.add(new Service("Test Service 1", "Description 1", 15, 1, Service.ServiceType.HEALTH_BOOST));
            services.add(new Service("Test Service 2", "Description 2", 30, 2, Service.ServiceType.MOVEMENT_BOOST));
        }
        
        @Override
        protected void applyServiceEffect(Visitor visitor, Service service) {
            // Пустая реализация для тестирования
        }
        
        // Вспомогательный метод для тестирования
        public List<Service> getServices() {
            return new ArrayList<>(services);
        }
        
        // Добавляем методы, необходимые для тестирования
        public boolean hasActiveVisitor(Visitor visitor) {
            synchronized(activeVisitors) {
                return activeVisitors.containsKey(visitor);
            }
        }
        
        public void removeActiveVisitor(Visitor visitor) {
            synchronized(activeVisitors) {
                ServiceTask task = activeVisitors.remove(visitor);
                if (task != null) {
                    visitor.setCurrentBuilding(null); // Очищаем ссылку на здание
                }
            }
        }
        
        public int countActiveVisitors() {
            synchronized(activeVisitors) {
                return activeVisitors.size();
            }
        }
    }
    
    @BeforeEach
    void setUp() {
        // Создаем тестовое здание с вместимостью 5
        testBuilding = new TestBuilding("Test Building", 5);
        
        // Создаем тестового игрока и посетителя
        testHero = new Hero(0, 0, 5, "TestHero");
        testPlayer = new Player(testHero, "T", false, "TestPlayer");
        playerVisitor = new Visitor("TestPlayer", testPlayer);
    }
    
    @Test
    @DisplayName("Проверка инициализации здания")
    void testBuildingInitialization() {
        assertEquals("Test Building", testBuilding.getName());
        assertEquals(5, testBuilding.getCapacity());
        assertEquals(2, testBuilding.getServicesCount());
    }
    
    @Test
    @DisplayName("Проверка получения услуги по индексу")
    void testGetService() {
        Service service = testBuilding.getService(0);
        assertNotNull(service);
        assertEquals("Test Service 1", service.getName());
        assertEquals("Description 1", service.getDescription());
        assertEquals(15, service.getDuration());
    }
    
    @Test
    @DisplayName("Проверка подсчета посетителей")
    void testCountVisitors() {
        // Изначально здание пустое
        assertEquals(0, testBuilding.countActiveVisitors());
        
        // Добавляем посетителя и проверяем счетчики
        ServiceTask task = testBuilding.visit(playerVisitor, 0);
        assertNotNull(task);
        assertEquals(1, testBuilding.countActiveVisitors());
        assertEquals(1, testBuilding.countTotalVisitors());
    }
    
    @Test
    @DisplayName("Проверка удаления посетителей")
    void testRemoveVisitor() {
        // Добавляем и затем удаляем посетителя
        ServiceTask task = testBuilding.visit(playerVisitor, 0);
        assertNotNull(task);
        assertTrue(testBuilding.hasActiveVisitor(playerVisitor));
        
        testBuilding.removeActiveVisitor(playerVisitor);
        assertFalse(testBuilding.hasActiveVisitor(playerVisitor));
        assertEquals(0, testBuilding.countActiveVisitors());
    }
    
    @Test
    @DisplayName("Проверка отклонения посещения при отсутствии свободных мест")
    void testRejectVisitWhenFull() {
        // Заполняем здание до предела
        for (int i = 0; i < testBuilding.getCapacity(); i++) {
            Visitor tempVisitor = new Visitor("Visitor" + i, false);  // Используем false вместо null
            ServiceTask task = testBuilding.visit(tempVisitor, 0);
            assertNotNull(task);
        }
        
        // Пытаемся добавить еще одного посетителя
        Visitor extraVisitor = new Visitor("ExtraVisitor", false);  // Используем false вместо null
        ServiceTask task = testBuilding.visit(extraVisitor, 0);
        
        // Проверяем, что новый посетитель не был добавлен
        assertNull(task);
        assertFalse(testBuilding.hasActiveVisitor(extraVisitor));
        assertEquals(testBuilding.getCapacity(), testBuilding.countActiveVisitors());
    }
} 