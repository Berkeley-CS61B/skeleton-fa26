import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Formatter;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntListRequiredTests {

    /**
     * Returns an IntList of the given integers.
     */
    public static IntList of(int... nums) {
        IntList L = new IntList(nums[0], null);
        IntList current = L;
        for (int i = 1; i < nums.length; i++) {
            current.rest = new IntList(nums[i], null);
            current = current.rest;
        }
        return L;
    }

    /**
     * Returns true if two IntLists are equal.
     */
    public static boolean checkEquals(IntList L1, IntList L2) {
        if (L1 == null && L2 == null) {
            return true;
        }
        if (L1 == null || L2 == null) {
            return false;
        }
        if (L1.first != L2.first) {
            return false;
        }
        return checkEquals(L1.rest, L2.rest);
    }

    /**
     * If a cycle exists in the IntList, this method
     * returns an integer equal to the item number of the location where the
     * cycle is detected.
     */
    private int detectCycles(IntList A) {
        IntList tortoise = A;
        IntList hare = A;

        if (A == null)
            return 0;

        int cnt = 0;


        while (true) {
            cnt++;
            if (hare.rest != null)
                hare = hare.rest.rest;
            else
                return 0;

            tortoise = tortoise.rest;

            if (tortoise == null || hare == null)
                return 0;

            if (hare == tortoise)
                return cnt;
        }
    }

    /** Outputs the IntList as a String. You are not expected to read
     * or understand this method. */
    private String intListToString(IntList L) {
        if (L == null) {
            return "()";
        }

        Formatter out = new Formatter();
        String sep;
        sep = "(";
        int cycleLocation = detectCycles(L);
        int cnt = 0;

        for (IntList p = L; p != null; p = p.rest) {
            out.format("%s%d", sep, p.first);
            sep = ", ";

            cnt++;
            if ((cnt > cycleLocation) && (cycleLocation > 0)) {
                out.format("... (cycle exists) ...");
                break;
            }
        }
        out.format(")");
        return out.toString();
    }

    @Test
    @Order(1)
    @DisplayName("Test incrRecursiveDestructive correctness")
    public void testIncrRecursiveDestructive() {
        IntList L = of(1, 2, 3);
        IntList expected = of(5, 6, 7);
        IntList result = IntList.incrRecursiveDestructive(L, 4);

        if (!checkEquals(result, expected)) {
            String errorMessage = String.format("For input %s, expected incrRecursiveDestructive to return %s but got %s", intListToString(L), intListToString(expected), intListToString(result));
            fail(errorMessage);
        }

        if (!checkEquals(L, of(5, 6, 7))) {
            String errorMessage = String.format("For input %s, expected L after call to incrRecursiveDestructive to be %s but got %s", intListToString(L), intListToString(of(1, 2, 3)), intListToString(L));
            fail(errorMessage);
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test first five numbers")
    public void testFirstFiveNumbers() {
        // To avoid leaking the answer, we will just check that the sum and product
        // of the first five numbers is correct. The autograder will check that you
        // have the exact correct numbers.
        int expectedSum = 758;
        long expectedProduct = 6929521200L;

        int[] actual = IntListMystery.firstFiveNumbers();
        int actualSum = actual[0] + actual[1] + actual[2] + actual[3] + actual[4];
        long actualProduct = (long) actual[0] * actual[1] * actual[2] * actual[3] * actual[4];

        if (actualSum != expectedSum || actualProduct != expectedProduct) {
            fail("Test failed: First five numbers are not correct.");
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test middle number")
    public void testMiddleNumber() {
        // To avoid leaking the answer, we will just check that the number mod N is correct
        // for various Ns. The autograder will check that you have the exact correct number.
        int expectedMod7 = 1;
        int expectedMod11 = 7;
        int expectedMod13 = 10;
        int expectedMod31 = 26;
        int expectedMod37 = 10;

        int actual = IntListMystery.middleNumber();
        boolean correctMods = (actual % 7 == expectedMod7) &&
                (actual % 11 == expectedMod11) &&
                (actual % 13 == expectedMod13) &&
                (actual % 31 == expectedMod31) &&
                (actual % 37 == expectedMod37);

        if (!correctMods) {
            fail("Test failed: Middle number is not correct.");
        }
    }
}
