package Map;

import Castle.Castle;
import Oasis.Oasis;
import Player.Player;
import Hero.Hero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MapStorageTest {
    private Player player;
    private Path mapsDir;
    
    @BeforeEach
    void setUp() throws IOException {
        mapsDir = Path.of("maps");
        if (!Files.exists(mapsDir)) {
            Files.createDirectories(mapsDir);
        }
        player = new Player(new Hero(0, 0, 5, "TestHero"), "&", false, "TestPlayer");

    }
    
    @Test
    void testSaveAndLoadMap() throws IOException {
        String testCreator = "TestCreator";
        String testMapName = "TestMap";
        Map map = new Map(testMapName, testCreator, 10, 10);

        Castle castle1 = new Castle(player, 1, 1);
        Castle castle2 = new Castle(player, 2, 2);
        map.setCastle(0, castle1);
        map.setCastle(1, castle2);

        Oasis oasis1 = new Oasis(3, 3);
        Oasis oasis2 = new Oasis(4, 4);
        map.addOasis(oasis1);
        map.addOasis(oasis2);

        map.setTile(5, 5, Map.TileType.ROAD);
        map.setTile(6, 6, Map.TileType.OBSTACLE);
        map.setTile(7, 7, Map.TileType.WATER);
        boolean saveResult = MapStorage.saveMap(map);
        assertTrue(saveResult);
        
        File textMapFile = new File(mapsDir.resolve(testCreator).resolve(testMapName + ".txt").toString());
        assertTrue(textMapFile.exists());
        
        Map loadedMap = MapStorage.loadMap(testMapName, testCreator);
        assertNotNull(loadedMap);
        
        assertEquals(map.getName(), loadedMap.getName());
        assertEquals(map.getCreator(), loadedMap.getCreator());
        assertEquals(map.getWidth(), loadedMap.getWidth());
        assertEquals(map.getHeight(), loadedMap.getHeight());
        
        assertEquals(Map.TileType.ROAD, loadedMap.getTileAt(5, 5));
        assertEquals(Map.TileType.OBSTACLE, loadedMap.getTileAt(6, 6));
        assertEquals(Map.TileType.WATER, loadedMap.getTileAt(7, 7));
        
        Castle[] castles = loadedMap.getCastles();
        assertNotNull(castles[0]);
        assertNotNull(castles[1]);
        assertEquals(1, castles[0].getX());
        assertEquals(1, castles[0].getY());
        assertEquals(2, castles[1].getX());
        assertEquals(2, castles[1].getY());
        
        List<Oasis> oases = loadedMap.getOases();
        assertEquals(2, oases.size());
    }
    
    @Test
    void testGetAllMapNames() throws IOException {
        Map map1 = new Map("Map1", "Creator1", 10, 10);
        Map map2 = new Map("Map2", "Creator1", 10, 10);
        Map map3 = new Map("Map3", "Creator2", 10, 10);
        
        Castle castle1 = new Castle(player, 1, 1);
        Castle castle2 = new Castle(player, 2, 2);
        map1.setCastle(0, castle1);
        map1.setCastle(1, castle2);
        map2.setCastle(0, castle1);
        map2.setCastle(1, castle2);
        map3.setCastle(0, castle1);
        map3.setCastle(1, castle2);
        
        MapStorage.saveMap(map1);
        MapStorage.saveMap(map2);
        MapStorage.saveMap(map3);
        
        List<String> allMapNames = MapStorage.getAllMapNames();
        
        assertTrue(allMapNames.contains("Creator1:Map1"));
        assertTrue(allMapNames.contains("Creator1:Map2"));
        assertTrue(allMapNames.contains("Creator2:Map3"));
    }
    
    @Test
    void testIsMapCreatedBy() throws IOException {
        Map map = new Map("TestMap", "Creator1", 10, 10);
        
        Castle castle1 = new Castle(player, 1, 1);
        Castle castle2 = new Castle(player, 2, 2);
        map.setCastle(0, castle1);
        map.setCastle(1, castle2);
        
        MapStorage.saveMap(map);
        
        assertTrue(MapStorage.isMapCreatedBy("TestMap", "Creator1"));
        
        assertFalse(MapStorage.isMapCreatedBy("TestMap", "Creator2"));
        
        assertFalse(MapStorage.isMapCreatedBy("NonExistentMap", "Creator1"));
    }
    
    @Test
    void testDeleteMap() throws IOException {
        Map map = new Map("TestMap", "Creator1", 10, 10);
        
        Castle castle1 = new Castle(player, 1, 1);
        Castle castle2 = new Castle(player, 2, 2);
        map.setCastle(0, castle1);
        map.setCastle(1, castle2);
        
        MapStorage.saveMap(map);
        
        assertTrue(MapStorage.isMapCreatedBy("TestMap", "Creator1"));
        
        MapStorage.deleteMap("TestMap", "Creator1");
        
        assertFalse(MapStorage.isMapCreatedBy("TestMap", "Creator1"));
    }
    
    @Test
    void testDisplayMapInConsole() throws IOException {
        Map map = new Map("TestMap", "Creator1", 5, 5);
        
        map.setTile(0, 0, Map.TileType.ROAD);
        map.setTile(1, 1, Map.TileType.OBSTACLE);
        map.setTile(2, 2, Map.TileType.WATER);
        
        Castle castle1 = new Castle(player, 0, 0);
        Castle castle2 = new Castle(player, 4, 4);
        map.setCastle(0, castle1);
        map.setCastle(1, castle2);
        
        Oasis oasis1 = new Oasis(1, 1);
        Oasis oasis2 = new Oasis(3, 3);
        map.addOasis(oasis1);
        map.addOasis(oasis2);
        
        MapStorage.saveMap(map);
        
        assertDoesNotThrow(() -> MapStorage.displayMapInConsole("TestMap", "Creator1"));
    }
} 