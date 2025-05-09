package Hero;

import java.util.ArrayList;
import java.io.Serializable;
import Unit.Unit;

public class Hero implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int x, y;
    private int maxMovementRange;
    private int movementRange; // Добавляем поле для текущей дальности перемещения
    private ArrayList<Unit> army;
    private String name;

    public Hero(int x, int y, int maxMovementRange, String name) {
        this.x = x;
        this.y = y;
        this.maxMovementRange = maxMovementRange;
        this.movementRange = maxMovementRange; // Начальная дальность перемещения
        this.army = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Геттер для текущей дальности перемещения
    public int getMovementRange() {
        return movementRange;
    }
    
    // Alias for getMovementRange() to maintain compatibility
    public int getEnergy() {
        return movementRange;
    }
    
    // Alias for getMaxMovementRange() to maintain compatibility
    public int getMaxEnergy() {
        return maxMovementRange;
    }
    
    // Alias for setting movementRange to maintain compatibility
    public void setEnergy(int energy) {
        this.movementRange = energy;
    }
    
    public void displayRestMovementRange () {
        System.out.printf("Энергия: " + this.movementRange + " ");
    }

    public void decreaseMovementRange(int cost) {
        if (this.movementRange - cost >= 0) {
            this.movementRange -= cost;
        } else {
            this.movementRange = 0;
        }
    }

    public void resetMovementRange() {
        this.movementRange = this.maxMovementRange;
    }

    public void addUnit(Unit unit) {
        this.army.add(unit);
    }

    public ArrayList<Unit> getArmy() {
        return army;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void Move(int newX, int newY, int pathCost) {
        this.x = newX;
        this.y = newY;
        decreaseMovementRange(pathCost);//передвинули и вычли энергию
    }

    public int getMaxMovementRange() {
        return this.maxMovementRange;
    }

    // Увеличиваем максимальную дальность перемещения
    public void increaseMaxMovementRange(int amount) {
        this.maxMovementRange += amount;
        this.movementRange = this.maxMovementRange; // Восстанавливаем энергию
    }

    //добавлено для тестов
    public void setX(int x) {
        this.x=x;
    }

    public void setY(int y) {
        this.y=y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

}