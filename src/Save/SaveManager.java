package Save;

import UI.GameMap;
import Player.*;
import Castle.Castle;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class SaveManager {
    private static final String SAVES_DIRECTORY = "saves";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static boolean testMode = false; // Режим тестирования (отключает мини-игру)
    
    static {
        File savesDir = new File(SAVES_DIRECTORY);
        if (!savesDir.exists()) {
            savesDir.mkdir();
        }
    }
    
    // Метод для включения/отключения тестового режима
    public static void setTestMode(boolean enabled) {
        testMode = enabled;
    }

    //File.separator - кросс-платформенное решение
    public static String saveGame(HumanPlayer player, ComputerPlayer computer, GameMap gameMap, List<Castle> castles, boolean isAutoSave, int turnsLeft) {
        String userDir = SAVES_DIRECTORY + File.separator + player.getName();
        File userDirectory = new File(userDir);
        if (!userDirectory.exists()) {
            userDirectory.mkdir();
        }
        
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String saveFileName = (isAutoSave ? "auto_" : "manual_") + timestamp + ".sav";
        File saveFile = new File(userDir, saveFileName);//поддиректория

        //ObjectOutputStream - поток для сериализации объектов
        //FileOutputStream - поток для записи в файл
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            GameSave gameSave = new GameSave(player, computer, gameMap, castles, timestamp, isAutoSave, turnsLeft);
            
            // Если это ручное сохранение и не в режиме тестирования, предлагаем мини-игру 21
            if (!isAutoSave && !testMode) {
                System.out.println("Предлагаем сыграть в мини-игру 21 для получения бонуса!");
                System.out.println("Хотите сыграть? (1-Да, 2-Нет)");
                
                Scanner scanner = new Scanner(System.in);
                int playChoice = getIntInput(scanner, "Ваш выбор: ");
                
                if (playChoice == 1) {
                    Game21 game = new Game21();
                    String winner = game.play();
                    int bonus = game.calculateBonus(winner);
                    
                    gameSave.setBonus(bonus);
                    gameSave.setBonusWinner(winner);
                    
                    System.out.println("Бонус в размере " + bonus + " золота будет начислен " +
                                       (winner.equals("player") ? "вам" : "компьютеру") + 
                                       " при следующей загрузке!");
                }
            }
            
            oos.writeObject(gameSave);
            
            System.out.println("Игра успешно сохранена: " + saveFileName);
            return saveFileName;
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении игры: " + e.getMessage());
            return "";
        }
    }
    
    private static int getIntInput(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                int value = Integer.parseInt(input);
                if (value == 1 || value == 2) {
                    return value;
                }
                System.out.println("Пожалуйста, введите 1 или 2.");
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число.");
            }
        }
    }
    
    public static GameSave loadGame(String playerName, String saveFileName) {
        String userDir = SAVES_DIRECTORY + File.separator + playerName;
        File saveFile = new File(userDir, saveFileName);
        
        if (!saveFile.exists()) {
            System.err.println("Сохранение не найдено: " + saveFile.getPath());
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            GameSave gameSave = (GameSave) ois.readObject();
            System.out.println("Игра успешно загружена: " + saveFileName);
            
            // Если есть бонус от мини-игры, выводим информацию
            if (gameSave.getBonus() > 0) {
                String winner = gameSave.getBonusWinner();
                System.out.println("Обнаружен бонус от мини-игры: " + gameSave.getBonus() + 
                                   " золота будет начислено " + 
                                   (winner.equals("player") ? "игроку" : "компьютеру"));
            }
            
            return gameSave;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке игры: " + e.getMessage());
            return null;
        }
    }
    
    public static List<String> getSavesByPlayer(String playerName) {
        List<String> saveFiles = new ArrayList<>();
        String userDir = SAVES_DIRECTORY + File.separator + playerName;
        File userDirectory = new File(userDir);
        
        if (userDirectory.exists() && userDirectory.isDirectory()) {
            File[] files = userDirectory.listFiles((dir, name) -> name.endsWith(".sav"));
            
            if (files != null) {
                for (File file : files) {
                    saveFiles.add(file.getName());
                }
            }
        }
        return saveFiles;
    }
    
    public static String autoSave(HumanPlayer player, ComputerPlayer computer, GameMap gameMap, List<Castle> castles, int turnsLeft) {
        return saveGame(player, computer, gameMap, castles, true, turnsLeft);
    }
    
    public static String manualSave(HumanPlayer player, ComputerPlayer computer, GameMap gameMap, List<Castle> castles, int turnsLeft) {
        return saveGame(player, computer, gameMap, castles, false, turnsLeft);
    }
    
    public static void cleanupOldAutoSaves(String playerName, int keepCount) {
        String userDir = SAVES_DIRECTORY + File.separator + playerName;
        File userDirectory = new File(userDir);
        
        if (userDirectory.exists() && userDirectory.isDirectory()) {
            File[] autoSaves = userDirectory.listFiles((dir, name) -> name.startsWith("auto_") && name.endsWith(".sav"));
            
            if (autoSaves != null && autoSaves.length > keepCount) {
                java.util.Arrays.sort(autoSaves, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                
                for (int i = keepCount; i < autoSaves.length; i++) {
                    autoSaves[i].delete();
                }
            }
        }
    }
} 