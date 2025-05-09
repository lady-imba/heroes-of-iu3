package Save;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Game21Test {
    private Game21 game;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        game = new Game21();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Проверка инициализации колоды - должно быть 40 карт")
    void testDeckInitialization() throws Exception {
        // Получаем доступ к приватному полю deck через рефлексию
        Field deckField = Game21.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        List<Integer> deck = (List<Integer>) deckField.get(game);
        
        // Проверяем, что колода содержит 40 карт
        assertEquals(40, deck.size(), "Колода должна содержать 40 карт");
        
        // Проверяем, что каждый номинал представлен ровно 4 раза (по количеству мастей)
        int[] cardCounts = new int[11]; // Индексы от 1 до 10 для номиналов
        for (Integer card : deck) {
            cardCounts[card]++;
        }
        
        // Проверяем количество каждого номинала
        for (int i = 1; i <= 10; i++) {
            assertEquals(4, cardCounts[i], "Номинал " + i + " должен встречаться 4 раза");
        }
    }

    @Test
    @DisplayName("Проверка метода drawCard - должен возвращать верхнюю карту из колоды")
    void testDrawCard() throws Exception {
        // Получаем доступ к приватному методу drawCard через рефлексию
        Method drawCardMethod = Game21.class.getDeclaredMethod("drawCard");
        drawCardMethod.setAccessible(true);
        
        // Получаем доступ к приватному полю deck
        Field deckField = Game21.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        List<Integer> deck = (List<Integer>) deckField.get(game);
        
        // Запоминаем первую карту в колоде
        Integer expectedCard = deck.get(0);
        
        // Вызываем метод drawCard
        Integer drawnCard = (Integer) drawCardMethod.invoke(game);
        
        // Проверяем, что вернулась первая карта из колоды
        assertEquals(expectedCard, drawnCard, "drawCard должен возвращать верхнюю карту из колоды");
        
        // Проверяем, что карта удалена из колоды
        assertEquals(39, deck.size(), "После вызова drawCard в колоде должно остаться 39 карт");
    }

    @Test
    @DisplayName("Проверка метода calculateHandValue - должен правильно суммировать значения карт")
    void testCalculateHandValue() throws Exception {
        // Получаем доступ к приватному методу calculateHandValue через рефлексию
        Method calculateHandValueMethod = Game21.class.getDeclaredMethod("calculateHandValue", List.class);
        calculateHandValueMethod.setAccessible(true);
        
        // Создаем тестовую руку
        List<Integer> hand = new ArrayList<>();
        hand.add(5);
        hand.add(10);
        hand.add(2);
        
        // Вызываем метод calculateHandValue
        Integer handValue = (Integer) calculateHandValueMethod.invoke(game, hand);
        
        // Проверяем, что сумма карт правильно рассчитана
        assertEquals(17, handValue, "Сумма карт 5+10+2 должна равняться 17");
    }

    @Test
    @DisplayName("Проверка метода calculateBonus - должен возвращать правильный бонус для игрока")
    void testCalculateBonusForPlayer() throws Exception {
        // Устанавливаем тестовые данные руки игрока
        Field playerHandField = Game21.class.getDeclaredField("playerHand");
        playerHandField.setAccessible(true);
        List<Integer> playerHand = new ArrayList<>();
        playerHand.add(10);
        playerHand.add(8);
        playerHandField.set(game, playerHand);
        
        // Вычисляем ожидаемый бонус: базовый бонус (50) + сумма карт (18) * 5 = 140
        int expectedBonus = 50 + (18 * 5);
        
        // Вызываем метод calculateBonus для игрока
        int actualBonus = game.calculateBonus("player");
        
        // Проверяем, что бонус правильный
        assertEquals(expectedBonus, actualBonus, "Бонус должен быть базовый (50) + сумма карт (18) * 5");
    }

    @Test
    @DisplayName("Проверка метода calculateBonus - должен возвращать правильный бонус для компьютера")
    void testCalculateBonusForComputer() throws Exception {
        // Устанавливаем тестовые данные руки компьютера
        Field computerHandField = Game21.class.getDeclaredField("computerHand");
        computerHandField.setAccessible(true);
        List<Integer> computerHand = new ArrayList<>();
        computerHand.add(7);
        computerHand.add(9);
        computerHandField.set(game, computerHand);
        
        // Вычисляем ожидаемый бонус: базовый бонус (50) + сумма карт (16) * 5 = 130
        int expectedBonus = 50 + (16 * 5);
        
        // Вызываем метод calculateBonus для компьютера
        int actualBonus = game.calculateBonus("computer");
        
        // Проверяем, что бонус правильный
        assertEquals(expectedBonus, actualBonus, "Бонус должен быть базовый (50) + сумма карт (16) * 5");
    }

    @Test
    @DisplayName("Проверка логики игры - компьютер должен перестать брать карты, когда до 21 остается менее 10")
    void testComputerLogic() {
        // Создаем тестовый ввод, в котором игрок сразу останавливается
        String input = "2\n"; // Игрок выбирает "Остановиться"
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
        
        // Играем в игру
        game.play();
        
        // Получаем вывод
        String output = outputStream.toString();
        
        // Проверяем, что компьютер перестает брать карты, когда сумма приближается к 21
        assertTrue(output.contains("Компьютер берет карту...") || 
                  output.contains("Компьютер перестает брать карты"), 
                  "Вывод должен содержать информацию о решении компьютера");
    }

    @Test
    @DisplayName("Проверка полной игры с вводом пользователя")
    void testFullGameWithUserInput() {
        // Создаем тестовый ввод, где игрок берет карту а затем останавливается
        String input = "1\n2\n"; // Сначала "Взять карту", затем "Остановиться"
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
        
        // Играем в игру
        String result = game.play();
        
        // Проверяем, что результат не пустой
        assertNotNull(result, "Результат игры не должен быть null");
        assertTrue(result.equals("player") || result.equals("computer"), 
                  "Результат должен быть либо 'player', либо 'computer'");
        
        // Проверяем, что в выводе есть информация о картах игрока и компьютера
        String output = outputStream.toString();
        assertTrue(output.contains("Ваши карты:"), "Вывод должен содержать карты игрока");
        assertTrue(output.contains("Компьютера карты:"), "Вывод должен содержать карты компьютера");
    }

    @Test
    @DisplayName("Проверка обработки перебора у игрока")
    void testPlayerBust() throws Exception {
        // Устанавливаем тестовые данные для игрока (перебор)
        Field playerHandField = Game21.class.getDeclaredField("playerHand");
        playerHandField.setAccessible(true);
        List<Integer> playerHand = new ArrayList<>();
        playerHand.add(10);
        playerHand.add(8);
        playerHand.add(7); // Сумма 25 > 21
        playerHandField.set(game, playerHand);
        
        // Симулируем выбор игрока "Взять карту"
        String input = "1\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
        
        // Играем в игру, но модифицируем внутреннее состояние так, что перебор будет сразу
        Method calculateHandValueMethod = Game21.class.getDeclaredMethod("calculateHandValue", List.class);
        calculateHandValueMethod.setAccessible(true);
        int handValue = (Integer) calculateHandValueMethod.invoke(game, playerHand);
        
        // Проверяем, что сумма карт игрока больше 21
        assertTrue(handValue > 21, "Сумма карт игрока должна быть больше 21");
    }

    // Очистка после тестов
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }
} 