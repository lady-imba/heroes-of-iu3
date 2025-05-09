package Unit;

import UI.BattleMap;
import Hero.Hero;
import org.junit.jupiter.api.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {
    // Кастомный уровень ERROR для соответствия заданию
    private static final Level ERROR = new Level("ERROR", Level.SEVERE.intValue() + 100) {};

    private static final Logger logger = Logger.getLogger(UnitTest.class.getName());

    private Unit unit;
    private Hero playerHero;
    private Hero computerHero;
    private BattleMap battleMap;
    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;

    static {
        try {
            // Настройка логирования в файл
            LogManager.getLogManager().reset();
            FileHandler fileHandler = new FileHandler("test_logs.log");
            fileHandler.setFormatter(new SimpleFormatter() {
                private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format(format,
                            new java.util.Date(lr.getMillis()),
                            lr.getLevel().getName(),
                            lr.getMessage()
                    );
                }
            });
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Ошибка настройки логгера: " + e);
        }
    }

    @BeforeEach
    public void setUp() {
        // Перехватываем System.out перед каждым тестом
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        logger.warning("Перехвачен поток вывода System.out");

        // Инициализация тестовых объектов
        logger.info("Подготовка тестового окружения");
        unit = new Unit("Archer", 100, 20, 3, 2);
        playerHero = new Hero(0, 0, 5, "Player");
        computerHero = new Hero(10, 10, 5, "Computer");
        battleMap = new BattleMap(10, 10, playerHero, computerHero);
        logger.info("Тестовое окружение готово");
    }

    @AfterEach
    public void tearDown() {
        // Восстанавливаем оригинальный поток
        System.setOut(originalOut);
        logger.warning("Восстановлен оригинальный поток вывода");
    }

    //throws Exception говорит о том что метод может выбросить исключение
    //использую try catch чтобы залогировать ошибку
    private void callApplyDamage(Unit unit, List<Unit> units, int totalDamage, Hero armyOwner) throws Exception {
        try {
            Method method = Unit.class.getDeclaredMethod("applyDamage", List.class, int.class, Hero.class);
            method.setAccessible(true); //временно отключает проверку доступа

            // Логирование согласно заданию
            logger.log(Level.INFO, "Тест прошёл");
            logger.log(ERROR, "Использование приватного метода applyDamage");

            method.invoke(unit, units, totalDamage, armyOwner);
            //unit – объект, у которого вызывается метод.
            //units, totalDamage, armyOwner – аргументы метода.
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка вызова приватного метода: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testApplyDamage_SingleUnit() throws Exception {
        logger.info("Запуск testApplyDamage_SingleUnit");

        Unit enemy = new Unit("Enemy", 50, 10, 2, 2);
        List<Unit> enemies = new ArrayList<>();
        enemies.add(enemy);
        computerHero.addUnit(enemy);

        callApplyDamage(unit, enemies, 20, computerHero);

        assertEquals(30, enemy.getHealth());
        assertTrue(enemies.contains(enemy));
        assertTrue(computerHero.getArmy().contains(enemy));

        logger.info("testApplyDamage_SingleUnit успешно пройден");
    }

    @Test
    public void testUnitInitialization() {
        logger.info("Запуск testUnitInitialization");

        assertEquals("Archer", unit.getType());
        assertEquals(100, unit.getHealth());
        assertEquals(20, unit.getDamage());
        assertEquals(3, unit.getAttackRange());
        assertEquals(2, unit.getMovementRange());
        assertEquals(0, unit.getX());
        assertEquals(0, unit.getY());

        logger.info("testUnitInitialization успешно пройден");
    }

    @Test
    public void testCanAttack_WithinRange() {
        logger.warning("Начало теста с проверкой вывода");

        unit.setX(2);
        unit.setY(2);
        unit.canAttack(3, 2); // Вызываем метод, который печатает в System.out

        // Проверяем перехваченный вывод
        String output = outputStream.toString().trim();
        assertTrue(output.contains("Целевая клетка доступна для атаки."));
        logger.warning("Перехваченный вывод: " + output);

        assertTrue(unit.canAttack(3, 2));
        logger.info("testCanAttack_WithinRange успешно пройден");
    }

    @Test
    public void testCanAttack_OutOfRange() {
        logger.warning("Начало теста с проверкой вывода");

        unit.setX(0);
        unit.setY(0);
        unit.canAttack(4, 0); // Вызываем метод, который печатает в System.out

        // Проверяем перехваченный вывод
        String output = outputStream.toString().trim();
        assertTrue(output.contains("Целевая клетка находится за пределами дальности атаки."));
        logger.warning("Перехваченный вывод: " + output);

        assertFalse(unit.canAttack(4, 0));
        logger.info("testCanAttack_OutOfRange успешно пройден");
    }

    @Test
    public void testCanMoveTo_WithinRange() {
        logger.warning("Начало теста с проверкой вывода");

        unit.setX(1);
        unit.setY(1);
        unit.canMoveTo(2, 2); // Вызываем метод, который печатает в System.out

        // Проверяем перехваченный вывод
        String output = outputStream.toString().trim();
        assertTrue(output.contains("Целевая клетка доступна для перемещения."));
        logger.warning("Перехваченный вывод: " + output);

        assertTrue(unit.canMoveTo(2, 2));
        logger.info("testCanMoveTo_WithinRange успешно пройден");
    }

    @Test
    public void testCanMoveTo_OutOfRange() {
        logger.warning("Начало теста с проверкой вывода");

        unit.setX(0);
        unit.setY(0);
        unit.canMoveTo(0, 3); // Вызываем метод, который печатает в System.out

        // Проверяем перехваченный вывод
        String output = outputStream.toString().trim();
        assertTrue(output.contains("Целевая клетка находится за пределами дальности перемещения."));
        logger.warning("Перехваченный вывод: " + output);

        assertFalse(unit.canMoveTo(0, 3));
        logger.info("testCanMoveTo_OutOfRange успешно пройден");
    }

    @Test
    public void testMoveUnit() {
        logger.info("Запуск testMoveUnit");

        playerHero.addUnit(unit);

        BattleMap.BattleCell fromCell = battleMap.getMap()[unit.getY()][unit.getX()];
        BattleMap.BattleCell toCell = battleMap.getMap()[3][3];

        unit.moveUnit(3, 3, fromCell, toCell, false);

        assertEquals(3, unit.getX());
        assertEquals(3, unit.getY());
        assertFalse(fromCell.playerUnits.contains(unit));
        assertTrue(toCell.playerUnits.contains(unit));

        logger.info("testMoveUnit успешно пройден");
    }

    @Test
    public void testAttackUnit_PlayerAttacksComputer() {
        logger.info("Запуск testAttackUnit_PlayerAttacksComputer");

        Unit enemy = new Unit("Enemy", 50, 10, 2, 2);
        computerHero.addUnit(enemy);

        BattleMap.BattleCell attackerCell = battleMap.getMap()[0][0];
        BattleMap.BattleCell targetCell = battleMap.getMap()[0][9];

        attackerCell.playerUnits.add(unit);
        targetCell.computerUnits.add(enemy);

        unit.attackUnit(attackerCell, targetCell, playerHero, computerHero);

        assertEquals(30, enemy.getHealth());
        assertTrue(targetCell.computerUnits.contains(enemy));
        assertTrue(computerHero.getArmy().contains(enemy));

        logger.info("testAttackUnit_PlayerAttacksComputer успешно пройден");
    }

    @Test
    public void testAttackUnit_KillEnemy() {
        logger.info("Запуск testAttackUnit_KillEnemy");

        Unit weakEnemy = new Unit("WeakEnemy", 15, 5, 1, 1);
        computerHero.addUnit(weakEnemy);

        BattleMap.BattleCell attackerCell = battleMap.getMap()[0][0];
        BattleMap.BattleCell targetCell = battleMap.getMap()[0][9];

        attackerCell.playerUnits.add(unit);
        targetCell.computerUnits.add(weakEnemy);

        unit.attackUnit(attackerCell, targetCell, playerHero, computerHero);

        assertTrue(targetCell.computerUnits.isEmpty());
        assertFalse(computerHero.getArmy().contains(weakEnemy));

        logger.info("testAttackUnit_KillEnemy успешно пройден");
    }

    @Test
    public void testSetHealth() {
        logger.info("Запуск testSetHealth");

        unit.setHealth(75);
        assertEquals(75, unit.getHealth());

        unit.setHealth(0);
        assertEquals(0, unit.getHealth());

        logger.info("testSetHealth успешно пройден");
    }

    @Test
    public void testCoordinates() {
        logger.info("Запуск testCoordinates");

        unit.setX(3);
        unit.setY(4);
        assertEquals(3, unit.getX());
        assertEquals(4, unit.getY());

        logger.info("testCoordinates успешно пройден");
    }
}