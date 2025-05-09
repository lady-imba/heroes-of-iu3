package Records;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameRecordTest {
    
    @Test
    void testGameRecordCreation() {
        GameRecord record = new GameRecord("TestPlayer", 1000, "TestMap", "TestCreator");
        
        assertEquals("TestPlayer", record.getPlayerName());
        assertEquals(1000, record.getScore());
        assertEquals("TestMap", record.getMapName());
        assertEquals("TestCreator", record.getMapCreator());
        assertNotNull(record.getDateTime());
    }
    
    @Test
    void testGameRecordComparison() {
        GameRecord record1 = new GameRecord("Player1", 1000, "Map1", "Creator1");
        GameRecord record2 = new GameRecord("Player2", 2000, "Map2", "Creator2");
        GameRecord record3 = new GameRecord("Player3", 500, "Map3", "Creator3");
        
        assertTrue(record2.compareTo(record1) < 0); 
        assertTrue(record1.compareTo(record3) < 0); 
        assertTrue(record2.compareTo(record3) < 0); 
    }
    
    @Test
    void testGameRecordToString() {
        GameRecord record = new GameRecord("TestPlayer", 1000, "TestMap", "TestCreator");
        String recordString = record.toString();
        
        assertTrue(recordString.contains("TestPlayer"));
        assertTrue(recordString.contains("1000"));
        assertTrue(recordString.contains("TestMap"));
        assertTrue(recordString.contains("TestCreator"));
    }
} 