package Records;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameRecord implements Serializable, Comparable<GameRecord> {
    private static final long serialVersionUID = 1L;
    
    private String playerName;
    private int score;
    private String mapName;
    private String mapCreator;
    private LocalDateTime dateTime;
    
    public GameRecord(String playerName, int score, String mapName, String mapCreator) {
        this.playerName = playerName;
        this.score = score;
        this.mapName = mapName;
        this.mapCreator = mapCreator;
        this.dateTime = LocalDateTime.now();
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public int getScore() {
        return score;
    }
    
    public String getMapName() {
        return mapName;
    }
    
    public String getMapCreator() {
        return mapCreator;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
    
    @Override
    public int compareTo(GameRecord other) {
        return Integer.compare(other.score, this.score);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %d очков за матч '%s' от %s (%s)", 
                playerName, score, mapName, mapCreator, getFormattedDateTime());
    }
} 