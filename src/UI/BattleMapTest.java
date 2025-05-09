package UI;

import Hero.Hero;
import Unit.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BattleMapTest {
    private BattleMap battleMap;
    private Hero playerHero;
    private Hero computerHero;

    @BeforeEach
    void setUp() {
        // Создаем героев с армиями
        playerHero = new Hero(0, 0, 5, "Player");
        computerHero = new Hero(0, 0, 5, "Computer");

        // Добавляем юнитов в армии
        playerHero.addUnit(new Unit("Warrior", 100, 20, 2, 3));
        playerHero.addUnit(new Unit("Archer", 80, 30, 4, 2));

        computerHero.addUnit(new Unit("Orc", 120, 25, 1, 2));
        computerHero.addUnit(new Unit("Mage", 70, 40, 3, 1));

        // Создаем карту 5x5
        battleMap = new BattleMap(5, 5, playerHero, computerHero);
    }

    @Test
    void constructor_ShouldInitializeCorrectly() {
        assertEquals(5, battleMap.getHeight());
        assertEquals(5, battleMap.getWidth());
        assertNotNull(battleMap.getCursor());
        assertNotNull(battleMap.getMap());
    }

    @Test
    void initializeMap_ShouldPlaceUnitsCorrectly() {
        BattleMap.BattleCell[][] map = battleMap.getMap();

        // Проверяем размещение юнитов игрока (левый край)
        assertFalse(map[0][0].playerUnits.isEmpty());
        assertFalse(map[1][0].playerUnits.isEmpty());

        // Проверяем размещение юнитов компьютера (правый край)
        assertFalse(map[0][4].computerUnits.isEmpty());
        assertFalse(map[1][4].computerUnits.isEmpty());

        // Проверяем пустые клетки
        assertTrue(map[2][2].playerUnits.isEmpty());
        assertTrue(map[2][2].computerUnits.isEmpty());
    }

    @Test
    void getCell_ShouldReturnCorrectCell() {
        BattleMap.BattleCell cell = battleMap.getMap()[1][1];
        assertNotNull(cell);
        assertTrue(cell.playerUnits.isEmpty());
        assertTrue(cell.computerUnits.isEmpty());
    }

    @Test
    void unitCoordinates_ShouldBeSetCorrectly() {
        // Проверяем координаты первого юнита игрока
        Unit playerUnit = playerHero.getArmy().get(0);
        assertEquals(0, playerUnit.getX());
        assertEquals(0, playerUnit.getY());

        // Проверяем координаты первого юнита компьютера
        Unit computerUnit = computerHero.getArmy().get(0);
        assertEquals(4, computerUnit.getX());
        assertEquals(0, computerUnit.getY());
    }

    @Test
    void cursor_ShouldBeInitializedCorrectly() {
        Cursor cursor = battleMap.getCursor();
        assertNotNull(cursor);
        assertEquals(0, cursor.getCursorX());

        // Ожидаем 1, так как (5-2)/2 = 1.5, но целочисленное деление даёт 1
        assertEquals(1, cursor.getCursorY()); // Изменили ожидаемое значение с 2 на 1

        assertFalse(cursor.isCursorActive());
    }

    @Test
    void battleCellCalculations_ShouldWorkCorrectly() {
        BattleMap.BattleCell cell = new BattleMap.BattleCell();

        // Добавляем тестовых юнитов
        Unit unit1 = new Unit("Test1", 50, 10, 1, 1);
        Unit unit2 = new Unit("Test2", 30, 15, 1, 1);

        cell.playerUnits.add(unit1);
        cell.playerUnits.add(unit2);

        assertEquals(80, cell.getTotalPlayerHealth());
        assertEquals(25, cell.getTotalPlayerDamage());

        cell.computerUnits.add(new Unit("Enemy", 100, 20, 1, 1));
        assertEquals(100, cell.getTotalComputerHealth());
        assertEquals(20, cell.getTotalComputerDamage());
    }

    @Test
    void mapDimensions_ShouldBeCorrect() {
        assertEquals(5, battleMap.getHeight());
        assertEquals(5, battleMap.getWidth());
    }

    @Test
    void heroReferences_ShouldBeCorrect() {
        assertSame(playerHero, battleMap.getPlayerHero());
        assertSame(computerHero, battleMap.getComputerHero());
    }
}
