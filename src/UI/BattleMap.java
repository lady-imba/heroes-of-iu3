package UI;

import Hero.Hero;
import Unit.Unit;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

public class BattleMap implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int height;
    private int width;
    private BattleCell[][] map;
    private Hero playerHero;
    private Hero computerHero;
    private Cursor cursor;

    public Cursor getCursor() {
        return cursor;
    }

    //убрала abstract
    // Внутренний класс для хранения юнитов в одной клетке
    public static class BattleCell implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public List<Unit> playerUnits = new ArrayList<>();
        public List<Unit> computerUnits = new ArrayList<>();

        // Суммарное здоровье юнитов игрока в клетке
        public int getTotalPlayerHealth() {
            return playerUnits.stream().mapToInt(Unit::getHealth).sum();
        }
        //.stream() - преобразует список playerUnits в поток (Stream<Unit>), чтобы можно было применить цепочку операций.
        //.mapToInt() - преобразует поток объектов в поток чисел
        //Unit::getHealth - взять метод getHealth() из класса Unit и применить его к текущему объекту в потоке

        // Суммарная атака юнитов игрока в клетке
        public int getTotalPlayerDamage() {
            return playerUnits.stream().mapToInt(Unit::getAttack).sum();
        }

        // Суммарное здоровье юнитов компьютера в клетке
        public int getTotalComputerHealth() {
            return computerUnits.stream().mapToInt(Unit::getHealth).sum();
        }

        // Суммарная атака юнитов компьютера в клетке
        public int getTotalComputerDamage() {
            return computerUnits.stream().mapToInt(Unit::getAttack).sum();
        }
    }

    // Конструктор
    public BattleMap(int height, int width, Hero playerHero, Hero computerHero) {
        this.height = height;
        this.width = width;
        this.map = new BattleCell[height][width];
        this.playerHero = playerHero;
        this.computerHero = computerHero;
        this.cursor = new Cursor(0, (height - playerHero.getArmy().size()) / 2, false, width, height);
        initializeMap();
    }

    // Инициализация карты
    private void initializeMap() {
        // Инициализация всех клеток
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[i][j] = new BattleCell();
            }
        }

        // Размещение юнитов игрока
        int playerRow = 0;
        for (Unit unit : playerHero.getArmy()) {
            int x = 0;
            int y = playerRow % height;
            map[y][x].playerUnits.add(unit); //наращиваем список playerUnits
            unit.setX(x);
            unit.setY(y);
            playerRow++;
        }

        // Размещение юнитов компьютера
        int computerRow = 0;
        for (Unit unit : computerHero.getArmy()) {
            int x = width - 1;
            int y = computerRow % height;
            map[y][x].computerUnits.add(unit);
            unit.setX(x);
            unit.setY(y);
            computerRow++;
        }
    }

    // Отображение карты
    public void display() {
        System.out.println("Карта сражения:");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                BattleCell cell = map[i][j];
                String display = ".";

                if (!cell.playerUnits.isEmpty()) {
                    display = "P(" + cell.getTotalPlayerHealth() + "/" + cell.getTotalPlayerDamage() + ")";
                } else if (!cell.computerUnits.isEmpty()) {
                    display = "E(" + cell.getTotalComputerHealth() + "/" + cell.getTotalComputerDamage() + ")";
                }

                if (cursor.isCursorActive() && i == cursor.getCursorY() && j == cursor.getCursorX()) {
                    System.out.print("[+" + display + "+] ");
                } else {
                    System.out.print(" " + display + "  ");
                }
            }
            System.out.println();
        }
    }

    public BattleCell[][] getMap() {
        return map;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Hero getPlayerHero() {
        return playerHero;
    }

    public Hero getComputerHero() {
        return computerHero;
    }

}