package Save;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Game21 {
    //deck-колода
    private List<Integer> deck;
    private List<Integer> playerHand;
    private List<Integer> computerHand;
    
    public Game21() {
        initializeDeck();
        playerHand = new ArrayList<>();
        computerHand = new ArrayList<>();
    }
    
    private void initializeDeck() {
        deck = new ArrayList<>();
        // Создаем колоду карт с номиналами от 1 до 10 по 4 штуки каждого номинала (4 масти)
        for (int i = 1; i <= 10; i++) {
            for (int j = 0; j < 4; j++) {
                deck.add(i);
            }
        }
        // Перемешиваем колоду
        Collections.shuffle(deck);
    }
    
    private int drawCard() {
        if (deck.isEmpty()) {
            System.out.println("Колода закончилась! Игра завершается.");
            return 0; // Возвращаем 0, если колода пуста
        }
        // Берем верхнюю карту из колоды
        return deck.remove(0);
    }

    //Преобразуем каждый Integer в примитивный int
    //Integer::intValue - это сокращение для: x -> x.intValue()
    
    private int calculateHandValue(List<Integer> hand) {
        return hand.stream().mapToInt(Integer::intValue).sum();
    }
    
    private void displayHand(String player, List<Integer> hand) {
        int sum = calculateHandValue(hand);
        System.out.print(player + " карты: ");
        for (Integer card : hand) {
            System.out.print(card + " ");
        }
        System.out.println("(Сумма: " + sum + ")");
    }
    
    public String play() {
        System.out.println("\n===== МИНИ-ИГРА 21 =====");
        System.out.println("Выиграйте, чтобы получить бонус при следующей загрузке!");
        System.out.println("В колоде 40 карт (4 масти × 10 карт от 1 до 10)");
        
        // Раздаем начальные карты(по 2 карты)
        playerHand.add(drawCard());
        playerHand.add(drawCard());
        computerHand.add(drawCard());
        computerHand.add(drawCard());
        
        Scanner scanner = new Scanner(System.in);
        boolean playerTurn = true;
        
        // Ход игрока
        while (playerTurn) {
            displayHand("Ваши", playerHand);
            displayHand("Компьютера", computerHand);
            System.out.println("Осталось карт в колоде: " + deck.size());
            
            int playerSum = calculateHandValue(playerHand);
            if (playerSum > 21) {
                System.out.println("Перебор! Вы проиграли.");
                return "computer";
            }
            
            System.out.println("1. Взять еще карту");
            System.out.println("2. Остановиться");
            
            int choice;
            while (true) {
                try {
                    System.out.print("Ваш выбор: ");
                    choice = Integer.parseInt(scanner.nextLine());
                    if (choice == 1 || choice == 2) {
                        break;
                    }
                    System.out.println("Пожалуйста, введите 1 или 2.");
                } catch (NumberFormatException e) {
                    System.out.println("Пожалуйста, введите число.");
                }
            }
            
            if (choice == 1) {
                int card = drawCard();
                if (card == 0) { // Колода закончилась
                    System.out.println("Игра завершается досрочно. Определяем победителя по текущим картам.");
                    playerTurn = false;
                } else {
                    playerHand.add(card);
                }
            } else {
                playerTurn = false;
            }
        }
        
        // Ход компьютера
        System.out.println("\nХод компьютера:");
        while (true) {
            int computerSum = calculateHandValue(computerHand);
            displayHand("Компьютера", computerHand);
            System.out.println("Осталось карт в колоде: " + deck.size());
            
            if (computerSum > 21) {
                System.out.println("Компьютер перебрал! Вы выиграли!");
                return "player";
            }
            
            // Компьютер перестает брать карты, когда до 21 остается менее 10 очков
            if (21 - computerSum < 10) {
                break;
            }
            
            // Компьютер берет карту
            System.out.println("Компьютер берет карту...");
            int card = drawCard();
            if (card == 0) { // Колода закончилась
                System.out.println("Игра завершается досрочно. Определяем победителя по текущим картам.");
                break;
            }
            computerHand.add(card);
        }
        
        // Определяем победителя
        int playerSum = calculateHandValue(playerHand);
        int computerSum = calculateHandValue(computerHand);
        
        System.out.println("\nИтоговые карты:");
        displayHand("Ваши", playerHand);
        displayHand("Компьютера", computerHand);
        
        if (playerSum > 21) {
            System.out.println("Вы перебрали! Компьютер выиграл.");
            return "computer";
        } else if (computerSum > 21) {
            System.out.println("Компьютер перебрал! Вы выиграли.");
            return "player";
        } else if (playerSum > computerSum) {
            System.out.println("Вы выиграли!");
            return "player";
        } else if (computerSum > playerSum) {
            System.out.println("Компьютер выиграл!");
            return "computer";
        } else {
            System.out.println("Ничья! Победа компьютера по правилам казино.");
            return "computer";
        }
    }
    
    // Метод для определения размера бонуса в зависимости от суммы карт
    public int calculateBonus(String winner) {
        int winnerSum;
        if (winner.equals("player")) {
            winnerSum = calculateHandValue(playerHand);
        } else {
            winnerSum = calculateHandValue(computerHand);
        }
        
        // Базовый бонус плюс дополнительный бонус в зависимости от суммы карт
        return 50 + (winnerSum * 5);
    }
} 