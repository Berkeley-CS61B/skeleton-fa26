import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestWordFinder {
    @Test
    @Order(0)
    @DisplayName("Test findMax using an 'm' Comparator")
    public void testFindMaxOnMComparator() {
        String[] strings = {"alpha", "beta", "gamma", "delta", "epsilon"};
        String expected = "gamma";
        String actual = WordFinder.findMax(strings, WordComparators.getCharComparator('m'));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Order(1)
    @DisplayName("Test findMax using a getCharListComparator()")
    public void testFindMaxOnGetCharListComparator() {
        String[] strings = {"alpha", "beta", "gamma", "delta", "epsilon"};
        String expected = "delta";
        String actual = WordFinder.findMax(strings, WordComparators.getCharListComparator(List.of('d', 'l', 't')));

        assertThat(actual).isEqualTo(expected);
    }
}
