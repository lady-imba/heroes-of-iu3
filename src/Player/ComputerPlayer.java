package Player;

import Castle.Castle;
import UI.GameMap;
import Hero.Hero;

import java.util.ArrayList;

public class ComputerPlayer extends Player {
    public ComputerPlayer(Hero initialHero, String terrainSymbol, boolean isAnotherPlayer, String name) {
        super(initialHero, terrainSymbol, isAnotherPlayer, name);
    }

    public void computerMove(Hero computerHero, Hero humanHero, Castle playerCastle, Player humanPlayer, GameMap map) {
        ArrayList<GameMap.Node> reachableCells = map.getReachableCells(
                computerHero.getX(),
                computerHero.getY(),
                computerHero.getMovementRange(),
                this
        );

        // Попытка атаковать героя игрока
        for (GameMap.Node cell : reachableCells) {
            if (cell.x == humanHero.getX() && cell.y == humanHero.getY()) {
                computerHero.Move(cell.x, cell.y, cell.cost);
                System.out.println("Компьютер атакует героя игрока!");
                map.startBattle(humanPlayer, this);
                return;
            }
        }

        // Поиск оптимальной клетки для движения к замку
        GameMap.Node bestCell = null;
        double minDistance = Double.MAX_VALUE;
        int targetX = playerCastle.getX();
        int targetY = playerCastle.getY();

        for (GameMap.Node cell : reachableCells) {
            double distance = Math.sqrt(
                    Math.pow(cell.x - targetX, 2) +
                            Math.pow(cell.y - targetY, 2)
            );

            if (distance < minDistance) {
                minDistance = distance;
                bestCell = cell;
            }
        }

        if (bestCell != null) {
            computerHero.Move(bestCell.x, bestCell.y, bestCell.cost);
            System.out.printf("Компьютер переместился на (%d, %d). Осталось энергии: %d%n",
                    bestCell.x, bestCell.y, computerHero.getMovementRange());
        }
    }
}
