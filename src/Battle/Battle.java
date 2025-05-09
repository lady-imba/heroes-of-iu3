package Battle;

import UI.BattleMap;
import Player.Player;
import Hero.Hero;
import Unit.Unit;
import java.util.*;

public class Battle {
    private Player player;
    private Player computer;
    private BattleMap battleMap;

    public Battle(Player player, Player computer) {
        this.player = player;
        this.computer = computer;
        this.battleMap = new BattleMap(10, 10, player.getActiveHero(), computer.getActiveHero());
    }

    public void start() {
        System.out.println("Началось сражение!");
        battleMap.display();

        while (!player.getActiveHero().getArmy().isEmpty() && !computer.getActiveHero().getArmy().isEmpty()) {
            playerTurn();
            if (computer.getActiveHero().getArmy().isEmpty()) {
                System.out.println("Игрок победил!");
                removeDefeatedHero(computer);
                break;
            }

            computerTurn();
            if (player.getActiveHero().getArmy().isEmpty()) {
                System.out.println("Компьютер победил!");
                removeDefeatedHero(player);
                break;
            }
        }
    }

    public void playerTurn() {
        System.out.println("Ход игрока:");
        Scanner scanner = new Scanner(System.in);

        for (Unit unit : player.getActiveHero().getArmy()) {
            System.out.println("Выберите действие для юнита " + unit.getType() + " (M - перемещение, A - атака, S - пропуск):");
            char action = scanner.next().charAt(0);
            battleMap.getCursor().setCursorX(unit.getX());
            battleMap.getCursor().setCursorY(unit.getY());
            battleMap.getCursor().setCursorActive(true);
            battleMap.display();

            switch (action) {
                case 'M':
                case 'm':
                    System.out.println("Используйте курсор для выбора позиции перемещения (WASD - движение, F - подтвердить):");
                    unit.moveUnitWithCursor(battleMap);
                    break;
                case 'A':
                case 'a':
                    System.out.println("Используйте курсор для выбора цели атаки (WASD - движение, F - подтвердить):");
                    unit.attackWithCursor(battleMap, player.getActiveHero(), computer.getActiveHero());
                    break;
                case 'S':
                case 's':
                    System.out.println("Юнит пропускает ход.");
                    break;
                default:
                    System.out.println("Неверное действие, юнит пропускает ход.");
                    break;
            }
            battleMap.getCursor().setCursorActive(false);
            battleMap.display();
        }
    }

    public void computerTurn() {
        System.out.println("Ход компьютера:");
        for (Unit unit : computer.getActiveHero().getArmy()) {
            boolean attacked = false;
            for (Unit playerUnit : player.getActiveHero().getArmy()) {
                if (Math.abs(unit.getX() - playerUnit.getX()) <= unit.getAttackRange() &&
                        Math.abs(unit.getY() - playerUnit.getY()) <= unit.getAttackRange()) {
                    BattleMap.BattleCell fromCell = battleMap.getMap()[unit.getY()][unit.getX()];
                    BattleMap.BattleCell toCell = battleMap.getMap()[ playerUnit.getY()][ playerUnit.getX()];
                    unit.attackUnit(fromCell, toCell, player.getActiveHero(), computer.getActiveHero());
                    attacked = true;
                    break;
                }
            }
            if (!attacked) {
                int moveX = unit.getX() - 1; // Двигаемся вперед
                int moveY = unit.getY();
                BattleMap.BattleCell fromCell = battleMap.getMap()[unit.getY()][unit.getX()];
                BattleMap.BattleCell toCell = battleMap.getMap()[moveY][moveX];
                unit.moveUnit(moveX, moveY, fromCell, toCell, true);
            }
        }
        battleMap.display();
    }

    // Удаление героя проигравшего участника
    public void removeDefeatedHero(Player defeatedPlayer) {
        Hero defeatedHero = defeatedPlayer.getActiveHero();
        defeatedPlayer.getHeroes().remove(defeatedHero); // Удаляем героя из списка героев

        if (defeatedPlayer.getHeroes().isEmpty()) {
            // Если у проигравшего не осталось героев, завершаем игру
            declareMatchWinner(defeatedPlayer);
        } else {
            // Если герои остались, выбираем следующего активного героя
            defeatedPlayer.setActiveHero(defeatedPlayer.getHeroes().getFirst());
            System.out.println("У проигравшего остались другие герои. Продолжаем игру!");
        }
    }

    // Объявление победителя матча
    private void declareMatchWinner(Player defeatedPlayer) {
        if (defeatedPlayer == player) {
            System.out.println("Компьютер победил в матче! Игра завершена.");
        } else {
            System.out.println("Игрок победил в матче! Игра завершена.");
        }
        System.exit(0); // Завершаем игру
    }

    public BattleMap getBattleMap() {
        return battleMap;
    }

    //для теста
    private void declareMatchWinnerForTest(Player defeatedPlayer) {
        if (defeatedPlayer == player) {
            System.out.println("Компьютер победил в матче! Игра завершена.");
        } else {
            System.out.println("Игрок победил в матче! Игра завершена.");
        }
    }

    public void removeDefeatedHeroForTest(Player defeatedPlayer) {
        Hero defeatedHero = defeatedPlayer.getActiveHero();
        defeatedPlayer.getHeroes().remove(defeatedHero); // Удаляем героя из списка героев

        if (defeatedPlayer.getHeroes().isEmpty()) {
            // Если у проигравшего не осталось героев, завершаем игру
            declareMatchWinnerForTest(defeatedPlayer);
        } else {
            // Если герои остались, выбираем следующего активного героя
            defeatedPlayer.setActiveHero(defeatedPlayer.getHeroes().getFirst());
            System.out.println("У проигравшего остались другие герои. Продолжаем игру!");
        }
    }
}