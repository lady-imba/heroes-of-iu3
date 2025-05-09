package Map;

import Castle.Castle;
import Oasis.Oasis;
import Player.Player;
import Hero.Hero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MapTest {
    private Map map;
    private Player player;
    private Hero hero;
    private String testCreator = "TestCreator";
    private String testMapName = "TestMap";
    private Path mapsDir;
    
    @BeforeEach
    void setUp() throws IOException {
        String playerName = "TestPlayer";
        hero = new Hero(0, 0, 5, "TestHero");
        player = new Player(hero, "&", false, playerName);
        
        mapsDir = Path.of("maps");
        if (!Files.exists(mapsDir)) {
            Files.createDirectories(mapsDir);
        }
        
        map = new Map(testMapName, testCreator, 10, 10);
    }
    
    @Test
    void testMapInitialization() {
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
        
        Castle[] castles = map.getCastles();
        assertNotNull(castles);
        assertEquals(2, castles.length);
        assertNull(castles[0]);
        assertNull(castles[1]);
        
        List<Oasis> oases = map.getOases();
        assertNotNull(oases);
        assertTrue(oases.isEmpty());
    }
    
    @Test
    void testSetTile() {
        map.setTile(1, 1, Map.TileType.ROAD);
        
        assertEquals(Map.TileType.ROAD, map.getTileAt(1, 1));
        
        map.setTile(-1, -1, Map.TileType.ROAD);
        map.setTile(10, 10, Map.TileType.ROAD);
        
        assertEquals(Map.TileType.EMPTY, map.getTileAt(-1, -1));
        assertEquals(Map.TileType.EMPTY, map.getTileAt(10, 10));
    }
    
    @Test
    void testSetCastle() {
        Castle castle = new Castle(player, 1, 1);
        
        map.setCastle(0, castle);
        
        Castle[] castles = map.getCastles();
        assertNotNull(castles[0]);
        assertEquals(1, castles[0].getX());
        assertEquals(1, castles[0].getY());
        
        Castle invalidCastle = new Castle(player, -1, -1);
        map.setCastle(1, invalidCastle);
        
        assertNull(castles[1]);
    }
    
    @Test
    void testAddOasis() {
        Oasis oasis = new Oasis(1, 1);
        
        map.addOasis(oasis);
        
        List<Oasis> oases = map.getOases();
        assertEquals(1, oases.size());
        assertEquals(1, oases.get(0).getOasisX());
        assertEquals(1, oases.get(0).getOasisY());
        
        Oasis invalidOasis = new Oasis(-1, -1);
        map.addOasis(invalidOasis);
        
        assertEquals(1, oases.size());
    }
    
    @Test
    void testRemoveOasis() {
        Oasis oasis = new Oasis(1, 1);
        
        map.addOasis(oasis);
        
        map.removeOasis(oasis);
        
        List<Oasis> oases = map.getOases();
        assertTrue(oases.isEmpty());
    }
    
    @Test
    void testSaveAndLoadMap() throws IOException {
        map.setTile(5, 5, Map.TileType.ROAD);
        map.setTile(6, 6, Map.TileType.OBSTACLE);
        map.setTile(7, 7, Map.TileType.WATER);
        
        Castle castle1 = new Castle(player, 1, 1);
        Castle castle2 = new Castle(player, 2, 2);
        map.setCastle(0, castle1);
        map.setCastle(1, castle2);
        
        Oasis oasis1 = new Oasis(3, 3);
        Oasis oasis2 = new Oasis(4, 4);
        map.addOasis(oasis1);
        map.addOasis(oasis2);
        
        boolean saveResult = MapStorage.saveMap(map);
        assertTrue(saveResult);
        
        Map loadedMap = MapStorage.loadMap(testMapName, testCreator);
        
        assertNotNull(loadedMap);
        assertEquals(map.getName(), loadedMap.getName());
        assertEquals(map.getWidth(), loadedMap.getWidth());
        assertEquals(map.getHeight(), loadedMap.getHeight());
        
        assertEquals(Map.TileType.ROAD, loadedMap.getTileAt(5, 5));
        assertEquals(Map.TileType.OBSTACLE, loadedMap.getTileAt(6, 6));
        assertEquals(Map.TileType.WATER, loadedMap.getTileAt(7, 7));
        
        Castle[] loadedCastles = loadedMap.getCastles();
        assertNotNull(loadedCastles[0]);
        assertNotNull(loadedCastles[1]);
        assertEquals(1, loadedCastles[0].getX());
        assertEquals(1, loadedCastles[0].getY());
        assertEquals(2, loadedCastles[1].getX());
        assertEquals(2, loadedCastles[1].getY());
        
        List<Oasis> loadedOases = loadedMap.getOases();
        assertEquals(2, loadedOases.size());
        assertEquals(3, loadedOases.get(0).getOasisX());
        assertEquals(3, loadedOases.get(0).getOasisY());
        assertEquals(4, loadedOases.get(1).getOasisX());
        assertEquals(4, loadedOases.get(1).getOasisY());
    }
    
    @Test
    void testGetTileTypeFromSymbol() {
        assertEquals(Map.TileType.EMPTY, MapStorage.getTileTypeFromSymbol('.'));
        assertEquals(Map.TileType.ROAD, MapStorage.getTileTypeFromSymbol('#'));
        assertEquals(Map.TileType.OBSTACLE, MapStorage.getTileTypeFromSymbol('X'));
        assertEquals(Map.TileType.WATER, MapStorage.getTileTypeFromSymbol('~'));
        
        assertEquals(Map.TileType.EMPTY, MapStorage.getTileTypeFromSymbol('?'));
    }
} 