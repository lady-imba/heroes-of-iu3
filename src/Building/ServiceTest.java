package Building;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {
    
    @Test
    @DisplayName("Проверка инициализации услуги")
    void testServiceInitialization() {
        Service service = new Service("Test Service", "Test Description", 15, 5, Service.ServiceType.HEALTH_BOOST);
        
        assertEquals("Test Service", service.getName());
        assertEquals("Test Description", service.getDescription());
        assertEquals(15, service.getDuration());
        assertEquals(5, service.getEffectValue());
        assertEquals(Service.ServiceType.HEALTH_BOOST, service.getType());
    }
    
    @Test
    @DisplayName("Проверка услуги с повышением здоровья")
    void testHealthBoostService() {
        Service service = new Service("Health Boost", "Increases health", 20, 10, Service.ServiceType.HEALTH_BOOST);
        
        assertEquals(Service.ServiceType.HEALTH_BOOST, service.getType());
        assertEquals(10, service.getEffectValue());
    }
    
    @Test
    @DisplayName("Проверка услуги с повышением перемещения")
    void testMovementBoostService() {
        Service service = new Service("Movement Boost", "Increases movement", 25, 3, Service.ServiceType.MOVEMENT_BOOST);
        
        assertEquals(Service.ServiceType.MOVEMENT_BOOST, service.getType());
        assertEquals(3, service.getEffectValue());
    }
    
    @Test
    @DisplayName("Проверка работы с отрицательной продолжительностью")
    void testServiceWithNegativeDuration() {
        // Создаем службу с отрицательной продолжительностью
        int negativeDuration = -10;
        Service service = new Service("Negative Duration", "Invalid", negativeDuration, 5, Service.ServiceType.HEALTH_BOOST);
        
        // Продолжительность должна быть такой, какую передали
        assertEquals(negativeDuration, service.getDuration());
        
        // Примечание: в реальном приложении следует добавить проверку 
        // в конструктор Service для предотвращения отрицательной продолжительности
    }
    
    @Test
    @DisplayName("Проверка типов услуг")
    void testServiceTypes() {
        // Проверяем все доступные типы услуг
        assertNotNull(Service.ServiceType.HEALTH_BOOST);
        assertNotNull(Service.ServiceType.MOVEMENT_BOOST);
        assertNotNull(Service.ServiceType.INVASION_BONUS);
        assertNotNull(Service.ServiceType.ATTACK_BOOST);
        assertNotNull(Service.ServiceType.NONE);
        
        // Проверяем, что разные типы услуг не равны друг другу
        assertNotEquals(Service.ServiceType.HEALTH_BOOST, Service.ServiceType.MOVEMENT_BOOST);
    }
} 