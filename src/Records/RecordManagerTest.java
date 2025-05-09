package Records;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecordManagerTest {
    
    @TempDir
    Path tempDir;
    
    private RecordManager recordManager;
    private String testRecordsDir;
    private String testRecordsFile;
    
    @BeforeEach
    void setUp() {
        testRecordsDir = tempDir.resolve("test_records").toString();
        testRecordsFile = "records.txt";
        recordManager = new RecordManager(testRecordsDir, testRecordsFile);
    }
    
    @Test
    void testAddRecord() {
        GameRecord record1 = new GameRecord("Player1", 1000, "Map1", "Creator1");
        GameRecord record2 = new GameRecord("Player2", 2000, "Map2", "Creator2");
        GameRecord record3 = new GameRecord("Player3", 500, "Map3", "Creator3");
        
        assertTrue(recordManager.addRecord(record1));
        assertTrue(recordManager.addRecord(record2));
        assertTrue(recordManager.addRecord(record3));
        
        List<GameRecord> topRecords = recordManager.getTopRecords(10);
        assertEquals(3, topRecords.size());
        
        assertEquals("Player2", topRecords.get(0).getPlayerName());
        assertEquals("Player1", topRecords.get(1).getPlayerName());
        assertEquals("Player3", topRecords.get(2).getPlayerName());
    }
    
    @Test
    void testUpdatePlayerRecord() {
        GameRecord record1 = new GameRecord("Player1", 1000, "Map1", "Creator1");
        GameRecord record2 = new GameRecord("Player1", 2000, "Map2", "Creator2"); // Тот же игрок, но больше очков
        
        assertTrue(recordManager.addRecord(record1));
        
        assertTrue(recordManager.addRecord(record2));
        
        List<GameRecord> topRecords = recordManager.getTopRecords(10);
        assertEquals(1, topRecords.size());
        assertEquals("Player1", topRecords.get(0).getPlayerName());
        assertEquals(2000, topRecords.get(0).getScore());
    }
    
    @Test
    void testGetTopRecords() {
        GameRecord record1 = new GameRecord("Player1", 1000, "Map1", "Creator1");
        GameRecord record2 = new GameRecord("Player2", 2000, "Map2", "Creator2");
        GameRecord record3 = new GameRecord("Player3", 500, "Map3", "Creator3");
        GameRecord record4 = new GameRecord("Player4", 1500, "Map4", "Creator4");
        GameRecord record5 = new GameRecord("Player5", 3000, "Map5", "Creator5");
        GameRecord record6 = new GameRecord("Player6", 2500, "Map6", "Creator6");
        
        recordManager.addRecord(record1);
        recordManager.addRecord(record2);
        recordManager.addRecord(record3);
        recordManager.addRecord(record4);
        recordManager.addRecord(record5);
        recordManager.addRecord(record6);
        
        List<GameRecord> top3Records = recordManager.getTopRecords(3);
        assertEquals(3, top3Records.size());
        assertEquals("Player5", top3Records.get(0).getPlayerName());
        assertEquals("Player6", top3Records.get(1).getPlayerName());
        assertEquals("Player2", top3Records.get(2).getPlayerName());
    }
    
    @Test
    void testGetPlayerRecord() {
        GameRecord record = new GameRecord("TestPlayer", 1000, "TestMap", "TestCreator");
        recordManager.addRecord(record);
        
        GameRecord playerRecord = recordManager.getPlayerRecord("TestPlayer");
        assertNotNull(playerRecord);
        assertEquals("TestPlayer", playerRecord.getPlayerName());
        assertEquals(1000, playerRecord.getScore());
        
        GameRecord nonExistentRecord = recordManager.getPlayerRecord("NonExistentPlayer");
        assertNull(nonExistentRecord);
    }
    
    @Test
    void testSaveAndLoadRecords() {
        GameRecord record1 = new GameRecord("Player1", 1000, "Map1", "Creator1");
        GameRecord record2 = new GameRecord("Player2", 2000, "Map2", "Creator2");
        
        recordManager.addRecord(record1);
        recordManager.addRecord(record2);
        
        RecordManager newRecordManager = new RecordManager(testRecordsDir, testRecordsFile);
        
        List<GameRecord> loadedRecords = newRecordManager.getTopRecords(10);
        assertEquals(2, loadedRecords.size());
        
        assertEquals("Player2", loadedRecords.get(0).getPlayerName());
        assertEquals(2000, loadedRecords.get(0).getScore());
        assertEquals("Player1", loadedRecords.get(1).getPlayerName());
        assertEquals(1000, loadedRecords.get(1).getScore());
    }
} 