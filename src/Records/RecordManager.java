package Records;

import java.io.*;
import java.util.*;

public class RecordManager {
    private static final String RECORDS_DIR = "records";
    private static final String RECORDS_FILE = "records.txt";
    private static final int MAX_RECORDS_PER_PLAYER = 1;
    private static final int TOP_RECORDS_TO_DISPLAY = 5; 
    
    private final String recordsDir;
    private final String recordsFile;
    private final List<GameRecord> records;
    
    public RecordManager() {
        this(RECORDS_DIR, RECORDS_FILE);
    }
    
    public RecordManager(String recordsDir, String recordsFile) {
        this.recordsDir = recordsDir;
        this.recordsFile = recordsFile;
        this.records = new ArrayList<>();
        loadRecords();
    }
    
    public boolean addRecord(GameRecord record) {
        //есть ли он уже в списке
        GameRecord existingRecord = getPlayerRecord(record.getPlayerName());
        if (existingRecord != null) {
            if (record.getScore() > existingRecord.getScore()) {
                records.remove(existingRecord);
                records.add(record);
                //сортировку списка рекордов (records) по убыванию количества очков
                records.sort((r1, r2) -> Integer.compare(r2.getScore(), r1.getScore()));
                saveRecords();
                return true;
            }
            return false;
        }
        
        records.add(record);
        records.sort((r1, r2) -> Integer.compare(r2.getScore(), r1.getScore()));
        saveRecords();
        return true;
    }
    
    public List<GameRecord> getTopRecords(int count) {
        return records.subList(0, Math.min(count, records.size()));
    }
    
    public List<GameRecord> getTopRecords() {
        return getTopRecords(TOP_RECORDS_TO_DISPLAY);
    }

    //Фильтрует поток, оставляя только записи, где имя игрока совпадает с искомым
    public GameRecord getPlayerRecord(String playerName) {
        return records.stream()
            .filter(record -> record.getPlayerName().equals(playerName))
            .findFirst()
            .orElse(null);
    }
    //.filter() - фильтрует элементы потока по условию
    //
    //.findFirst() - возвращает первый элемент, соответствующий фильтру
    //
    //.orElse() - определяет значение по умолчанию, если элемент не найден
    
    private void loadRecords() {
        File dir = new File(recordsDir);
        if (!dir.exists()) {
            dir.mkdirs();
            return;
        }
        
        File file = new File(dir, recordsFile);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String playerName = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    String mapName = parts[2];
                    String creator = parts[3];
                    records.add(new GameRecord(playerName, score, mapName, creator));
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке рекордов: " + e.getMessage());
        }
        
        records.sort((r1, r2) -> Integer.compare(r2.getScore(), r1.getScore()));
    }
    
    private void saveRecords() {
        File dir = new File(recordsDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        File file = new File(dir, recordsFile);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (GameRecord record : records) {
                writer.println(String.format("%s,%d,%s,%s",
                    record.getPlayerName(),
                    record.getScore(),
                    record.getMapName(),
                    record.getMapCreator()));
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении рекордов: " + e.getMessage());
        }
    }
    
    public void displayTopRecords() {
        List<GameRecord> topRecords = getTopRecords();
        
        if (topRecords.isEmpty()) {
            System.out.println("Пока нет доступных рекордов.");
            return;
        }
        
        System.out.println("\n=== ЛУЧШИЕ РЕКОРДЫ ===");
        for (int i = 0; i < topRecords.size(); i++) {
            GameRecord record = topRecords.get(i);
            System.out.printf("%d. %s%n", i + 1, record);
        }
        System.out.println("==================");
    }
} 