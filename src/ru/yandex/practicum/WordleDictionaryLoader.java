package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {

    private static final String FILE_NAME = "words_ru.txt";
    private static final int WORD_LENGTH = 5;

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary readingTheDictionary() throws IOException {
        List<String> words = new ArrayList<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (is == null) {
                throw new IOException("Файл словаря не найден в ресурсах: " + FILE_NAME);
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String word = normalize(line);
                    if (isValidWord(word)) {
                        words.add(word);
                    }
                }
            }
        }

        log.println("Словарь загружен, слов: " + words.size());
        return new WordleDictionary(words);
    }

    private String normalize(String word) {
        return word.trim().toLowerCase().replace('ё', 'е');
    }

    private boolean isValidWord(String word) {
        if (word.length() != WORD_LENGTH) {
            return false;
        }
        for (int i = 0; i < word.length(); i++) {
            if (!Character.isLetter(word.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}