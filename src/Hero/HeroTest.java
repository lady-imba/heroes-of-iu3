package Hero;

import Unit.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class HeroTest {
    private Hero hero;
    private Unit testUnit;
    private final int INITIAL_X = 2;
    private final int INITIAL_Y = 3;
    private final int INITIAL_RANGE = 5;

    @BeforeEach
    void setUp() {
        hero = new Hero(INITIAL_X, INITIAL_Y, INITIAL_RANGE, "TestHero");
        testUnit = new Unit("Warrior", 100, 10, 2, 3);
    }

    @Test
    void testInitialState() {
        assertEquals(INITIAL_X, hero.getX());
        assertEquals(INITIAL_Y, hero.getY());
        assertEquals(INITIAL_RANGE, hero.getMaxMovementRange());
        assertEquals(INITIAL_RANGE, hero.getMovementRange());
        assertEquals("TestHero", hero.getName());
        assertTrue(hero.getArmy().isEmpty());
    }

    @Test
    void testDecreaseMovementRange_NormalCase() {
        hero.decreaseMovementRange(2);
        assertEquals(INITIAL_RANGE - 2, hero.getMovementRange());
    }

    @Test
    void testDecreaseMovementRange_ToZero() {
        hero.decreaseMovementRange(INITIAL_RANGE);
        assertEquals(0, hero.getMovementRange());
    }

    @Test
    void testDecreaseMovementRange_Overflow() {
        hero.decreaseMovementRange(INITIAL_RANGE + 1);
        assertEquals(0, hero.getMovementRange());
    }

    @Test
    void testResetMovementRange() {
        hero.decreaseMovementRange(3);
        hero.resetMovementRange();
        assertEquals(INITIAL_RANGE, hero.getMovementRange());
    }

    @Test
    void testAddUnit() {
        hero.addUnit(testUnit);
        assertEquals(1, hero.getArmy().size());
        // assertSame проверяет идентичность ссылок(надо убедиться что это один и тот же объект в памяти)
        assertSame(testUnit, hero.getArmy().get(0));
    }

    @Test
    void testMove_UpdatesPositionAndRange() {
        int newX = 4;
        int newY = 5;
        int pathCost = 2;

        hero.Move(newX, newY, pathCost);

        assertEquals(newX, hero.getX());
        assertEquals(newY, hero.getY());
        assertEquals(INITIAL_RANGE - pathCost, hero.getMovementRange());
    }

    @Test
    void testIncreaseMaxMovementRange() {
        int increaseAmount = 2;
        int expectedMax = INITIAL_RANGE + increaseAmount;

        hero.increaseMaxMovementRange(increaseAmount);

        assertEquals(expectedMax, hero.getMaxMovementRange());
        assertEquals(expectedMax, hero.getMovementRange());
    }

    @Test
    void testDisplayRestMovementRange_Output() {
        //Создаём временный буфер в памяти для записи вывода вместо консоли
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        //Подменяем стандартный System.out на наш буфер:
        //PrintStream - обёртка для вывода текста
        //Теперь весь вывод будет сохраняться в outContent
        System.setOut(new PrintStream(outContent));

        hero.displayRestMovementRange();

        assertTrue(outContent.toString().contains("Энергия: " + INITIAL_RANGE));
        System.setOut(System.out);
    }

    @Test
    void testArmyManagement() {
        Unit unit1 = new Unit("Archer", 80, 8, 4, 2);
        Unit unit2 = new Unit("Knight", 120, 15, 1, 2);

        hero.addUnit(unit1);
        hero.addUnit(unit2);

        ArrayList<Unit> army = hero.getArmy();
        assertEquals(2, army.size());
        assertTrue(army.contains(unit1));
        assertTrue(army.contains(unit2));
    }

    @Test
    void testMovementAfterRangeIncrease() {
        int increaseAmount = 3;
        hero.decreaseMovementRange(2); // Тратим 2 из 5 → остаётся 3
        hero.increaseMaxMovementRange(increaseAmount); // max = 5+3=8, current = 8
        assertEquals(8, hero.getMovementRange()); // Проверяем полное восстановление
    }
}