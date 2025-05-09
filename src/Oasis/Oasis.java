package Oasis;

import Hero.Hero;
import java.io.Serializable;

public class Oasis implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int x;
    private int y;
    boolean oasisUsed = false;

    public Oasis(int x, int y) {
        this.x = x;
        this.y = y;
    }
    // Сброс состояния оазиса
    public void resetOasis() {
        oasisUsed = false;
    }

    // Применение эффекта оазиса
    public void applyOasisEffect(Hero hero) {
        if (canUseOasis(hero)) {
            hero.increaseMaxMovementRange(1);
            oasisUsed = true;
            System.out.println(hero.getName() + " получил +1 к дальности!");
        }
    }

    // Проверка возможности использования оазиса
    public boolean canUseOasis(Hero hero) {
        return hero.getX() == x &&
                hero.getY() == y &&
                !oasisUsed; //флаг не позволяет использовать оазис дважды за 1 ход
    }

    public int getOasisY() {
        return y;
    }

    public int getOasisX() {
        return x;
    }

    public boolean isOasisUsed() {
        return oasisUsed;
    }
}
