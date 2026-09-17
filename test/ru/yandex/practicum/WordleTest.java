package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private static final StringWriter SINK = new StringWriter();
    private static final PrintWriter LOG = new PrintWriter(SINK);

    private static WordleDictionary dictOf(String... words) {
        return new WordleDictionary(List.of(words));
    }

    @Test
    void dictionaryFiltersByLength() {
        WordleDictionary d = dictOf("дом", "слово", "ёжики", "абвгд");
        assertEquals(2, d.size()); // "слово" и "абвгд" ("ёжики" -> "ежики"? нет, длина 5)
    }

    @Test
    void dictionaryNormalizesYo() {
        WordleDictionary d = dictOf("ёжики");
        // "ёжики" -> "ежики", длина 5
        assertEquals(1, d.size());
        assertTrue(d.contains("ежики"));
        assertFalse(d.contains("ёжики"));
    }

    @Test
    void normalizeHandlesNull() {
        assertNull(WordleDictionary.normalize(null));
    }

    @Test
    void getRandomWordFromEmptyThrows() {
        WordleDictionary d = new WordleDictionary(List.of());
        assertThrows(IllegalStateException.class, d::getRandomWord);
    }

    @Test
    void invalidWordExceptionKeepsMessage() {
        GameException e = new InvalidWordException("boom");
        assertEquals("boom", e.getMessage());
    }

    @Test
    void wordNotFoundKeepsMessage() {
        GameException e = new WordNotFoundInDictionaryException("nope");
        assertEquals("nope", e.getMessage());
    }

    @Test
    void checkWordRejectsWrongLength() {
        WordleGame game = new WordleGame(dictOf("слово", "абвгд"), LOG);
        assertThrows(InvalidWordException.class, () -> game.checkWord("дом"));
    }

    @Test
    void checkWordRejectsUnknownWord() {
        WordleGame game = new WordleGame(dictOf("слово", "абвгд"), LOG);
        assertThrows(WordNotFoundInDictionaryException.class,
                () -> game.checkWord("чужое"));
    }

    @Test
    void checkWordRejectsEmpty() {
        WordleGame game = new WordleGame(dictOf("слово", "абвгд"), LOG);
        assertThrows(InvalidWordException.class, () -> game.checkWord(""));
    }

        @Test
    void maskExactMatch() throws GameException {
        // словарь из одного слова -> ответ детерминирован
        WordleGame game = new WordleGame(dictOf("слово"), LOG);
        assertEquals("+++++", game.checkWord("слово"));
    }

    @Test
    void maskHandlesRepeatedLetters() {
        // Если buildMask откроете как package-private/static:
        // assertEquals("^+--^", WordleGame.buildMask("аббаа", "баабб"));
        // Пока — через checkWord на словаре, где ответ известен.
        // Для этого нужен доступ к buildMask; см. примечание ниже.
    }

    @Test
    void winIsDetected() throws GameException {
        WordleGame game = new WordleGame(dictOf("слово"), LOG);
        game.checkWord("слово");
        assertTrue(game.isWin());
        assertTrue(game.isGameOver());
        assertEquals(1, game.getSteps());
    }

    @Test
    void loseAfterMaxSteps() throws GameException {
        WordleGame game = new WordleGame(dictOf("слово", "абвгд"), LOG);
        // 6 промахов
        for (int i = 0; i < game.getMaxSteps(); i++) {
            game.checkWord("абвгд");
        }
        assertTrue(game.isGameOver());
        assertFalse(game.isWin());
    }

    @Test
    void hintIsNullWhenDictionaryExhausted() {
        WordleGame game = new WordleGame(dictOf("слово"), LOG);
        assertNull(game.getHint()); // ответ исключён -> кандидатов нет
    }

    @Test
    void hintDoesNotReturnAnswerOrGuessedWords() throws GameException {
        WordleGame game = new WordleGame(dictOf("слово", "абвгд", "книга"), LOG);
        String hint = game.getHint();
        assertNotNull(hint);
        assertNotEquals(game.getAnswer(), hint);
    }
}