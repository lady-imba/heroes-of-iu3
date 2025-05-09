package UI;

import Castle.Castle;
import Hero.Hero;
import Oasis.Oasis;
import Player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameMapTest {
    private GameMap gameMap;
    private Player player;
    private Player computerPlayer;
    private Castle[] castles;
    private final int MAP_HEIGHT = 10;
    private final int MAP_WIDTH = 10;

    @BeforeEach
    void setUp() {
        // Создаем игроков
        player = new Player(new Hero(0, 0, 5, "PlayerHero"), "P", false, "Player");
        computerPlayer = new Player(new Hero(0, 0, 5, "ComputerHero"), "C", true, "Computer");

        // Создаем замки
        castles = new Castle[]{
                new Castle(player, 2, 2),  // Замок игрока
                new Castle(computerPlayer, 7, 7)  // Замок компьютера
        };

        // Инициализируем карту
        gameMap = new GameMap(MAP_HEIGHT, MAP_WIDTH, castles, player, computerPlayer);
    }

    @Test
    void constructor_ShouldInitializeCorrectly() {
        assertNotNull(gameMap);
        assertEquals(MAP_HEIGHT, gameMap.getHeight());
        assertEquals(MAP_WIDTH, gameMap.getWidth());
        assertNotNull(gameMap.getMap());
        assertNotNull(gameMap.getCursor());
        assertNotNull(gameMap.getOasis());
        assertEquals(2, gameMap.getCastles().length);
    }

//    @Test
//    void initializeMap_ShouldSetCorrectCellTypes() {
//        Cell[][] map = gameMap.getMap();
//        Oasis oasis = gameMap.getOasis();
//
//        // Проверяем специальные клетки
//        assertEquals("И", map[2][2].getType()); // Замок игрока
//        assertEquals("К", map[7][7].getType()); // Замок компьютера
//        assertEquals(Cell.oasis, map[oasis.getOasisY()][oasis.getOasisX()].getType()); // Оазис
//
//        // Проверяем дороги (по диагонали), исключая клетки замков
//        for (int i = 0; i < Math.min(MAP_HEIGHT, MAP_WIDTH); i++) {
//            if (i == 2 || i == 7) continue; // Пропускаем клетки замков
//            assertEquals(Cell.road, map[i][i].getType(),
//                    "Диагональная клетка [" + i + "][" + i + "] должна быть дорогой");
//        }
//
//        // Проверяем территорию игрока (i < 3 && j < 4)
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 4; j++) {
//                if (i == j) continue; // Диагональные клетки уже проверили
//                assertEquals(player.getTerrainSymbol(), map[i][j].getType(),
//                        "Клетка [" + i + "][" + j + "] должна быть территорией игрока");
//            }
//        }
//
//        // Проверяем территорию компьютера (i > height - 4 && j > width - 5)
//        for (int i = MAP_HEIGHT - 3; i < MAP_HEIGHT; i++) {
//            for (int j = MAP_WIDTH - 4; j < MAP_WIDTH; j++) {
//                if (i == j) continue; // Диагональные клетки уже проверили
//                assertEquals(computerPlayer.getTerrainSymbol(), map[i][j].getType(),
//                        "Клетка [" + i + "][" + j + "] должна быть территорией компьютера");
//            }
//        }
//
//        // Проверяем барьеры
//        assertBarrier(3, 0);
//        assertBarrier(3, 1);
//        assertBarrier(3, 2);
//        assertBarrier(3, 4);
//        assertBarrier(0, 4);
//        assertBarrier(1, 4);
//        assertBarrier(2, 4);
//        assertBarrier(3, 4);
//    }

    @Test
    void initializeMap_ShouldSetPlayerCastle() {
        Cell[][] map = gameMap.getMap();
        assertEquals("И", map[2][2].getType(), "Клетка [2][2] должна быть замком игрока");
    }

    @Test
    void initializeMap_ShouldSetComputerCastle() {
        Cell[][] map = gameMap.getMap();
        assertEquals("К", map[7][7].getType(), "Клетка [7][7] должна быть замком компьютера");
    }

    @Test
    void initializeMap_ShouldSetOasis() {
        Cell[][] map = gameMap.getMap();
        Oasis oasis = gameMap.getOasis();
        assertEquals(Cell.oasis, map[oasis.getOasisY()][oasis.getOasisX()].getType(),
                "Клетка с оазисом должна иметь правильный тип");
    }

    @Test
    void initializeMap_ShouldSetDiagonalRoads() {
        Cell[][] map = gameMap.getMap();
        for (int i = 0; i < Math.min(MAP_HEIGHT, MAP_WIDTH); i++) {
            if (i == 2 || i == 7) continue; // Пропускаем клетки замков
            assertEquals(Cell.road, map[i][i].getType(),
                    "Диагональная клетка [" + i + "][" + i + "] должна быть дорогой");
        }
    }

    @Test
    void initializeMap_ShouldSetPlayerTerritory() {
        Cell[][] map = gameMap.getMap();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == j) continue; // Диагональные клетки уже проверили
                assertEquals(player.getTerrainSymbol(), map[i][j].getType(),
                        "Клетка [" + i + "][" + j + "] должна быть территорией игрока");
            }
        }
    }

    @Test
    void initializeMap_ShouldSetComputerTerritory() {
        Cell[][] map = gameMap.getMap();
        for (int i = MAP_HEIGHT - 3; i < MAP_HEIGHT; i++) {
            for (int j = MAP_WIDTH - 4; j < MAP_WIDTH; j++) {
                if (i == j) continue; // Диагональные клетки уже проверили
                assertEquals(computerPlayer.getTerrainSymbol(), map[i][j].getType(),
                        "Клетка [" + i + "][" + j + "] должна быть территорией компьютера");
            }
        }
    }

    @Test
    void initializeMap_ShouldSetBarriers() {
        assertBarrier(3, 0);
        assertBarrier(3, 1);
        assertBarrier(3, 2);
        assertBarrier(3, 4);
        assertBarrier(0, 4);
        assertBarrier(1, 4);
        assertBarrier(2, 4);
        assertBarrier(3, 4);
    }

    private void assertBarrier(int y, int x) {
        assertEquals(Cell.barrier, gameMap.getMap()[y][x].getType(),
                "Клетка [" + y + "][" + x + "] должна быть барьером");
    }

    @Test
    void cursor_ShouldBeInitializedCorrectly() {
        Cursor cursor = gameMap.getCursor();
        assertNotNull(cursor);
        assertEquals(1, cursor.getCursorX());
        assertEquals(1, cursor.getCursorY());
        assertFalse(cursor.isCursorActive());
    }

    @Test
    void getCrossedCastle_ShouldReturnCorrectCastle() {
        Hero hero = new Hero(2, 2, 5, "TestHero");
        Castle castle = gameMap.getCrossedCastle(hero);
        assertNotNull(castle);
        assertEquals(2, castle.getX());
        assertEquals(2, castle.getY());

        // Проверка для несуществующего замка
        hero.setX(5);
        hero.setY(5);
        assertNull(gameMap.getCrossedCastle(hero));
    }

    @Test
    void getReachableCells_ShouldReturnCorrectCells() {
        // Проверяем достижимые клетки с ограничением по стоимости
        ArrayList<GameMap.Node> reachable = gameMap.getReachableCells(0, 0, 4, player);
        assertFalse(reachable.isEmpty());

        // Проверяем, что барьеры не включены в достижимые клетки
        //.stream() преобразует коллекцию в поток для обработки
        assertFalse(reachable.stream()
                .anyMatch(node -> node.x == 0 && node.y == 3));
    }
    //.anyMatch(node -> node.x == 0 && node.y == 3)); Проверяет, есть ли в потоке хотя бы один элемент node, у которого:
    //node.x равен 0 И node.y равен 3

    @Test
    void display_ShouldUpdateMapCorrectly() {
        Hero hero = player.getActiveHero();
        hero.setX(3);
        hero.setY(3);

        ArrayList<Hero> computerHeroes = new ArrayList<>();
        computerHeroes.add(computerPlayer.getActiveHero());

        // Проверяем, что герои отображаются правильно
        gameMap.Display(hero, computerHeroes, player, computerPlayer);

        // Получаем текущее состояние карты
        Cell[][] map = gameMap.getMap();

        assertEquals("Г", map[hero.getY()][hero.getX()].getType());
        assertEquals("0", map[computerHeroes.get(0).getY()][computerHeroes.get(0).getX()].getType());
    }

    @Test
    void oasis_ShouldBeInitializedCorrectly() {
        Oasis oasis = gameMap.getOasis();
        assertNotNull(oasis);
        assertEquals(MAP_WIDTH/2-4, oasis.getOasisX());
        assertEquals(MAP_HEIGHT/2+2, oasis.getOasisY());
        assertFalse(oasis.isOasisUsed());
    }

    @Test
    void updateMapView_ShouldRefreshMapState() {
        // Изменяем символ территории игрока
        player = new Player(new Hero(0, 0, 5, "PlayerHero"), "X", false, "Player");

        gameMap.UpdateMapView(player, computerPlayer);

        // Проверяем, что территория игрока обновилась
        assertEquals("X", gameMap.getMap()[0][1].getType());
    }

    @Test
    void startBattle_ShouldInitializeBattle() {
        // assertDoesNotThrow - проверяет, что выполнение кода не вызывает исключений.
        //Если исключение будет выброшено - тест упадёт.
        assertDoesNotThrow(() -> gameMap.startBattle(player, computerPlayer));
    }
}