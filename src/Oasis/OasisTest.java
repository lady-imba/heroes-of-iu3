package Oasis;

import Hero.Hero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class OasisTest {
    private Oasis oasis;
    private Hero hero;
    private final int OASIS_X = 3;
    private final int OASIS_Y = 4;

    @BeforeEach
    void setUp() {
        oasis = new Oasis(OASIS_X, OASIS_Y);
        hero = new Hero(OASIS_X, OASIS_Y, 2, "TestHero"); // Герой создается на оазисе
    }

    @Test
    void testCanUseOasis_WhenHeroOnOasis_ReturnsTrue() {
        assertTrue(oasis.canUseOasis(hero));
    }

    @Test
    void testCanUseOasis_WhenHeroNotOnOasis_ReturnsFalse() {
        hero = new Hero(1, 1, 2, "DistantHero"); // Герой в других координатах
        assertFalse(oasis.canUseOasis(hero));
    }

    @Test
    void testCanUseOasis_WhenOasisAlreadyUsed_ReturnsFalse() {
        oasis.applyOasisEffect(hero); // Используем оазис первый раз
        assertFalse(oasis.canUseOasis(hero)); // Попытка повторного использования
    }

    @Test
    void testApplyOasisEffect_IncreasesMaxMovementRange() {
        int initialMaxRange = hero.getMaxMovementRange();
        oasis.applyOasisEffect(hero);
        assertEquals(initialMaxRange + 1, hero.getMaxMovementRange());
    }

    @Test
    void testApplyOasisEffect_ResetsCurrentMovementRange() {
        hero.decreaseMovementRange(1); // Тратим часть энергии
        oasis.applyOasisEffect(hero);
        assertEquals(hero.getMaxMovementRange(), hero.getMovementRange());
    }

    @Test
    void testApplyOasisEffect_SetsUsedFlag() {
        oasis.applyOasisEffect(hero);
        assertTrue(oasis.oasisUsed);
    }

    @Test
    void testApplyOasisEffect_PrintsCorrectMessage() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        oasis.applyOasisEffect(hero);
        assertTrue(outContent.toString().contains("TestHero получил +1 к дальности!"));

        System.setOut(System.out);
    }

    @Test
    void testResetOasis_ClearsUsedFlag() {
        oasis.applyOasisEffect(hero);
        oasis.resetOasis();
        assertFalse(oasis.oasisUsed);
    }

    @Test
    void testGetCoordinates() {
        assertEquals(OASIS_X, oasis.getOasisX());
        assertEquals(OASIS_Y, oasis.getOasisY());
    }

    @Test
    void testOasisEffectNotApplied_WhenHeroNotOnOasis() {
        hero = new Hero(1, 1, 2, "DistantHero");
        int initialMaxRange = hero.getMaxMovementRange();
        oasis.applyOasisEffect(hero);
        assertEquals(initialMaxRange, hero.getMaxMovementRange()); // Дальность не изменилась
    }
}