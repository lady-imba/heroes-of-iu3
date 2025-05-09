package Records;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ScoreCalculatorTest {
    
    @Test
    void testScoreCalculatorCreation() {
        ScoreCalculator calculator = new ScoreCalculator(100);
        assertEquals(0, calculator.getUnitsDestroyed());
        assertEquals(0, calculator.getResourcesCollected());
        assertEquals(0, calculator.getBuildingsConstructed());
        assertTrue(calculator.getGameTimeSeconds() >= 0);
    }
    
    @Test
    void testAddUnitsDestroyed() {
        ScoreCalculator calculator = new ScoreCalculator(100);
        calculator.addUnitsDestroyed(5);
        assertEquals(5, calculator.getUnitsDestroyed());
        
        calculator.addUnitsDestroyed(3);
        assertEquals(8, calculator.getUnitsDestroyed());
    }
    
    @Test
    void testAddResourcesCollected() {
        ScoreCalculator calculator = new ScoreCalculator(100);
        calculator.addResourcesCollected(100);
        assertEquals(100, calculator.getResourcesCollected());
        
        calculator.addResourcesCollected(50);
        assertEquals(150, calculator.getResourcesCollected());
    }
    
    @Test
    void testAddBuildingsConstructed() {
        ScoreCalculator calculator = new ScoreCalculator(100);
        calculator.addBuildingsConstructed(2);
        assertEquals(2, calculator.getBuildingsConstructed());
        
        calculator.addBuildingsConstructed(1);
        assertEquals(3, calculator.getBuildingsConstructed());
    }
    
    @Test
    void testCalculateFinalScore() {
        ScoreCalculator calculator = new ScoreCalculator(100);
        
        calculator.addUnitsDestroyed(5);
        calculator.addResourcesCollected(100);
        calculator.addBuildingsConstructed(2);
        
        int score = calculator.calculateFinalScore();
        
        assertTrue(score > 0);
        
        ScoreCalculator calculator2 = new ScoreCalculator(100);
        int score2 = calculator2.calculateFinalScore();
        assertTrue(score > score2);
    }
    
    @Test
    void testMapSizeBonus() {
        ScoreCalculator calculator1 = new ScoreCalculator(100);
        ScoreCalculator calculator2 = new ScoreCalculator(400);
        
        calculator1.addUnitsDestroyed(5);
        calculator1.addResourcesCollected(100);
        calculator1.addBuildingsConstructed(2);
        
        calculator2.addUnitsDestroyed(5);
        calculator2.addResourcesCollected(100);
        calculator2.addBuildingsConstructed(2);
        
        int score1 = calculator1.calculateFinalScore();
        int score2 = calculator2.calculateFinalScore();
        
        assertTrue(score2 > score1);
    }
} 