package Building;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TimeManagerTest {
    private TimeManager timeManager;
    
    @BeforeEach
    void setUp() {
        // Получаем экземпляр TimeManager и сбрасываем его в начальное состояние
        timeManager = TimeManager.getInstance();
        timeManager.reset();
    }
    
    @AfterEach
    void tearDown() {
        // Сбрасываем состояние TimeManager после теста
        timeManager.reset();
    }
    
    @Test
    @DisplayName("Проверка получения экземпляра TimeManager")
    void testGetInstance() {
        TimeManager instance1 = TimeManager.getInstance();
        TimeManager instance2 = TimeManager.getInstance();
        
        // Должен возвращаться один и тот же экземпляр (синглтон)
        assertSame(instance1, instance2);
        assertNotNull(instance1);
    }
    
    @Test
    @DisplayName("Проверка инициализации TimeManager")
    void testInitialState() {
        // Начальное состояние после сброса: день 1, 8:00
        assertEquals(1, timeManager.getTotalDays());
        assertEquals(8, timeManager.getTotalHours());
        assertEquals(0, timeManager.getTotalMinutes());
        
        // Общее количество минут: 1*24*60 + 8*60 + 0 = 1440 + 480 = 1920
        // (день начинается с 1, отсчет идет от начала дня 1)
        assertEquals(1920, timeManager.getTotalGameMinutes());
    }
    
    @Test
    @DisplayName("Проверка продвижения времени")
    void testAdvanceTime() {
        // Запоминаем начальное состояние
        long initialMinutes = timeManager.getTotalGameMinutes();
        
        // Продвигаем время на 65 минут
        timeManager.advanceTime(65);
        
        // Проверяем, что время обновилось корректно
        assertEquals(initialMinutes + 65, timeManager.getTotalGameMinutes());
        assertEquals(9, timeManager.getTotalHours());
        assertEquals(5, timeManager.getTotalMinutes());
    }
    
    @Test
    @DisplayName("Проверка продвижения времени через дни")
    void testAdvanceTimeThroughDays() {
        // Запоминаем начальное состояние
        int initialDays = timeManager.getTotalDays();
        
        // Продвигаем время на 25 часов (1 день и 1 час)
        timeManager.advanceTime(25 * 60);
        
        // Проверяем, что дни и часы обновились корректно
        assertEquals(initialDays + 1, timeManager.getTotalDays());
        assertEquals(9, timeManager.getTotalHours());
        assertEquals(0, timeManager.getTotalMinutes());
    }
    
    @Test
    @DisplayName("Проверка получения времени через N минут")
    void testGetTimeAfterMinutes() {
        // Проверяем время через 30 минут
        String timeAfter30Min = timeManager.getTimeAfterMinutes(30);
        assertTrue(timeAfter30Min.contains("08:30"));
        
        // Проверяем время через 90 минут (переход через час)
        String timeAfter90Min = timeManager.getTimeAfterMinutes(90);
        assertTrue(timeAfter90Min.contains("09:30"));
        
        // Проверяем время через 1440 минут (переход через день)
        String timeAfter1Day = timeManager.getTimeAfterMinutes(24 * 60);
        assertTrue(timeAfter1Day.contains("День 2"));
        assertTrue(timeAfter1Day.contains("08:00"));
    }
} 