import Battle.Battle;
import Castle.Castle;
import Building.Building;
import Hero.Hero;
import Map.Map;
import Map.MapEditor;
import Map.MapStorage;
import UI.Cursor;
import UI.GameMap;
import UI.Cell;
import Records.ScoreCalculator;
import Records.GameRecord;
import Unit.Unit;
import Player.*;
import Save.*;
import Records.RecordManager;
import Building.NPCManager;
import Building.TimeManager;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static RecordManager recordManager = new RecordManager();
    private static NPCManager npcManager; // Менеджер NPC
    private static boolean timeManagerStarted = false; // Флаг для отслеживания состояния TimeManager
    
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("===== ДОБРО ПОЖАЛОВАТЬ В HEROES OF IU3 =====");
        System.out.print("Введите ваше имя: ");
        String playerName = scanner.nextLine();

        while (true){
            System.out.println("\n===== ГЛАВНОЕ МЕНЮ =====");
            System.out.println("1. Запустить новую игру");
            System.out.println("2. Загрузить сохранение");
            System.out.println("3. Открыть редактор карт");
            System.out.println("4. Просмотреть рекорды");
            System.out.println("5. Выход");
            System.out.println("===========================================");

            int choice = getIntInput(scanner, "Выберите действие: ");

            switch (choice) {
                case 1:
                    startNewGame(scanner, playerName);
                    break;
                case 2:
                    loadGame(scanner, playerName);
                    break;
                case 3:
                    MapEditor editor = new MapEditor(playerName);
                    editor.start();
                    break;
                case 4:
                    recordManager.displayTopRecords();
                    break;
                case 5:
                    System.out.println("До свидания!");
                    return;
                default:
                    System.out.println("Неверный выбор. Пожалуйста, выберите действие из меню.");
            }
        }
    }
    
    private static void loadGame(Scanner scanner, String playerName) {
        List<String> saves = SaveManager.getSavesByPlayer(playerName);
        if (saves.isEmpty()) {
            System.out.println("У вас нет сохранений.");
            return;
        }
        
        System.out.println("\n===== ДОСТУПНЫЕ СОХРАНЕНИЯ =====");
        for (int i = 0; i < saves.size(); i++) {
            System.out.println((i + 1) + ". " + saves.get(i));
        }
        
        int saveChoice = getIntInput(scanner, "Выберите сохранение для загрузки (0 для отмены): ");
        if (saveChoice == 0 || saveChoice > saves.size()) {
            return;
        }
        
        GameSave gameSave = SaveManager.loadGame(playerName, saves.get(saveChoice - 1));
        if (gameSave != null) {
            continueGame(gameSave);
        }
    }
    
    private static void continueGame(GameSave gameSave) {
        HumanPlayer player = (HumanPlayer) gameSave.getPlayer();
        ComputerPlayer computer = gameSave.getComputer();
        GameMap gameMap = gameSave.getGameMap();
        List<Castle> castles = gameSave.getCastles();
        int turnsLeft = gameSave.getTurnsLeft();
        
        // Инициализируем NPC и игровое время
        initializeNPCs(gameMap);
        
        // Применяем бонус от мини-игры 21 (если есть)
        if (gameSave.getBonus() > 0) {
            String winner = gameSave.getBonusWinner();
            int bonus = gameSave.getBonus();
            
            if (winner.equals("player")) {
                player.AddGold(bonus);
                System.out.println("Вы получили бонус в размере " + bonus + " золота!");
            } else {
                computer.AddGold(bonus);
                System.out.println("Компьютер получил бонус в размере " + bonus + " золота!");
            }
        }
        
        ScoreCalculator scoreCalculator = new ScoreCalculator(gameMap.getWidth() * gameMap.getHeight());
        
        boolean gameEnded = false;
        Player winner = null;
        boolean returnToMainMenu = false;
        
        while (turnsLeft > 0 && !gameEnded && !returnToMainMenu) {
            gameMap.getOasis().resetOasis();
            for (Castle castle : castles) {
                castle.processInvasion();
            }
            
            for (Player p : new Player[]{player, computer}) {
                p.getActiveHero().resetMovementRange();
                System.out.printf("Герой игрока %s восстановил энергию: %d/%d%n",
                        p.getName(),
                        p.getActiveHero().getMovementRange(),
                        p.getActiveHero().getMaxMovementRange());
            }
            
            // Предложение ручного сохранения игры
            System.out.println("\n=== МЕНЮ ДЕЙСТВИЙ ===");
            System.out.println("1. Продолжить игру");
            System.out.println("2. Сохранить игру");
            System.out.println("3. Разместить здание");
            System.out.println("4. Вернуться в главное меню");
            System.out.println("=====================");
            
            Scanner scanner = new Scanner(System.in);
            int choice = getIntInput(scanner, "Выберите действие: ");
            
            if (choice == 2) {
                manualSaveGame(player, computer, gameMap, castles, turnsLeft);
                continue;
            } else if (choice == 3) {
                placeBuilding(scanner, gameMap, player, computer);
                continue;
            } else if (choice == 4) {
                System.out.println("Возврат в главное меню...");
                // Предлагаем сохранить игру перед выходом
                System.out.println("Сохранить игру перед выходом? (1-Да, 2-Нет)");
                int saveChoice = getIntInput(scanner, "Ваш выбор: ");
                if (saveChoice == 1) {
                    manualSaveGame(player, computer, gameMap, castles, turnsLeft);
                }
                returnToMainMenu = true;
                continue;
            }
            
            for (int i = 0; i < 2; i++) {
                if (returnToMainMenu) break;
                
                Player currentPlayer = i == 0 ? player : computer;
                Player otherPlayer = i == 0 ? computer : player;
                ArrayList<Hero> restHeroes = new ArrayList<>();
                restHeroes.add(otherPlayer.getActiveHero());
                
                gameMap.Display(currentPlayer.getActiveHero(), restHeroes, player, computer);
                
                if (currentPlayer instanceof HumanPlayer) {
                    ((HumanPlayer)currentPlayer).moveHero(currentPlayer.getActiveHero(), restHeroes, otherPlayer, gameMap);
                } else {
                    ((ComputerPlayer)currentPlayer).computerMove(currentPlayer.getActiveHero(), otherPlayer.getActiveHero(), 
                            otherPlayer.getCastle(), otherPlayer, gameMap);
                }
                
                Castle crossedCastle = gameMap.getCrossedCastle(currentPlayer.getActiveHero());
                if (crossedCastle != null) {
                    crossedCastle.enterCastle(currentPlayer);
                }
                
                currentPlayer.AddGold(10);
                
                if (otherPlayer.getCastle().isDestroyed()) {
                    gameEnded = true;
                    winner = currentPlayer;
                    break;
                }
            }
            
            turnsLeft--;
            
            // Auto-save after each turn
            SaveManager.autoSave(player, computer, gameMap, castles, turnsLeft);
            SaveManager.cleanupOldAutoSaves(player.getName(), 5); // Keep only 5 most recent auto-saves
        }
        
        if (returnToMainMenu) {
            cleanupGameResources();
            return; // Возвращаемся в главное меню без отображения результатов игры
        }
        
        if (!gameEnded && turnsLeft <= 0) {
            winner = player.getGold() >= computer.getGold() ? player : computer;
        }
        
        if (winner == player) {
            scoreCalculator.addUnitsDestroyed(computer.getActiveHero().getArmy().size());
            scoreCalculator.addBuildingsConstructed(1);
        } else {
            scoreCalculator.addUnitsDestroyed(player.getActiveHero().getArmy().size());
        }
        
        scoreCalculator.addResourcesCollected(player.getGold());
        
        int finalScore = scoreCalculator.calculateFinalScore();
        
        System.out.println("\n===== РЕЗУЛЬТАТЫ ИГРЫ =====");
        System.out.println("Победитель: " + winner.getName());
        System.out.println("Время игры: " + scoreCalculator.getGameTimeSeconds() + " секунд");
        System.out.println("Уничтожено юнитов: " + scoreCalculator.getUnitsDestroyed());
        System.out.println("Собрано ресурсов: " + scoreCalculator.getResourcesCollected());
        System.out.println("Построено зданий: " + scoreCalculator.getBuildingsConstructed());
        System.out.println("Итоговый счет: " + finalScore);
        
        GameRecord record = new GameRecord(player.getName(), finalScore, gameMap.getName(), gameMap.getCreator());
        boolean isNewRecord = recordManager.addRecord(record);
        
        if (isNewRecord) {
            System.out.println("Поздравляем! Вы установили новый рекорд!");
        } else {
            System.out.println("К сожалению, это не новый рекорд.");
        }
        
        recordManager.displayTopRecords();
        
        // Очистка ресурсов игры
        cleanupGameResources();
    }
    
    private static void startNewGame(Scanner scanner, String playerName) {
        System.out.println("\n===== ВЫБОР КАРТЫ =====");
        System.out.println("1. Использовать стандартную карту");
        System.out.println("2. Выбрать пользовательскую карту");
        System.out.println("=========================");
        
        int mapChoice = getIntInput(scanner, "Выберите вариант: ");
        
        Map customMap = null;
        int mapHeight = 10;
        int mapWidth = 10;
        String mapName = "Standard Map";
        String mapCreator = "System";
        
        if (mapChoice == 2) {
            List<String> mapNames = MapStorage.getAllMapNames();
            if (mapNames.isEmpty()) {
                System.out.println("Нет доступных пользовательских карт. Используется стандартная карта.");
            } else {
                System.out.println("\n===== ДОСТУПНЫЕ КАРТЫ =====");
                for (int i = 0; i < mapNames.size(); i++) {
                    System.out.println((i + 1) + ". " + mapNames.get(i));
                }
                
                int mapIndex = getIntInput(scanner, "Выберите карту (0 для стандартной): ");
                if (mapIndex > 0 && mapIndex <= mapNames.size()) {
                    String[] mapInfo = mapNames.get(mapIndex - 1).split(":");
                    if (mapInfo.length == 2) {
                        mapCreator = mapInfo[0];
                        mapName = mapInfo[1];
                        customMap = MapStorage.loadMap(mapName, mapCreator);
                        if (customMap != null) {
                            mapHeight = customMap.getHeight();
                            mapWidth = customMap.getWidth();
                            System.out.println("Загружена карта: " + mapName + " от " + mapCreator);
                        } else {
                            System.out.println("Ошибка при загрузке карты. Используется стандартная карта.");
                        }
                    }
                }
            }
        }
        
        int maxMovementRange = (int) Math.round(Math.sqrt(Math.pow(mapWidth-3, 2) + Math.pow(mapHeight-3, 2)) / 2);

        HumanPlayer humanPlayer = new HumanPlayer(new Hero(1, 1, 100, "Warrior"), "&", false, playerName);
        ComputerPlayer computer = new ComputerPlayer(new Hero(mapWidth - 2, mapHeight - 2, maxMovementRange, "Warrior"),"$",true, "Boba");

        humanPlayer.getActiveHero().addUnit(new Unit("Копейщик", 20, 2, 10, 2));
        humanPlayer.getActiveHero().addUnit(new Unit("Копейщик", 20, 2, 10, 2));
        computer.getActiveHero().addUnit(new Unit("Копейщик", 2, 2, 2, 2));
        computer.getActiveHero().addUnit(new Unit("Копейщик", 2, 2, 2, 2));

        Castle playerCastle = new Castle(humanPlayer, 0, 0);
        Castle computerCastle = new Castle(computer, mapWidth - 1, mapHeight - 1);
        humanPlayer.setCastle(playerCastle);
        computer.setCastle(computerCastle);

        List<Castle> castles = Arrays.asList(playerCastle, computerCastle);

        ScoreCalculator scoreCalculator = new ScoreCalculator(mapWidth * mapHeight);


        GameMap gameMap;
        if (customMap != null) {
            gameMap = new GameMap(customMap, castles.toArray(new Castle[0]), humanPlayer, computer);
        } else {
            gameMap = new GameMap(mapHeight, mapWidth, castles.toArray(new Castle[0]), humanPlayer, computer);
        }
        
        // Инициализируем NPC и игровое время
        initializeNPCs(gameMap);

        int turns = 100;
        boolean gameEnded = false;
        Player winner = null;
        boolean returnToMainMenu = false;

        while (turns > 0 && !gameEnded && !returnToMainMenu) {
            gameMap.getOasis().resetOasis();
            for (Castle castle : castles) {
                castle.processInvasion();
            }
            
            for (Player p : new Player[]{humanPlayer, computer}) {
                p.getActiveHero().resetMovementRange();
                System.out.printf("Герой игрока %s восстановил энергию: %d/%d%n",
                        p.getName(),
                        p.getActiveHero().getMovementRange(),
                        p.getActiveHero().getMaxMovementRange());
            }
            
            // Предложение ручного сохранения игры
            System.out.println("\n=== МЕНЮ ДЕЙСТВИЙ ===");
            System.out.println("1. Продолжить игру");
            System.out.println("2. Сохранить игру");
            System.out.println("3. Разместить здание");
            System.out.println("4. Вернуться в главное меню");
            System.out.println("=====================");
            
            int choice = getIntInput(scanner, "Выберите действие: ");
            
            if (choice == 2) {
                manualSaveGame(humanPlayer, computer, gameMap, castles, turns);
                continue;
            } else if (choice == 3) {
                placeBuilding(scanner, gameMap, humanPlayer, computer);
                continue;
            } else if (choice == 4) {
                System.out.println("Возврат в главное меню...");
                // Предлагаем сохранить игру перед выходом
                System.out.println("Сохранить игру перед выходом? (1-Да, 2-Нет)");
                int saveChoice = getIntInput(scanner, "Ваш выбор: ");
                if (saveChoice == 1) {
                    manualSaveGame(humanPlayer, computer, gameMap, castles, turns);
                }
                returnToMainMenu = true;
                continue;
            }
            
            for (int i = 0; i < 2; i++) {
                if (returnToMainMenu) break;
                
                Player currentPlayer = i == 0 ? humanPlayer : computer;
                Player otherPlayer = i == 0 ? computer : humanPlayer;
                ArrayList<Hero> restHeroes = new ArrayList<>();
                restHeroes.add(otherPlayer.getActiveHero());
                
                gameMap.Display(currentPlayer.getActiveHero(), restHeroes, humanPlayer, computer);
                
                if (currentPlayer instanceof HumanPlayer) {
                    ((HumanPlayer)currentPlayer).moveHero(currentPlayer.getActiveHero(), restHeroes, otherPlayer, gameMap);
                } else {
                    ((ComputerPlayer)currentPlayer).computerMove(currentPlayer.getActiveHero(), otherPlayer.getActiveHero(), 
                            otherPlayer.getCastle(), otherPlayer, gameMap);
                }
                
                Castle crossedCastle = gameMap.getCrossedCastle(currentPlayer.getActiveHero());
                if (crossedCastle != null) {
                    crossedCastle.enterCastle(currentPlayer);
                }
                
                currentPlayer.AddGold(10);
                
                if (otherPlayer.getCastle().isDestroyed()) {
                    gameEnded = true;
                    winner = currentPlayer;
                    break;
                }
            }
            
            turns--;
            
            // Auto-save after each turn
            SaveManager.autoSave(humanPlayer, computer, gameMap, castles, turns);
            SaveManager.cleanupOldAutoSaves(playerName, 5); // Keep only 5 most recent auto-saves
        }
        
        if (returnToMainMenu) {
            cleanupGameResources();
            return; // Возвращаемся в главное меню без отображения результатов игры
        }
        
        if (!gameEnded && turns <= 0) {
            winner = humanPlayer.getGold() >= computer.getGold() ? humanPlayer : computer;
        }
        
        if (winner == humanPlayer) {
            scoreCalculator.addUnitsDestroyed(computer.getActiveHero().getArmy().size());
            scoreCalculator.addBuildingsConstructed(1);
        } else {
            scoreCalculator.addUnitsDestroyed(humanPlayer.getActiveHero().getArmy().size());
        }
        
        scoreCalculator.addResourcesCollected(humanPlayer.getGold());
        
        int finalScore = scoreCalculator.calculateFinalScore();
        
        System.out.println("\n===== РЕЗУЛЬТАТЫ ИГРЫ =====");
        System.out.println("Победитель: " + winner.getName());
        System.out.println("Время игры: " + scoreCalculator.getGameTimeSeconds() + " секунд");
        System.out.println("Уничтожено юнитов: " + scoreCalculator.getUnitsDestroyed());
        System.out.println("Собрано ресурсов: " + scoreCalculator.getResourcesCollected());
        System.out.println("Построено зданий: " + scoreCalculator.getBuildingsConstructed());
        System.out.println("Итоговый счет: " + finalScore);
        
        GameRecord record = new GameRecord(playerName, finalScore, mapName, mapCreator);
        boolean isNewRecord = recordManager.addRecord(record);
        
        if (isNewRecord) {
            System.out.println("Поздравляем! Вы установили новый рекорд!");
        } else {
            System.out.println("К сожалению, это не новый рекорд.");
        }
        
        recordManager.displayTopRecords();
        
        // Очистка ресурсов игры
        cleanupGameResources();
    }
    
    private static int getIntInput(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число.");
            }
        }
    }

    // Метод для ручного сохранения игры
    private static void manualSaveGame(HumanPlayer player, ComputerPlayer computer, GameMap gameMap, List<Castle> castles, int turnsLeft) {
        System.out.println("\n=== РУЧНОЕ СОХРАНЕНИЕ ИГРЫ ===");
        String saveResult = SaveManager.manualSave(player, computer, gameMap, castles, turnsLeft);
        if (!saveResult.isEmpty()) {
            System.out.println("Игра успешно сохранена!");
        } else {
            System.out.println("Ошибка при сохранении игры!");
        }
    }

    // Обновим метод placeBuilding() для добавления параметров
    private static void placeBuilding(Scanner scanner, GameMap gameMap, HumanPlayer humanPlayer, ComputerPlayer computer) {
        System.out.println("\n=== РАЗМЕЩЕНИЕ ЗДАНИЯ ===");
        
        // Показать доступные здания
        gameMap.showAvailableBuildings();
        
        int buildingChoice = getIntInput(scanner, "Выберите здание (0 для отмены): ");
        if (buildingChoice == 0) {
            return;
        }
        
        String buildingType;
        switch (buildingChoice) {
            case 1:
                buildingType = "Hotel";
                break;
            case 2:
                buildingType = "Cafe";
                break;
            case 3:
                buildingType = "Hairdresser";
                break;
            default:
                System.out.println("Неверный выбор здания!");
                return;
        }
        
        int x = getIntInput(scanner, "Введите X-координату (0 до " + (gameMap.getWidth() - 1) + "): ");
        int y = getIntInput(scanner, "Введите Y-координату (0 до " + (gameMap.getHeight() - 1) + "): ");
        
        if (x < 0 || x >= gameMap.getWidth() || y < 0 || y >= gameMap.getHeight()) {
            System.out.println("Координаты за пределами карты!");
            return;
        }
        
        if (gameMap.playerPlaceBuilding(buildingType, x, y)) {
            System.out.println("Здание успешно размещено на карте!");
            gameMap.Display(humanPlayer.getActiveHero(), new ArrayList<>(Arrays.asList(computer.getActiveHero())), humanPlayer, computer);
        } else {
            System.out.println("Не удалось разместить здание. Позиция может быть занята.");
        }
    }

    private static void initializeNPCs(GameMap gameMap) {
        // Если менеджер NPC уже создан, освобождаем ресурсы
        if (npcManager != null) {
            npcManager.shutdown();
        }
        
        // Создаем новый менеджер NPC с зданиями из карты
        npcManager = new NPCManager(gameMap.getBuildings());
        
        // Сбрасываем и запускаем TimeManager
        TimeManager timeManager = TimeManager.getInstance();
        if (timeManagerStarted) {
            // Если таймер уже запускался ранее, сначала сбрасываем его состояние
            timeManager.reset();
        }
        
        try {
            // Запускаем таймер
            timeManager.start();
            timeManagerStarted = true;
            System.out.println("В городе живут 15 NPC, которые посещают здания. Их активность можно увидеть при входе в здание.");
        } catch (Exception e) {
            System.err.println("Ошибка при запуске таймера: " + e.getMessage());
            System.out.println("Пожалуйста, перезапустите программу для корректной работы NPC.");
        }
    }

    private static void cleanupGameResources() {
        // Останавливаем менеджер NPC если он был инициализирован
        if (npcManager != null) {
            npcManager.shutdown();
            npcManager = null;
        }
        
        // Останавливаем игровое время
        if (timeManagerStarted) {
            TimeManager.getInstance().stop();
            timeManagerStarted = false;
        }
    }
}