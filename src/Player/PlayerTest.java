package Player;

import Hero.Hero;
import Castle.Castle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;
    private Player computerPlayer;
    private Hero hero;
    private Castle castle;

    @BeforeEach
    void setUp() {
        hero = new Hero(0, 0, 5, "TestHero");
        player = new Player(hero, "P", false, "Player");
        computerPlayer = new Player(new Hero(0, 0, 5, "ComputerHero"), "C", true, "Computer");
        castle = new Castle(player, 10, 10);
    }

    @Test
    void testInitialization() {
        assertEquals("P", player.getTerrainSymbol());
        assertEquals("Player", player.getName());
        assertFalse(player.isAnotherPlayer());
        assertEquals(500, player.getGold());
        assertEquals(1, player.getHeroes().size());
        assertEquals(hero, player.getActiveHero());
        assertNotNull(player.getPlayerId());
    }

    @Test
    void testGoldManagement() {
        // Проверка добавления золота
        player.AddGold(100);
        assertEquals(600, player.getGold());

        // Проверка успешной траты
        assertTrue(player.SpendGold(200));
        assertEquals(400, player.getGold());

        // Проверка неудачной траты (недостаточно золота)
        assertFalse(player.SpendGold(500));
        assertEquals(400, player.getGold()); // Сумма не изменилась
    }

    @Test
    void testHeroManagement() {
        // Проверка добавления героев
        player.buyHeroes(2);
        assertEquals(3, player.getHeroes().size()); // 1 исходный + 2 новых

        // Проверка смены активного героя
        Hero newHero = new Hero(0, 0, 5, "NewHero");
        player.pushHero(newHero);
        player.setActiveHero(newHero);
        assertEquals(newHero, player.getActiveHero());
    }

    @Test
    void testUnitManagement() {
        // Проверка добавления юнитов
        player.addUnits("Копейщик", 5);
        assertEquals(5, player.getUnits().get("Копейщик"));

        // Проверка удаления юнитов
        player.removeUnits("Копейщик", 3);
        assertEquals(2, player.getUnits().get("Копейщик"));

    }

    @Test
    void testCastleInteraction() {
        // Проверка привязки замка
        player.setCastle(castle);
        assertEquals(castle, player.getCastle());

        // Проверка героев в замке
        hero.setX(10);
        hero.setY(10);
        assertEquals(1, player.getHeroesInCastle().size());
        assertEquals(hero, player.getHeroesInCastle().get(0));
    }

    @Test
    void testPlayerIdUniqueness() {
        Player anotherPlayer = new Player(new Hero(0, 0, 5, "Another"), "A", false, "Another");
        assertNotEquals(player.getPlayerId(), anotherPlayer.getPlayerId());
    }

    @Test
    void testHeroesInCastleWhenNotInCastle() {
        player.setCastle(castle);
        hero.setX(5);
        hero.setY(5);
        assertTrue(player.getHeroesInCastle().isEmpty());
    }

    @Test
    void testAddMultipleUnitTypes() {
        player.addUnits("Копейщик", 3);
        player.addUnits("Лучник", 2);

        assertEquals(3, player.getUnits().get("Копейщик"));
        assertEquals(2, player.getUnits().get("Лучник"));
    }

    @Test
    void testRemoveNonExistentUnitType() {
        player.addUnits("Копейщик", 2);
        player.removeUnits("Мечник", 1); // Такого типа нет
        assertEquals(2, player.getUnits().get("Копейщик")); // Не должно измениться
        assertNull(player.getUnits().get("Мечник"));
    }
}