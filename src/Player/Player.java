package Player;

import Hero.Hero;
import Castle.Castle;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int gold;
    private int level;
    private ArrayList<Hero> heroes;
    private HashMap<String, Integer> units;
    private Hero activeHero;
    private String playerId;
    private String terrainSymbol;
    private Castle castle;
    private boolean isAnotherPlayer;
    private String name;
    private boolean invasionBonus; // Флаг бонуса сокращения времени захвата замка

    public Player(Hero initialHero, String terrainSymbol, boolean isAnotherPlayer, String name) {
        this.level = 1;
        this.gold = 500;
        this.heroes = new ArrayList<>();
        this.pushHero(initialHero);
        activeHero = initialHero;
        this.units = new HashMap<>();
        this.playerId = UUID.randomUUID().toString();
        this.terrainSymbol=terrainSymbol;
        this.castle = null;
        this.isAnotherPlayer = isAnotherPlayer;
        this.name = name;
        this.invasionBonus = false; // По умолчанию бонус не активен
    }

    public String getTerrainSymbol() {
        return terrainSymbol;
    }

    public boolean isAnotherPlayer() {
        return isAnotherPlayer;
    }

    public void setCastle(Castle castle) {
        this.castle = castle;
    }

    public void buyHeroes(int amount) {
        while (amount > 0) {
            Hero newHero = new Hero(0, 0, 5, "Warrior");
            pushHero(newHero);
            amount--;
        }
    }

    public String getPlayerId() {
        return playerId;
    }

    // Добавляем юнитов к герою
    public void addUnits(String unitType, int count) {
        units.put(unitType, units.getOrDefault(unitType, 0) + count);
    }


    public HashMap<String, Integer> getUnits() {
        return units;
    }

    public void removeUnits(String type, int amount) {
        if (this.units.containsKey(type) && (this.units.get(type)) >= amount) {
            this.units.put(type, this.units.get(type) - amount);
        }
    }

    public void AddGold(int gold) {
        this.gold += gold;
    }

    public boolean SpendGold(int gold) {
        if (this.gold >= gold) {
            this.gold -= gold;
            return true;
        }
        return false;
    }

    public int getGold() {
        return gold;
    }

    public ArrayList<Hero> getHeroes() {
        return heroes;
    }

    public void setActiveHero(Hero activeHero) {
        this.activeHero = activeHero;
    }

    public Hero getActiveHero() {
        return activeHero;
    }

    public void pushHero(Hero hero) {
        this.heroes.add(hero);
    }

    public ArrayList<Hero> getHeroesInCastle() {
        ArrayList<Hero> heroesInCastle = new ArrayList<>();
        for (Hero hero : heroes) {
            if (hero.getX() == castle.getX() && hero.getY() == castle.getY()) {
                heroesInCastle.add(hero);
            }
        }
        return heroesInCastle;
    }

    public String getName() {
        return name;
    }

    public void restoreHeroEnergy(int energyAmount, boolean silent) {
        if (activeHero != null) {
            int oldEnergy = activeHero.getEnergy();
            activeHero.setEnergy(Math.min(activeHero.getMaxEnergy(), oldEnergy + energyAmount));
            
            if (!silent) {
                System.out.println("Энергия героя восстановлена: " + oldEnergy + " -> " + activeHero.getEnergy());
            }
        }
    }

    public void restoreHeroEnergy(int energyAmount) {
        restoreHeroEnergy(energyAmount, false);
    }

    //добавлено для тестов
    public Castle getCastle() {
        return castle;
    }

    public void setInvasionBonus(boolean hasBonus) {
        this.invasionBonus = hasBonus;
    }

    public boolean hasInvasionBonus() {
        return invasionBonus;
    }
}