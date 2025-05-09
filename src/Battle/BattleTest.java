package Battle;

import UI.BattleMap;
import Player.Player;
import Hero.Hero;
import Unit.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;


import static org.junit.jupiter.api.Assertions.*;

class BattleTest {
    private Player player;
    private Player computer;
    private Battle battle;
    private Hero playerHero;
    private Hero computerHero;
    private Unit playerUnit;
    private Unit computerUnit;
    private BattleMap battleMap;

    @BeforeEach
    void setUp() {
        // Создаем тестовых героев
        playerHero = new Hero(0, 0, 5, "PlayerHero");
        computerHero = new Hero(0, 0, 5, "ComputerHero");

        // Создаем тестовых юнитов
        playerUnit = new Unit("Warrior", 100, 20, 1, 2);
        computerUnit = new Unit("Archer", 80, 15, 2, 1);

        // Добавляем юнитов к героям
        playerHero.addUnit(playerUnit);
        computerHero.addUnit(computerUnit);

        // Создаем игроков
        player = new Player(playerHero, "P", false, "Player");
        computer = new Player(computerHero, "E", true, "Computer");

        // Создаем битву
        battle = new Battle(player, computer);
        battleMap = getBattleMapReflection(battle);
    }

    //var - компилятор анализирует выражение справа от = и подставляет соответствующий тип.
    // Вспомогательный метод для доступа к battleMap через reflection
    //Battle.class — получаем метаинформацию о классе Battle.
    //getDeclaredField("battleMap") — находим поле battleMap в этом классе (даже если оно private).
    private BattleMap getBattleMapReflection(Battle battle) {
        try {
            var field = Battle.class.getDeclaredField("battleMap");
            // Устанавливаем флаг доступности поля в true, чтобы можно было работать с приватным полем
            field.setAccessible(true);//по сути временно снимает ограничение доступа
            return (BattleMap) field.get(battle);//возвращает значение поля для тестируемого объекта
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testBattleEndComputerWins() {
        // Очищаем армию игрока (имитируем поражение)
        player.getActiveHero().getArmy().clear();

        // Вызываем обработку поражения напрямую
        battle.removeDefeatedHeroForTest(player);

        // Проверки
        assertFalse(player.getHeroes().contains(playerHero),
                "Пораженный герой должен быть удален из списка");
    }

    @Test
    void testBattleInitialization() {
        assertNotNull(battle);
        assertNotNull(battleMap);
        assertEquals(10, battleMap.getHeight());
        assertEquals(10, battleMap.getWidth());

        // Проверяем начальное расположение юнитов
        assertFalse(battleMap.getMap()[0][0].playerUnits.isEmpty());
        assertFalse(battleMap.getMap()[0][9].computerUnits.isEmpty());
    }

    @Test
    void testComputerTurnAttack() {
        // Устанавливаем позиции для атаки
        playerUnit.setX(5);
        playerUnit.setY(5);
        computerUnit.setX(6);
        computerUnit.setY(5);

        // Обновляем карту
        battleMap.getMap()[5][5].playerUnits.add(playerUnit);
        battleMap.getMap()[5][6].computerUnits.add(computerUnit);

        int initialHealth = playerUnit.getHealth();
        battle.computerTurn();

        assertTrue(playerUnit.getHealth() < initialHealth,
                "Компьютер должен атаковать при возможности");
    }


    @Test
    void testComputerTurnMove() {
        // Устанавливаем позиции вне зоны атаки
        playerUnit.setX(0);
        playerUnit.setY(0);
        computerUnit.setX(9);
        computerUnit.setY(9);

        // Обновляем карту
        battleMap.getMap()[0][0].playerUnits.add(playerUnit);
        battleMap.getMap()[9][9].computerUnits.add(computerUnit);

        int initialX = computerUnit.getX();
        battle.computerTurn();

        assertTrue(computerUnit.getX() < initialX,
                "Компьютер должен двигаться к игроку");
    }

    @Test
    void testBattleEndPlayerWins() {
        // Удаляем юнита компьютера
        computer.getActiveHero().getArmy().clear();

        battle.removeDefeatedHeroForTest(computer);

        assertFalse(computer.getHeroes().contains(computerHero),
                "При поражении все герои компьютера должны быть удалены");
    }


    @Test
    void testRemoveDefeatedHeroWithRemainingHeroes() {
        // Добавляем второго героя
        Hero secondHero = new Hero(0, 0, 5, "SecondHero");
        computer.getHeroes().add(secondHero);

        int initialCount = computer.getHeroes().size();
        battle.removeDefeatedHero(computer);

        assertEquals(initialCount - 1, computer.getHeroes().size(),
                "Должен остаться один герой после поражения");
        assertEquals(secondHero, computer.getActiveHero(),
                "Активным должен стать оставшийся герой");
    }

    @Test
    void testPlayerTurnSkip() {
        // 1. Создаём строку "S\n", которая будет имитировать ввод пользователя
//    "S" - это символ, который "введёт" пользователь
//    "\n" - символ переноса строки (эмулирует нажатие Enter)
        String input = "S\n";
        // 2. Преобразуем строку в поток байтов (InputStream)
//    getBytes() конвертирует строку в массив байтов (по умолчанию в кодировке UTF-8)
//    ByteArrayInputStream создаёт поток ввода из этого массива байтов
        InputStream in = new ByteArrayInputStream(input.getBytes());
        // 3. Подменяем стандартный поток ввода System.in на наш искусственный поток
//    Теперь, когда код будет читать из System.in, он получит нашу строку "S\n"
//    вместо реального ввода с клавиатуры
        System.setIn(in);

        int initialHealth = computerUnit.getHealth();
        battle.playerTurn();

        assertEquals(initialHealth, computerUnit.getHealth(),
                "При пропуске хода здоровье не должно меняться");
    }
}