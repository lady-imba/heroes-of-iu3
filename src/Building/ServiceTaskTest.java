package Building;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceTaskTest {
    private ServiceTask serviceTask;
    private Thread testThread;
    
    @BeforeEach
    void setUp() {
        // Создаем тестовый поток
        testThread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Создаем сервисную задачу с этим потоком
        serviceTask = new ServiceTask(testThread);
    }
    
    @Test
    @DisplayName("Проверка инициализации ServiceTask")
    void testServiceTaskInitialization() {
        assertNotNull(serviceTask);
        assertSame(testThread, serviceTask.getServiceThread());
        assertFalse(serviceTask.isDone());
    }
    
    @Test
    @DisplayName("Проверка выполнения задачи")
    void testTaskCompletion() {
        // Изначально задача не выполнена
        assertFalse(serviceTask.isDone());
        
        // Отмечаем задачу как выполненную
        serviceTask.complete();
        
        // Проверяем, что задача теперь выполнена
        assertTrue(serviceTask.isDone());
    }
    
    @Test
    @DisplayName("Проверка установки и получения потока")
    void testSetServiceThread() {
        // Создаем новый поток
        Thread newThread = new Thread(() -> {});
        
        // Устанавливаем новый поток
        serviceTask.setServiceThread(newThread);
        
        // Проверяем, что поток был установлен
        assertSame(newThread, serviceTask.getServiceThread());
    }
    
    @Test
    @DisplayName("Проверка работы колбека при завершении")
    void testOnCompleteCallback() {
        // Создаем флаг для отслеживания вызова колбека
        boolean[] callbackCalled = {false};
        
        // Устанавливаем колбек
        serviceTask.setOnCompleteCallback(() -> callbackCalled[0] = true);
        
        // Проверяем, что колбек еще не вызван
        assertFalse(callbackCalled[0]);
        
        // Завершаем задачу
        serviceTask.complete();
        
        // Проверяем, что колбек был вызван
        assertTrue(callbackCalled[0]);
    }
    
    @Test
    @DisplayName("Проверка немедленного вызова колбека при уже завершенной задаче")
    void testImmediateCallbackIfDone() {
        // Сначала завершаем задачу
        serviceTask.complete();
        
        // Создаем флаг для отслеживания вызова колбека
        boolean[] callbackCalled = {false};
        
        // Устанавливаем колбек после завершения задачи
        serviceTask.setOnCompleteCallback(() -> callbackCalled[0] = true);
        
        // Проверяем, что колбек был вызван немедленно
        assertTrue(callbackCalled[0]);
    }
} 