package syntaxexercises;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class MapExercisesTest {
    @Test
    @DisplayName("countWords: counts each word")
    public void testCountWords() {
        Map<String, Integer> counts = MapExercises.countWords(List.of("hi", "bye", "hi"));
        assertThat(counts).containsExactly("hi", 2, "bye", 1);
    }

    @Test
    @DisplayName("countWords: empty list gives empty map")
    public void testCountWordsEmpty() {
        assertThat(MapExercises.countWords(List.of())).isEmpty();
    }

    @Test
    @DisplayName("countWords: repeated word")
    public void testCountWordsAllSame() {
        assertThat(MapExercises.countWords(List.of("a", "a", "a", "a"))).containsExactly("a", 4);
    }
}
