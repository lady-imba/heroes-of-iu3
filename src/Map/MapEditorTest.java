package Map;

import Castle.Castle;
import Oasis.Oasis;
import Player.Player;
import Hero.Hero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MapEditorTest {
    private MapEditor mapEditor;
    private Player player;
    private Hero hero;
    private String testCreator = "TestCreator";
    private String testMapName = "TestMap";
    private Path mapsDir;
    
    @BeforeEach
    void setUp() throws IOException {
        String playerName = "TestPlayer";
        mapEditor = new MapEditor(playerName);
        hero = new Hero(0, 0, 5, "TestHero");
        player = new Player(hero, "&", false, playerName);
        
        mapsDir = Path.of("maps");
        if (!Files.exists(mapsDir)) {
            Files.createDirectories(mapsDir);
        }
    }
    
    @Test
    void testCreateNewMap() throws IOException {
        Map map = mapEditor.createNewMap(testMapName, testCreator, 10, 10);
        
        assertNotNull(map);
        assertEquals(testMapName, map.getName());
        assertEquals(testCreator, map.getCreator());
        assertEquals(10, map.getWidth());
        assertEquals(10, map.getHeight());
        
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                assertEquals(Map.TileType.EMPTY, map.getTileAt(x, y));
            }
        }
    }
    
    @Test
    void testEditTile() throws IOException {
        Map map = mapEditor.createNewMap(testMapName, testCreator, 10, 10);
        
        boolean result = mapEditor.editTile(map, 1, 1, Map.TileType.ROAD);
        
        assertTrue(result);
        assertEquals(Map.TileType.ROAD, map.getTileAt(1, 1));
        
        result = mapEditor.editTile(map, -1, -1, Map.TileType.ROAD);
        assertFalse(result);
        
        result = mapEditor.editTile(map, 10, 10, Map.TileType.ROAD);
        assertFalse(result);
    }
    
    @Test
    void testPlaceCastle() throws IOException {
        Map map = mapEditor.createNewMap(testMapName, testCreator, 10, 10);
        
        Castle castle = new Castle(player, 1, 1);
        boolean result = mapEditor.placeCastle(map, castle, 0);
        
        assertTrue(result);
        Castle[] castles = map.getCastles();
        assertNotNull(castles[0]);
        assertEquals(1, castles[0].getX());
        assertEquals(1, castles[0].getY());
        
        Castle invalidCastle = new Castle(player, -1, -1);
        result = mapEditor.placeCastle(map, invalidCastle, 1);
        assertFalse(result);
    }
    
    @Test
    void testAddOasis() throws IOException {
        Map map = mapEditor.createNewMap(testMapName, testCreator, 10, 10);
        
        Oasis oasis = new Oasis(1, 1);
        boolean result = mapEditor.addOasis(map, oasis);
        
        assertTrue(result);
        List<Oasis> oases = map.getOases();
        assertEquals(1, oases.size());
        assertEquals(1, oases.get(0).getOasisX());
        assertEquals(1, oases.get(0).getOasisY());
        
        Oasis invalidOasis = new Oasis(-1, -1);
        result = mapEditor.addOasis(map, invalidOasis);
        assertFalse(result);
    }
    
    @Test
    void testRemoveOasis() throws IOException {
        Map map = mapEditor.createNewMap(testMapName, testCreator, 10, 10);
        
        Oasis oasis = new Oasis(1, 1);
        mapEditor.addOasis(map, oasis);
        
        boolean result = mapEditor.removeOasis(map, 1, 1);
        
        assertTrue(result);
        List<Oasis> oases = map.getOases();
        assertTrue(oases.isEmpty());
        
        result = mapEditor.removeOasis(map, 2, 2);
        assertFalse(result);
    }
    
    @Test
    void testSaveMap() throws IOException {
        Map map = mapEditor.createNewMap(testMapName, testCreator, 10, 10);
        
        Castle castle1 = new Castle(player, 1, 1);
        Castle castle2 = new Castle(player, 2, 2);
        mapEditor.placeCastle(map, castle1, 0);
        mapEditor.placeCastle(map, castle2, 1);
        
        boolean result = mapEditor.saveMap(map);
        
        assertTrue(result);
        
        File textMapFile = new File(mapsDir.resolve(testCreator).resolve(testMapName + ".txt").toString());
        assertTrue(textMapFile.exists());
    }
    
    @Test
    void testLoadMap() throws IOException {
        Map originalMap = mapEditor.createNewMap(testMapName, testCreator, 10, 10);
        
        Castle castle1 = new Castle(player, 1, 1);
        Castle castle2 = new Castle(player, 2, 2);
        mapEditor.placeCastle(originalMap, castle1, 0);
        mapEditor.placeCastle(originalMap, castle2, 1);
        
        mapEditor.saveMap(originalMap);
        
        Map loadedMap = mapEditor.loadMap(testMapName, testCreator);
        
        assertNotNull(loadedMap);
        assertEquals(originalMap.getName(), loadedMap.getName());
        assertEquals(originalMap.getCreator(), loadedMap.getCreator());
        assertEquals(originalMap.getWidth(), loadedMap.getWidth());
        assertEquals(originalMap.getHeight(), loadedMap.getHeight());
    }
    
    @Test
    void testDeleteMap() throws IOException {
        Map map = mapEditor.createNewMap(testMapName, testCreator, 10, 10);
        
        Castle castle1 = new Castle(player, 1, 1);
        Castle castle2 = new Castle(player, 2, 2);
        mapEditor.placeCastle(map, castle1, 0);
        mapEditor.placeCastle(map, castle2, 1);
        
        mapEditor.saveMap(map);
        
        boolean result = mapEditor.deleteMap(testMapName, testCreator);
        
        assertTrue(result);
        
        File textMapFile = new File(mapsDir.resolve(testCreator).resolve(testMapName + ".txt").toString());
        assertFalse(textMapFile.exists());
    }
} 