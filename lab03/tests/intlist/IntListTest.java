package intlist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class IntListTest {
    @Test
    @DisplayName("replaceID (destructive, iterative) replaces all occurrences")
    public void testReplace() {
        IntList L = IntList.of(1, 2, 1, 3, 1);
        IntList.replaceID(L, 1, 5);
        assertThat(L).isEqualTo(IntList.of(5, 2, 5, 3, 5));
    }

    @Test
    @DisplayName("replaceID leaves a list without the value untouched")
    public void testReplaceAbsentValue() {
        IntList L = IntList.of(4, 5, 6);
        IntList.replaceID(L, 7, 100);
        assertThat(L).isEqualTo(IntList.of(4, 5, 6));
    }

    @Test
    @DisplayName("replaceRND returns a modified copy")
    public void testReplaceNonDestructive() {
        IntList L = IntList.of(1, 2, 1, 3);
        IntList result = IntList.replaceRND(L, 1, 9);
        assertThat(result).isEqualTo(IntList.of(9, 2, 9, 3));
    }

    @Test
    @DisplayName("replaceRND does not modify the original list")
    public void testReplaceNonDestructiveIsNonDestructive() {
        IntList L = IntList.of(1, 2, 1, 3);
        IntList.replaceRND(L, 1, 9);
        assertThat(L).isEqualTo(IntList.of(1, 2, 1, 3));
    }

    @Test
    @DisplayName("replaceRD (destructive) replaces all occurrences")
    public void testReplaceRecursive() {
        IntList L = IntList.of(8, 1, 8, 8, 2);
        IntList.replaceRD(L, 8, 0);
        assertThat(L).isEqualTo(IntList.of(0, 1, 0, 0, 2));
    }

    @Test
    @DisplayName("stairify: [2, 5, 3] becomes [2, 3, 4, 5, 4, 3]")
    public void testStairifyBrokenStair() {
        IntList L = IntList.of(2, 5, 3).stairify();
        assertThat(L).isEqualTo(IntList.of(2, 3, 4, 5, 4, 3));
    }

    @Test
    @DisplayName("stairify: [2, 4, 4, 3] becomes [2, 3, 4, 4, 3]")
    public void testStairifyPartiallyBroken() {
        IntList L = IntList.of(2, 4, 4, 3).stairify();
        assertThat(L).isEqualTo(IntList.of(2, 3, 4, 4, 3));
    }

    @Test
    @DisplayName("stairify does nothing to a list that is already a stair")
    public void testStairifyAlreadyStair() {
        IntList L = IntList.of(2, 3, 4, 5).stairify();
        assertThat(L).isEqualTo(IntList.of(2, 3, 4, 5));
    }

    @Test
    @DisplayName("stairify works going downhill with negative numbers")
    public void testStairifyNegatives() {
        IntList L = IntList.of(-4, -1).stairify();
        assertThat(L).isEqualTo(IntList.of(-4, -3, -2, -1));
    }
}
