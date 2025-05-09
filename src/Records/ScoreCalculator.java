package Records;

import java.time.Duration;
import java.time.Instant;

public class ScoreCalculator {
    private Instant gameStartTime;
    private int unitsDestroyed;
    private int resourcesCollected;
    private int buildingsConstructed;
    private int mapSize;
    
    public ScoreCalculator(int mapSize) {
        this.gameStartTime = Instant.now();
        this.unitsDestroyed = 0;
        this.resourcesCollected = 0;
        this.buildingsConstructed = 0;
        this.mapSize = mapSize;
    }
    
    public void addUnitsDestroyed(int count) {
        this.unitsDestroyed += count;
    }
    
    public void addResourcesCollected(int amount) {
        this.resourcesCollected += amount;
    }
    
    public void addBuildingsConstructed(int count) {
        this.buildingsConstructed += count;
    }
    
    public int calculateFinalScore() {
        long gameTimeSeconds = Duration.between(gameStartTime, Instant.now()).getSeconds();
        
        int timeScore = Math.max(0, 10000 - (int)(gameTimeSeconds / 10));
        
        int unitsScore = unitsDestroyed * 100;
        
        int resourcesScore = resourcesCollected * 5;
        
        int buildingsScore = buildingsConstructed * 200;
        
        int mapSizeBonus = mapSize / 100;
        
        return timeScore + unitsScore + resourcesScore + buildingsScore + mapSizeBonus;
    }
    
    public long getGameTimeSeconds() {
        return Duration.between(gameStartTime, Instant.now()).getSeconds();
    }
    
    public int getUnitsDestroyed() {
        return unitsDestroyed;
    }
    
    public int getResourcesCollected() {
        return resourcesCollected;
    }
    
    public int getBuildingsConstructed() {
        return buildingsConstructed;
    }
} 