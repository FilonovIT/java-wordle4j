package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private static final int MAX_STEPS = 6;

    private final String answer;
    private final WordleDictionary dictionary;
    private final List<String> userInputs;
    private final PrintWriter log;

    public WordleGame(WordleDictionary dict, PrintWriter log) {
        if (dict == null || dict.size() == 0) {
            throw new IllegalArgumentException("Словарь пуст, невозможно начать игру");
        }
        this.dictionary = dict;
        this.log = log;
        this.answer = dict.getRandomWord();
        if (this.answer == null) {
            throw new IllegalStateException("Не удалось загадать слово");
        }
        this.userInputs = new ArrayList<>();
        log.println("Новая игра. Длина слова: " + answer.length());
    }

    WordleGame(WordleDictionary dict, String answer, PrintWriter log) {
        if (dict == null || dict.size() == 0) {
            throw new IllegalArgumentException("Словарь пуст, невозможно начать игру");
        }
        if (answer == null || answer.length() != 5) {
            throw new IllegalArgumentException("Ответ должен состоять из 5 букв");
        }
        this.dictionary = dict;
        this.log = Objects.requireNonNull(log, "log не должен быть null");
        this.answer = WordleDictionary.normalize(answer);
        this.userInputs = new ArrayList<>();
        log.println("Новая игра. Длина слова: " + this.answer.length());
    }

    /**
     * Проверяет введённое слово и возвращает подсказку в формате Wordle:
     * '+' — буква на своём месте
     * '^' — буква есть в слове, но не на этом месте
     * '-' — буквы нет в слове
     */
    public String checkWord(String userWord) throws GameException {
        String normalized = WordleDictionary.normalize(userWord);
        validateWord(normalized);

        userInputs.add(normalized);
        String mask = buildMask(normalized, answer);
        log.println("Ход #" + userInputs.size() + ": " + normalized + " -> " + mask);
        return mask;
    }

    /**
     * Проверяет, является ли слово корректным для игры
     */
    private void validateWord(String word) throws GameException {
        if (word == null || word.isEmpty()) {
            throw new InvalidWordException("Слово не может быть пустым");
        }
        if (word.length() != answer.length()) {
            throw new InvalidWordException(
                    "Слово должно состоять из " + answer.length() + " букв");
        }
        if (!dictionary.contains(word)) {
            throw new WordNotFoundInDictionaryException(
                    "Слово '" + word + "' отсутствует в словаре");
        }
    }

    /**
     * Возвращает подсказку — слово из словаря, подходящее под все ранее введённые слова.
     * Возвращает null, если подходящих слов не осталось.
     */
    public String getHint() {
        List<String> candidates = new ArrayList<>();

        for (String word : dictionary.getWords()) {
            if (word.equals(answer)) {
                continue;
            }
            if (userInputs.contains(word)) {
                continue;
            }
            if (matchesAllPreviousAttempts(word)) {
                candidates.add(word);
            }
        }

        if (candidates.isEmpty()) {
            log.println("Подсказка: подходящих слов не осталось");
            return null;
        }

        String hint = candidates.get((int) (Math.random() * candidates.size()));
        log.println("Подсказка: " + hint);
        return hint;
    }

    /**
     * Проверяет, согласуется ли слово со всеми предыдущими попытками пользователя.
     * Кандидат подходит, если для каждой попытки он дал бы ту же маску, что была показана.
     */
    private boolean matchesAllPreviousAttempts(String candidate) {
        for (String input : userInputs) {
            String actualMask = buildMask(input, answer);
            String candidateMask = buildMask(input, candidate);
            if (!actualMask.equals(candidateMask)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Строит маску для введённого слова относительно заданного слова-ответа.
     * Корректно обрабатывает повторяющиеся буквы.
     */
    private String buildMask(String input, String target) {
        char[] result = new char[target.length()];
        Map<Character, Integer> remaining = new HashMap<>();

        for (int i = 0; i < target.length(); i++) {
            remaining.merge(target.charAt(i), 1, Integer::sum);
        }

        // первый проход: точные совпадения
        for (int i = 0; i < target.length(); i++) {
            if (input.charAt(i) == target.charAt(i)) {
                result[i] = '+';
                remaining.merge(input.charAt(i), -1, Integer::sum);
            }
        }

        // второй проход: буква есть, но не на своём месте
        for (int i = 0; i < target.length(); i++) {
            if (result[i] != 0) {
                continue;
            }
            char c = input.charAt(i);
            if (remaining.getOrDefault(c, 0) > 0) {
                result[i] = '^';
                remaining.merge(c, -1, Integer::sum);
            } else {
                result[i] = '-';
            }
        }

        return new String(result);
    }

    public boolean isGameOver() {
        return isWin() || userInputs.size() >= MAX_STEPS;
    }

    public boolean isWin() {
        return !userInputs.isEmpty()
                && userInputs.get(userInputs.size() - 1).equals(answer);
    }

    public int getSteps() {
        return userInputs.size();
    }

    public int getMaxSteps() {
        return MAX_STEPS;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getUserInputs() {
        return new ArrayList<>(userInputs);
    }
}