package Castle;

import Player.Player;
import Hero.Hero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CastleTest {
    private Castle castle;
    private Player player;
    private Player enemy;
    private Hero hero;
    private boolean isInCastle;

    @BeforeEach
    void setUp() {
        hero = new Hero(0, 0, 5, "TestHero");
        player = new Player(hero, "P", false, "Player");
        enemy = new Player(new Hero(0, 0, 5, "EnemyHero"), "E", true, "Enemy");
        castle = new Castle(player, 10, 10);
        player.setCastle(castle);
        this.isInCastle = false;
    }

    @Test
    void testCastleInitialization() {
        assertEquals(10, castle.getX());
        assertEquals(10, castle.getY());
        assertEquals(player.getPlayerId(), castle.getOwnerId());
        assertFalse(castle.isInCastle());
        assertEquals(3, castle.getAvailableBuildings().size());
    }

    @Test
    void testEnterCastleByOwner() {
        assertFalse(castle.isInCastle(), "Изначально isInCastle должен быть false");
        castle.enterCastleTest(player);
        assertTrue(castle.isInCastle(), "После входа владельца isInCastle должен быть true");
    }

    @Test
    void testEnterCastleByEnemy() {
        castle.enterCastle(enemy);
        assertNotNull(castle.getInvader());
        assertEquals(enemy, castle.getInvader());
    }

    @Test
    void testPurchaseBuildingDirect() {
        // Подготовка
        player.AddGold(500);
        int initialAvailable = castle.getAvailableBuildings().size();

        // Вызываем метод напрямую
        castle.purchaseBuildingTest(1); // Покупаем первую доступную постройку

        // Проверки
        assertEquals(1, castle.getBuildings().size());
        assertEquals(850, player.getGold());
        assertEquals(initialAvailable - 1, castle.getAvailableBuildings().size());
    }
}