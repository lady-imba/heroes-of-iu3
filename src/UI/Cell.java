package UI;

import Player.Player;
import java.util.Objects;
import java.io.Serializable;

public class Cell implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String type;
    public static final String barrier = "#";
    public static final String road = "=";
    public static final String empty = ".";
    public static final String oasis = "О";
    public static final String hotel = "А"; // Символ для отеля
    public static final String cafe = "К";  // Символ для кафе
    public static final String hairdresser = "П"; // Символ для парикмахерской
    public static final String building = "Ы"; // Общий символ для зданий (не используется)
    private Player player;
    private String symbol;      // текущий отображаемый символ
    private String customSymbol; // хранит пользовательский символ
    private int x, y;

    public Cell(String type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.symbol = type;    // по умолчанию равен типу клетки
        this.customSymbol = null;
    }

    public String getSymbol() {
        if (player != null) {
            return player.getTerrainSymbol();
        }
        return customSymbol != null ? customSymbol : type;
    }

    public void setSymbol(String symbol) {
        this.customSymbol = symbol;
        updateSymbol();
    }

    public void setPlayer(Player player) {
        this.player = player;
        updateSymbol();
    }

    private void updateSymbol() {
        this.symbol = getSymbol(); // обновляем symbol в соответствии с логикой
    }

    // Остальные методы остаются без изменений
    public void setType(String type) {
        this.type = type;
        updateSymbol();
    }

    public String getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getMovementCost(Player player) {
        if (Objects.equals(this.type, road)) {
            return 2;
        }
        if (Objects.equals(this.type, empty)) {
            return 4;
        }
        if (Objects.equals(this.type, barrier)) {
            return Integer.MAX_VALUE;
        }
        if (Objects.equals(this.type, hotel) || Objects.equals(this.type, cafe) || 
            Objects.equals(this.type, hairdresser) || Objects.equals(this.type, building)) {
            return 3; // Здания можно проходить, но с небольшим штрафом
        }
        if (this.player != null && this.player != player) {
            return 4;
        }
        if (Objects.equals(this.type, "И") || Objects.equals(this.type, "К")) {
            return 2; // Замки должны иметь стоимость как дороги
        }
        return 1;
    }

    public Player getPlayer() {
        return player;
    }
}