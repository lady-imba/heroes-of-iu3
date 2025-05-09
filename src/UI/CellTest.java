package UI;

import Player.Player;
import Hero.Hero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CellTest {
    private Cell roadCell;
    private Cell emptyCell;
    private Cell barrierCell;
    private Cell oasisCell;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        // Инициализация клеток разных типов
        roadCell = new Cell(Cell.road, 0, 0);
        emptyCell = new Cell(Cell.empty, 1, 1);
        barrierCell = new Cell(Cell.barrier, 2, 2);
        oasisCell = new Cell(Cell.oasis, 3, 3);

        // Создание тестовых игроков с разными символами
        Hero hero1 = new Hero(0, 0, 5, "Hero1");
        Hero hero2 = new Hero(0, 0, 5, "Hero2");
        player1 = new Player(hero1, "P1", false, "Player1");
        player2 = new Player(hero2, "P2", true, "Player2");
    }

    @Test
    void constructor_ShouldInitializeCorrectly() {
        assertEquals(Cell.road, roadCell.getType());
        assertEquals(0, roadCell.getX());
        assertEquals(0, roadCell.getY());
        assertNull(roadCell.getPlayer());
        assertEquals(Cell.road, roadCell.getSymbol()); // Ожидаем тип клетки, а не null
    }

    @Test
    void getSymbol_ShouldReturnType_WhenSymbolNotSet() {
        assertEquals(Cell.road, roadCell.getSymbol());
        assertEquals(Cell.empty, emptyCell.getSymbol());
    }

    @Test
    void getSymbol_ShouldReturnCustomSymbol_WhenSet() {
        roadCell.setSymbol("@");
        assertEquals("@", roadCell.getSymbol());
    }

    @Test
    void getSymbol_ShouldReturnPlayerSymbol_WhenPlayerSet() {
        oasisCell.setPlayer(player1);
        assertEquals("P1", oasisCell.getSymbol());
    }

    @Test
    void setType_ShouldChangeCellType() {
        emptyCell.setType(Cell.oasis);
        assertEquals(Cell.oasis, emptyCell.getType());
        assertEquals(Cell.oasis, emptyCell.getSymbol());
    }

    @Test
    void getMovementCost_ForDifferentCellTypes() {
        assertEquals(2, roadCell.getMovementCost(player1));
        assertEquals(4, emptyCell.getMovementCost(player1));
        assertEquals(Integer.MAX_VALUE, barrierCell.getMovementCost(player1));
        assertEquals(1, oasisCell.getMovementCost(player1));
    }

    @Test
    void getMovementCost_ForPlayerTerritory() {
        // Своя территория
        oasisCell.setPlayer(player1);
        assertEquals(1, oasisCell.getMovementCost(player1));

        // Чужая территория
        assertEquals(4, oasisCell.getMovementCost(player2));
    }

    @Test
    void setPlayer_ShouldChangeSymbol() {
        assertNull(roadCell.getPlayer());
        roadCell.setPlayer(player1);
        assertEquals(player1, roadCell.getPlayer());
        assertEquals("P1", roadCell.getSymbol());
    }

    @Test
    void playerPriority_OverCustomSymbol() {
        roadCell.setSymbol("@");
        assertEquals("@", roadCell.getSymbol());

        roadCell.setPlayer(player1);
        assertEquals("P1", roadCell.getSymbol());

        roadCell.setPlayer(null);
        assertEquals("@", roadCell.getSymbol());
    }

    @Test
    void coordinateSystem_ShouldWorkCorrectly() {
        assertEquals(0, roadCell.getX());
        assertEquals(0, roadCell.getY());

        assertEquals(3, oasisCell.getX());
        assertEquals(3, oasisCell.getY());
    }

    @Test
    void constants_ShouldHaveCorrectValues() {
        assertEquals("#", Cell.barrier);
        assertEquals("=", Cell.road);
        assertEquals(".", Cell.empty);
        assertEquals("О", Cell.oasis);
    }
}