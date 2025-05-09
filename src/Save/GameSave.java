package Save;

import UI.GameMap;
import Player.*;
import Castle.Castle;
import Map.Map;
import java.io.Serializable;
import java.util.List;

//Это класс-контейнер для данных, который хранит состояние игры.
//GameSave отвечает только за хранение данных ("что" сохраняется)
//SaveManager отвечает за процесс сохранения/загрузки "как" сохраняется
public class GameSave implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private HumanPlayer player;
    private ComputerPlayer computer;
    private GameMap gameMap;
    private String timestamp;
    private boolean isAutoSave;
    private int turnsLeft;
    private List<Castle> castles;
    private int bonus; // Бонус от мини-игры 21
    private String bonusWinner; // Кто выиграл бонус ("player" или "computer")
    
    public GameSave(HumanPlayer player, ComputerPlayer computer, GameMap gameMap, List<Castle> castles, String timestamp, boolean isAutoSave, int turnsLeft) {
        this.player = player;
        this.computer = computer;
        this.gameMap = gameMap;
        this.castles = castles;
        this.timestamp = timestamp;
        this.isAutoSave = isAutoSave;
        this.turnsLeft = turnsLeft;
        this.bonus = 0;
        this.bonusWinner = null;
    }

    public GameSave(HumanPlayer player, ComputerPlayer computer, Map map, List<Castle> castles, String timestamp, boolean isAutoSave, int turnsLeft) {
        this.player = player;
        this.computer = computer;
        // Преобразуем Map в GameMap
        this.gameMap = new UI.GameMap(map, castles.toArray(new Castle[0]), player, computer);
        this.castles = castles;
        this.timestamp = timestamp;
        this.isAutoSave = isAutoSave;
        this.turnsLeft = turnsLeft;
        this.bonus = 0;
        this.bonusWinner = null;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public ComputerPlayer getComputer() {
        return computer;
    }
    
    public GameMap getGameMap() {
        return gameMap;
    }

    public Map getMap() {
        if (gameMap != null) {
            return gameMap.getCustomMap();
        }
        return null;
    }
    
    public List<Castle> getCastles() {
        return castles;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public boolean isAutoSave() {
        return isAutoSave;
    }
    
    public int getTurnsLeft() {
        return turnsLeft;
    }
    
    public int getBonus() {
        return bonus;
    }
    
    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
    
    public String getBonusWinner() {
        return bonusWinner;
    }
    
    public void setBonusWinner(String bonusWinner) {
        this.bonusWinner = bonusWinner;
    }
    
    @Override
    public String toString() {
        String saveType = isAutoSave ? "Автосохранение" : "Ручное сохранение";
        String bonusInfo = bonus > 0 ? " (Бонус: " + bonus + " для " + 
                          (bonusWinner != null && bonusWinner.equals("player") ? "игрока" : "компьютера") + ")" : "";
        return saveType + " от " + timestamp + bonusInfo;
    }
} 