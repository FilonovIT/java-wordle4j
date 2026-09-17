package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Wordle {

    private static final String LOG_FILE = "wordle.log";

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            log.println("=== Новая игра ===");

            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            WordleDictionary dict = loader.readingTheDictionary();

            if (dict.size() == 0) {
                System.out.println("Словарь пуст, игра невозможна.");
                return;
            }

            WordleGame game = new WordleGame(dict, log);

            System.out.println("Загадано слово из " + game.getAnswer().length()
                    + " букв. У вас " + game.getMaxSteps() + " попыток.");
            System.out.println("Введите слово или пустую строку для подсказки.");

            play(game, log);

            if (game.isWin()) {
                System.out.println("Победа! Слово: " + game.getAnswer()
                        + ". Попыток: " + game.getSteps());
                log.println("Победа за " + game.getSteps() + " попыток");
            } else {
                System.out.println("Проигрыш. Загаданное слово: " + game.getAnswer());
                log.println("Проигрыш. Ответ: " + game.getAnswer());
            }
        } catch (IOException e) {
            System.err.println("Не удалось открыть лог-файл: " + e.getMessage());
        }
    }

    private static void play(WordleGame game, PrintWriter log) {
        Scanner scanner = new Scanner(System.in);

        while (!game.isGameOver() && !game.isWin()) {
            System.out.print("Попытка " + (game.getSteps() + 1) + ": ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine().trim().toLowerCase()
                    .replace('ё', 'е');

            if (input.isEmpty()) {
                String hint = game.getHint();
                System.out.println(hint == null
                        ? "Подходящих слов не осталось."
                        : "Подсказка: " + hint);
                log.println("Подсказка: " + hint);
                continue;
            }

            try {
                String mask = game.checkWord(input);
                System.out.println(mask);
                log.println("Ход: " + input + " -> " + mask);
            } catch (GameException e) {
                System.out.println("Ошибка: " + e.getMessage());
                log.println("Отклонено: " + input + " (" + e.getMessage() + ")");
            }
        }
    }
}