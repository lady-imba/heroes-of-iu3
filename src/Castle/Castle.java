package Castle;

import java.util.Map;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.Serializable;

import Battle.Battle;
// Заменяем импорт Building.Building на локальный класс
// import Building.Building;
import Player.Player;
import Hero.Hero;
import Unit.Unit;


public class Castle implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private ArrayList<CastleBuilding> buildings;
    private Player player;
    private Player invader = null; // Игрок, захвативший замок
    private int invasionTimer = 0; // Счётчик ходов захвата
    private int x;
    private int y;
    private String ownerId;
    private boolean isInCastle;
    private ArrayList<CastleBuilding> availableBuildings; // Список доступных для покупки зданий

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public Castle(Player player, int x, int y) {
        this.x = x;
        this.y = y;
        this.player = player;
        buildings = new ArrayList<>();
        availableBuildings = new ArrayList<>();
        CastleBuilding tavern = new CastleBuilding("Tavern", "Найм героев", 300);
        CastleBuilding stable =  new CastleBuilding("Stable", "Увеличивает передвижение героя", 150);
        CastleBuilding guard = new CastleBuilding("Guard", "Покупка копейщиков", 100);
        availableBuildings.add(tavern);
        availableBuildings.add(stable);
        availableBuildings.add(guard);
        this.ownerId = player.getPlayerId();
        this.isInCastle = false;
    }

    public void enterCastle(Player player) {
        this.isInCastle = true;
        if (!player.getPlayerId().equals(ownerId)) {
            // Чужой игрок вошёл в замок
            if (invader == null) {
                invader = player;
                System.out.println(player.getName() + " начал захват замка!");
                if (player.isAnotherPlayer()) {
                    return;//если вошел комп, то возвращаемся
                } else {
                    onCastleCaptured();//если вошел человек, то заходим сюда
                }
            }
        } else {
            // Владелец вернулся в замок
            if (invader != null) {
                System.out.println("Владелец вернул контроль над замком!");
                resetInvasion();
                startBattleForCastle();
            }
        }

        if (this.isDestroyed()) {
            return;
        }
        while (this.isInCastle) {
            this.displayMenu();
        }
    }

    public void displayMenu() {
        if (!isInCastle) {
            return;
        }
        System.out.println("\n=== Меню замка ===");
        System.out.println("Ваше золото: " + player.getGold());
        System.out.println("Выберите действие:");
        System.out.println("1 - Использовать существующие здания");
        System.out.println("2 - Купить новые здания");
        System.out.println("3 - Снарядить героя армией");
        System.out.println("F - Выйти из замка");

        Scanner scanner = new Scanner(System.in);
        String choice = scanner.next();

        switch (choice.toUpperCase()) {
            case "1":
                showExistingBuildings();
                break;
            case "2":
                showBuildingsForPurchase();
                break;
            case "3":
                equipHeroWithArmy();
                break;
            case "F":
                leaveCastle();
                break;
            default:
                System.out.println("Неверный ввод!");
                displayMenu();
        }
    }

    private void showExistingBuildings() {
        System.out.println("\nСуществующие здания:");
        for (int i = 0; i < buildings.size(); i++) {
            System.out.printf("%d - %s (%s)%n", i, buildings.get(i).getName(), buildings.get(i).getDescription());
        }
        System.out.println("Нажмите F чтобы вернуться");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
        if (!input.equalsIgnoreCase("F")) {
            int choice = Integer.parseInt(input);
            useBuilding(choice);
        }
        displayMenu();
    }

    private void showBuildingsForPurchase() {
        System.out.println("\nДоступные для покупки здания:");
        for (int i = 0; i < availableBuildings.size(); i++) {
            CastleBuilding b = availableBuildings.get(i);
            System.out.printf("%d - %s (%s) Цена: %d золота%n",
                    i, b.getName(), b.getDescription(), b.getCost());
        }
        System.out.println("Введите номер здания для покупки или F для отмены");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();

        if (input.equalsIgnoreCase("F")) {
            displayMenu();
            return;
        }

        try {
            int choice = Integer.parseInt(input);
            purchaseBuilding(choice);
        } catch (Exception e) {
            System.out.println("Ошибка ввода!");
            showBuildingsForPurchase();
        }
    }

    public void purchaseBuilding(int index) {
        if (index < 0 || index >= availableBuildings.size()) {
            System.out.println("Неверный выбор!");
            return;
        }

        CastleBuilding selected = availableBuildings.get(index);

        if (player.getGold() >= selected.getCost()) {
            player.SpendGold(selected.getCost());
            buildings.add(selected);
            System.out.println("Здание " + selected.getName() + " успешно куплено!");

            availableBuildings.remove(index);
        } else {
            System.out.println("Недостаточно золота! У вас " + player.getGold());
        }
        showBuildingsForPurchase();
    }

    private void increaseHeroMovementRange() {
        System.out.println("\n=== Улучшение героев в конюшне ===");
        ArrayList<Hero> heroesInCastle = player.getHeroesInCastle();

        if (heroesInCastle.isEmpty()) {
            System.out.println("В замке нет героев для улучшения!");
            return;
        }

        System.out.println("Выберите героя для увеличения дальности перемещения:");
        for (int i = 0; i < heroesInCastle.size(); i++) {
            Hero hero = heroesInCastle.get(i);
            System.out.printf("%d - %s (Текущая дальность: %d)%n",
                    i, hero.getName(), hero.getMaxMovementRange());
        }

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        if (choice >= 0 && choice < heroesInCastle.size()) {
            Hero selectedHero = heroesInCastle.get(choice);
            selectedHero.increaseMaxMovementRange(1); // Увеличиваем максимальную дальность
            System.out.printf("Дальность перемещения героя %s увеличена до %d%n",
                    selectedHero.getName(), selectedHero.getMaxMovementRange());
        } else {
            System.out.println("Неверный выбор!");
        }
    }

    private void useBuilding(int index) {
        if (index < 0 || index >= buildings.size()) {
            System.out.println("Неверный выбор!");
            return;
        }

        String chosenBuilding = buildings.get(index).getName();
        switch (chosenBuilding) {
            case "Tavern":
                goInTavern();
                break;
            case "Guard":
                goInGuardPost();
                break;
            case "Stable":
                goInStable();
                break;
            default:
                System.out.println("Это здание ещё не реализовано!");
        }
    }

    public void goInTavern() {
        System.out.println("Ваш баланс: " + player.getGold() + ". Стоимость героя - 100 золота");
        int k = player.getGold() / 100;
        System.out.println("Вы можете купить " + k + " героев");
        System.out.println("Введите количество");
        Scanner scanner = new Scanner(System.in);
        int amount = scanner.nextInt();
        if (amount * 100 <= player.getGold()) {
            player.SpendGold(amount * 100);
            player.buyHeroes(amount);
            System.out.println("Вы приобрели " + amount + " героев!");
        } else {
            System.out.println("У вас недостаточно золота!");
        }
        displayMenu();
    }

    public void goInStable() {
        System.out.println("Вы зашли в конюшню!");
        increaseHeroMovementRange();
        displayMenu();
    }

    public void goInGuardPost() {
        System.out.println("Ваш баланс: " + player.getGold() + ". Стоимость одного копейщика - 2 золота");
        int k = player.getGold() / 2;
        System.out.println("Вы можете купить " + k + " копейщиков (ХП-5, Атака-1, Перемещение-2)");
        System.out.println("Введите количество");
        Scanner scanner = new Scanner(System.in);
        int amount = scanner.nextInt();
        if (amount * 2 <= player.getGold()) {
            player.SpendGold(amount * 2);
            player.addUnits("Копейщик", amount);
            System.out.println("Вы приобрели " + amount + " копейщиков!");
        } else {
            System.out.println("У вас недостаточно золота!");
        }
        displayMenu();
    }

    private void equipHeroWithArmy() {
        System.out.println("\n=== Снаряжение героя армией ===");
        ArrayList<Hero> heroesInCastle = player.getHeroesInCastle();

        if (heroesInCastle.isEmpty()) {
            System.out.println("В замке нет героев для снаряжения!");
            return;
        }

        // Выбор героя
        System.out.println("Выберите героя:");
        for (int i = 0; i < heroesInCastle.size(); i++) {
            Hero hero = heroesInCastle.get(i);
            System.out.printf("%d - %s (Текущая армия: %d юнитов)%n",
                    i, hero.getName(), hero.getArmy().size());
        }

        Scanner scanner = new Scanner(System.in);
        int heroChoice = scanner.nextInt();

        if (heroChoice < 0 || heroChoice >= heroesInCastle.size()) {
            System.out.println("Неверный выбор героя!");
            return;
        }

        Hero selectedHero = heroesInCastle.get(heroChoice);

        // Выбор юнитов
        System.out.println("\nДоступные юниты в армии игрока:");
        Map<String, Integer> availableUnits = player.getUnits();
        if (availableUnits.isEmpty()) {
            System.out.println("У вас нет юнитов в армии!");
            return;
        }

        //Map.Entry<String, Integer> entry - получаем и ключ, и значение за одну операцию.
        for (Map.Entry<String, Integer> entry : availableUnits.entrySet()) { //.entrySet() - преобразует словарь в набор пар
            System.out.printf("- %s: %d юнитов%n", entry.getKey(), entry.getValue());
        }

        System.out.println("Введите тип юнита, которого хотите добавить:");
        String unitType = scanner.next();

        if (!availableUnits.containsKey(unitType) || availableUnits.get(unitType) <= 0) {
            System.out.println("У вас нет таких юнитов!");
            return;
        }

        System.out.println("Введите количество юнитов (доступно: " + availableUnits.get(unitType) + "):");
        int unitCount = scanner.nextInt();

        if (unitCount <= 0 || unitCount > availableUnits.get(unitType)) {
            System.out.println("Неверное количество юнитов!");
            return;
        }

        // Добавляем юнитов к герою и удаляем их из армии игрока
        for (int i = 0; i <unitCount; i++) {
            selectedHero.addUnit(new Unit(unitType, 10, 2, 2, 2));
        }
        player.removeUnits(unitType, player.getUnits().get(unitType));

        System.out.printf("Герой %s теперь имеет %d юнитов типа %s.%n",
                selectedHero.getName(), selectedHero.getArmy().size(), unitType);
    }

    public void leaveCastle() {
        this.isInCastle = false;
    }

    // Обновление состояния замка каждый ход
    public void processInvasion() {
        if (invader != null) {
            invasionTimer++;
            
            // Проверяем, есть ли у захватчика бонус сокращения времени захвата
            int requiredTurns = 2; // По умолчанию требуется 2 хода
            if (invader.hasInvasionBonus()) {
                requiredTurns = 1; // Если есть бонус, то требуется только 1 ход
                System.out.println("Игрок " + invader.getName() + " использует бонус сокращения времени захвата!");
            }
            
            if (invasionTimer >= requiredTurns) {
                System.out.println("Замок захвачен игроком " + invader.getName() + "!");
                onCastleCaptured(); // Объявление победы
            }
        }
    }

    // Сброс захвата при входе владельца
    private void resetInvasion() {
        invader = null;
        invasionTimer = 0;
    }

    // Обработка захвата
    private void onCastleCaptured() {// Логика завершения игры
        invasionTimer = 2;
        System.out.println("Игрок " + invader.getName() + " победил в матче!");
    }

    private void startBattleForCastle () {
        Battle battle = new Battle(player, invader);
        battle.start();
    }

    //добавлено для тестов
    public String getOwnerId() {
        return ownerId;
    }

    // В класс Castle добавляем:
    public boolean isInCastle() {
        return this.isInCastle;
    }

    public ArrayList<CastleBuilding> getBuildings(){
        return buildings;
    }

    public ArrayList<CastleBuilding> getAvailableBuildings(){
        return availableBuildings;
    }

    public Player getInvader() {
        return invader;
    }

    public int getInvasionTimer(){
        return invasionTimer;
    }


     //Проверяет, уничтожен ли замок (захвачен противником) return true, если замок захвачен, иначе false
    public boolean isDestroyed() {
        if (invader == null) {
            return false;
        }
        
        // Проверяем, есть ли у захватчика бонус сокращения времени захвата
        int requiredTurns = 2; // По умолчанию требуется 2 хода
        if (invader.hasInvasionBonus()) {
            requiredTurns = 1; // Если есть бонус, то требуется только 1 ход
        }
        
        return invasionTimer >= requiredTurns;
    }

    public void purchaseBuildingTest(int index) {
        if (index < 0 || index >= availableBuildings.size()) {
            return;
        }

        CastleBuilding selected = availableBuildings.get(index);

        if (player.getGold() >= selected.getCost()) {
            player.SpendGold(selected.getCost());
            buildings.add(selected);
            availableBuildings.remove(index);
        }
    }

    public void enterCastleTest(Player player) {
        this.isInCastle = true;
        if (!player.getPlayerId().equals(ownerId)) {
            // Чужой игрок вошёл в замок
            if (invader == null) {
                invader = player;
                System.out.println(player.getName() + " начал захват замка!");
                if (player.isAnotherPlayer()) {
                    return;//если вошел комп, то возвращаемся
                } else {
                    onCastleCaptured();//если вошел человек, то заходим сюда
                }
            }
        } else {
            // Владелец вернулся в замок
            if (invader != null) {
                System.out.println("Владелец вернул контроль над замком!");
                resetInvasion();
                startBattleForCastle();
            }
        }
    }
}