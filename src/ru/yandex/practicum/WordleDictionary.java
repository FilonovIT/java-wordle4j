package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private static final int WORD_LENGTH = 5;

    private final List<String> words;

    public WordleDictionary(List<String> dictionary) {
        Objects.requireNonNull(dictionary, "Словарь не должен быть равен null.");
        List<String> filtered = new ArrayList<>();
        for (String raw : dictionary) {
            if (raw == null) {
                continue;
            }
            String word = normalize(raw);
            if (word.length() == WORD_LENGTH) {
                filtered.add(word);
            }
        }
        this.words = List.copyOf(filtered);
    }

    public static String normalize(String word) {
        return word == null
                ? null
                : word.trim().toLowerCase(Locale.ROOT).replace('ё', 'е');
    }

    public List<String> getWords() {
        return words;
    }

    public int size() {
        return words.size();
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст");
        }
        return words.get(ThreadLocalRandom.current().nextInt(words.size()));
    }
}