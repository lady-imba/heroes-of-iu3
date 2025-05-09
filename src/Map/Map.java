package Map;

import java.io.Serializable;
import Castle.Castle;
import Building.Building;
import Oasis.Oasis;
import java.util.ArrayList;
import java.util.List;

public class Map implements Serializable { //объекты класса могут быть сериализованы (преобразованы в последовательность байтов) и десериализованы (восстановлены из байтов).
    private static final long serialVersionUID = 1L;//уникальный идентификатор версии сериализованного класса.

    private String name;
    private String creator;
    private int width;
    private int height;
    private TileType[][] tiles;
    private Castle[] castles;
    private List<Oasis> oases;

    //enum (перечисление) — это специальный тип данных, который позволяет задать ограниченный набор именованных констант
    public enum TileType {
        EMPTY,
        ROAD,
        OBSTACLE,
        WATER,
        HUMAN,
        COMPUTER
    }

    public Map(String name, String creator, int width, int height) {
        this.name = name;
        this.creator = creator;
        this.width = width;
        this.height = height;
        this.tiles = new TileType[width][height];
        this.castles = new Castle[2];
        this.oases = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = TileType.EMPTY;
            }
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    public TileType getTileAt(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[x][y];
        }
        return TileType.EMPTY;
    }

    public void setTile(int x, int y, TileType type) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[x][y] = type;
        }
    }

    public Castle[] getCastles() {
        return castles;
    }

    public void setCastle(int index, Castle castle) {
        if (index >= 0 && index < castles.length) {
            if (castle != null &&
                    castle.getX() >= 0 && castle.getX() < width &&
                    castle.getY() >= 0 && castle.getY() < height) {
                castles[index] = castle;
            }
        }
    }

    public List<Oasis> getOases() {
        return oases;
    }

    public void addOasis(Oasis oasis) {
        if (oasis != null &&
                oasis.getOasisX() >= 0 && oasis.getOasisX() < width &&
                oasis.getOasisY() >= 0 && oasis.getOasisY() < height) {
            oases.add(oasis);
        }
    }

    public void removeOasis(Oasis oasis) {
        oases.remove(oasis);
    }

    public void clearOases() {
        oases.clear();
    }
}