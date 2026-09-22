import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Comparator;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestWordComparators {

    // -------- getxComparator (counts lowercase 'x') --------

    @Test
    @Order(0)
    @DisplayName("Test getXComparator with same number of xs")
    public void testGetXEqualCounts() {
        Comparator<String> cmp = WordComparators.getXComparator();
        int result = cmp.compare("abc", "def"); // both have 0 'x'
        assertThat(result).isEqualTo(0);
    }

    @Test
    @Order(1)
    @DisplayName("Test getXComparator with fewer xs in first arg")
    public void testGetXMoreInFirst() {
        Comparator<String> cmp = WordComparators.getXComparator();
        int result = cmp.compare("xx", "x"); // 2 - 1 = 1
        assertThat(result).isGreaterThan(0);
    }

    @Test
    @Order(2)
    @DisplayName("Test getXComparator with fewer xs in first arg")
    public void testGetXFewerInFirst() {
        Comparator<String> cmp = WordComparators.getXComparator();
        int result = cmp.compare("x", "xx"); // 1 - 2 = -1
        assertThat(result).isLessThan(0);
    }

    @Test
    @Order(3)
    @DisplayName("Test getXComparator is case sensitive")
    public void testGetXCaseSensitive() {
        Comparator<String> cmp = WordComparators.getXComparator();
        int result = cmp.compare("Xx", "xx"); // 1 - 2 = -1
        assertThat(result).isLessThan(0);
    }

    @Test
    @Order(4)
    @DisplayName("Test getXComparator with first string empty")
    public void testGetXEmptyVsSome() {
        Comparator<String> cmp = WordComparators.getXComparator();
        int result = cmp.compare("", "x"); // 0 - 1 = -1
        assertThat(result).isLessThan(0);
    }

    // -------- getCharComparator(c) --------

    @Test
    @Order(5)
    @DisplayName("Test getCharComparator('a') with more as in first arg")
    public void testGetCharMoreInFirst() {
        Comparator<String> cmp = WordComparators.getCharComparator('a');
        int result = cmp.compare("caramel", "almond"); // 2 - 1 = 1
        assertThat(result).isGreaterThan(0);
    }

    @Test
    @Order(6)
    @DisplayName("Test getCharComparator('a') with equals as")
    public void testGetCharTieIsZero() {
        Comparator<String> cmp = WordComparators.getCharComparator('a');
        int result = cmp.compare("aba", "baa"); // both 2 'a'
        assertThat(result).isEqualTo(0);
    }

    @Test
    @Order(7)
    @DisplayName("Test getCharComparator('y') with zero ys in first arg ")
    public void testGetCharZeroVsSome() {
        Comparator<String> cmp = WordComparators.getCharComparator('y');
        int result = cmp.compare("zzz", "y"); // 0 - 1 = -1
        assertThat(result).isLessThan(0);
    }

    @Test
    @Order(8)
    @DisplayName("Test getCharComparator('z') with zero zs in either arg")
    public void testGetCharNeitherHasChar() {
        Comparator<String> cmp = WordComparators.getCharComparator('z');
        int result = cmp.compare("alpha", "beta"); // 0 - 0 = 0
        assertThat(result).isEqualTo(0);
    }

    // -------- getCharListComparator(chars) --------

    @Test
    @Order(9)
    @DisplayName("Test getCharListComparator(vowels) with more in first arg")
    public void testGetCharListMoreInFirstVowels() {
        Comparator<String> cmp = WordComparators.getCharListComparator(List.of('a', 'e', 'i', 'o', 'u'));
        int result = cmp.compare("education", "sky"); // 5 - 0 = 5
        assertThat(result).isGreaterThan(0);
    }

    @Test
    @Order(10)
    @DisplayName("Test getCharListComparator(vowels) with zero vowels in either arg")
    public void testGetCharListTieIsZeroVowels() {
        Comparator<String> cmp = WordComparators.getCharListComparator(List.of('a', 'e', 'i', 'o', 'u'));
        int result = cmp.compare("paper", "beta"); // 2 - 2 = 0
        assertThat(result).isEqualTo(0);
    }

    @Test
    @Order(11)
    @DisplayName("Test getCharListComparator(vowels) with more vowels in second arg")
    public void testGetCharListZeroVsSomeVowels() {
        Comparator<String> cmp = WordComparators.getCharListComparator(List.of('a', 'e', 'i', 'o', 'u'));
        int result = cmp.compare("myth", "a"); // 0 - 1 = -1
        assertThat(result).isLessThan(0);
    }

    @Test
    @Order(12)
    @DisplayName("Test getCharListComparator(['q','z']) with no qs or zs in either arg")
    public void testGetCharListNeitherHasChars() {
        Comparator<String> cmp = WordComparators.getCharListComparator(List.of('q', 'z'));
        int result = cmp.compare("alpha", "beta"); // 0 - 0 = 0
        assertThat(result).isEqualTo(0);
    }

    @Test
    @Order(14)
    @DisplayName("Test getCharListComparator([]) considers all strings equal")
    public void testGetCharListEmptyListAlwaysTie() {
        Comparator<String> cmp = WordComparators.getCharListComparator(List.of());
        int result1 = cmp.compare("aeiou", "");
        int result2 = cmp.compare("xyz", "bbb");
        assertThat(result1).isEqualTo(0);
        assertThat(result2).isEqualTo(0);
    }
}
