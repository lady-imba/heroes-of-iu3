package Unit;

import UI.BattleMap;
import Hero.Hero;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Unit implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String type;
    private int health;
    private int attack;
    private int attackRange;
    private int movementRange;
    private int x, y;
    private int movement;  // Добавляем параметр движения

    public Unit(String type, int health, int attack, int attackRange, int movementRange) {
        this.type = type;
        this.health = health;
        this.attack = attack;
        this.attackRange = attackRange;
        this.movementRange = movementRange;
        this.x = 0; // Начальные координаты
        this.y = 0;
        this.movement = movementRange; // По умолчанию равно movementRange
    }

    public boolean canAttack(int targetX, int targetY) {
        int dx = Math.abs(targetX - this.getX());
        int dy = Math.abs(targetY - this.getY());
        boolean canMove = dx + dy <= this.getAttackRange();
        if (canMove) {
            System.out.println("Целевая клетка доступна для атаки.");
        } else {
            System.out.println("Целевая клетка находится за пределами дальности атаки.");
        }
        return canMove;
    }

    public boolean canMoveTo(int targetX, int targetY) {
        int dx = Math.abs(targetX - this.getX());
        int dy = Math.abs(targetY - this.getY());
        boolean canMove = dx + dy <= this.getMovement();
        if (canMove) {
            System.out.println("Целевая клетка доступна для перемещения.");
        } else {
            System.out.println("Целевая клетка находится за пределами дальности перемещения.");
        }
        return canMove;
    }

    // Перемещение юнита
    public void moveUnit(int toX, int toY, BattleMap.BattleCell fromCell, BattleMap.BattleCell toCell, boolean isEnemy) {
        if (isEnemy) {
            fromCell.computerUnits.remove(this);
            toCell.computerUnits.add(this);
        } else {
            fromCell.playerUnits.remove(this);
            toCell.playerUnits.add(this);
        }

        setX(toX);
        setY(toY);
    }

    public void moveUnitWithCursor(BattleMap battleMap) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            char command = scanner.next().charAt(0);

            int targetX = battleMap.getCursor().getCursorX();
            int targetY = battleMap.getCursor().getCursorY();

            boolean canMove = this.canMoveTo(targetX, targetY);

            switch (command) {
                case 'w':
                case 'W':
                    battleMap.getCursor().moveCursor(0, -1);
                    break;
                case 'a':
                case 'A':
                    battleMap.getCursor().moveCursor(-1, 0);
                    break;
                case 's':
                case 'S':
                    battleMap.getCursor().moveCursor(0, 1);
                    break;
                case 'd':
                case 'D':
                    battleMap.getCursor().moveCursor(1, 0);
                    break;
                case 'f':
                case 'F':
                    if (canMove && targetX >= 0 && targetX < battleMap.getWidth() && targetY >= 0 && targetY < battleMap.getHeight()) {
                        BattleMap.BattleCell fromCell = battleMap.getMap()[this.getY()][this.getX()];
                        BattleMap.BattleCell toCell = battleMap.getMap()[targetY][targetX];
                        moveUnit(targetX, targetY, fromCell, toCell, false);
                        return;
                    }
                    break;
                default:
                    System.out.println("Неверная команда.");
                    break;
            }
            battleMap.display();
        }
    }

    private void applyDamage(List<Unit> units, int totalDamage, Hero armyOwner) {
        List<Unit> toRemove = new ArrayList<>();

        for (Unit unit : units) {
            if (totalDamage <= 0) break;

            int damage = Math.min(totalDamage, unit.getHealth());
            unit.setHealth(unit.getHealth() - damage);
            totalDamage -= damage;

            if (unit.getHealth() <= 0) {
                toRemove.add(unit);
            }
        }

        units.removeAll(toRemove);
        armyOwner.getArmy().removeAll(toRemove);
    }

    // Атака юнита
    public void attackUnit(BattleMap.BattleCell attackerCell, BattleMap.BattleCell targetCell, Hero playerHero, Hero computerHero) {
        if (!attackerCell.playerUnits.isEmpty() && !targetCell.computerUnits.isEmpty()) {
            int totalDamage = attackerCell.getTotalPlayerDamage();
            applyDamage(targetCell.computerUnits, totalDamage, computerHero);
        } else if (!attackerCell.computerUnits.isEmpty() && !targetCell.playerUnits.isEmpty()) {
            int totalDamage = attackerCell.getTotalComputerDamage();
            applyDamage(targetCell.playerUnits, totalDamage, playerHero);
        } else {
            System.out.println("Невозможно атаковать: нет вражеских юнитов в целевой клетке.");
        }

    }

    public void attackWithCursor(BattleMap battleMap, Hero playerHero, Hero computerHero) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            char command = scanner.next().charAt(0);

            int targetX = battleMap.getCursor().getCursorX();
            int targetY = battleMap.getCursor().getCursorY();
            boolean canAttack = canAttack(targetX, targetY);

            switch (command) {
                case 'w':
                case 'W':
                    battleMap.getCursor().moveCursor(0, -1);
                    break;
                case 'a':
                case 'A':
                    battleMap.getCursor().moveCursor(-1, 0);
                    break;
                case 's':
                case 'S':
                    battleMap.getCursor().moveCursor(0, 1);
                    break;
                case 'd':
                case 'D':
                    battleMap.getCursor().moveCursor(1, 0);
                    break;
                case 'f':
                case 'F':
                    if (canAttack) {
                        BattleMap.BattleCell attackerCell = battleMap.getMap()[this.getY()][this.getX()];
                        BattleMap.BattleCell targetCell = battleMap.getMap()[targetY][targetX];

                        if (targetCell.playerUnits.isEmpty() && targetCell.computerUnits.isEmpty()) {
                            System.out.println("Нет юнитов для атаки в целевой клетке!");
                            return;
                        }

                        attackUnit(attackerCell, targetCell, playerHero, computerHero);
                        battleMap.getCursor().moveCursor(1, 0);
                        return;
                    }
                    break;
                default:
                    System.out.println("Неверная команда.");
                    break;
            }
            battleMap.display();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttack() {
        return attack;
    }
    
    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDamage() {
        return attack;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public int getMovementRange() {
        return movementRange;
    }

    public int getMovement() { return movement; }
    public void setMovement(int movement) { this.movement = movement; }
}