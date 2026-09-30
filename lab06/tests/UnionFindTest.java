import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.fail;

public class UnionFindTest {

    /**
     * Checks that the initial state of the disjoint sets are correct (this will pass with the skeleton
     * code, but ensure it still passes after all parts are implemented).
     */
    @Test
    @DisplayName("Initial state test")
    public void initialStateTest() {
        UnionFind uf = new UnionFind(4);
        assertThat(uf.connected(0, 1)).isFalse();
        assertThat(uf.connected(0, 2)).isFalse();
        assertThat(uf.connected(0, 3)).isFalse();
        assertThat(uf.connected(1, 2)).isFalse();
        assertThat(uf.connected(1, 3)).isFalse();
        assertThat(uf.connected(2, 3)).isFalse();
    }

    /**
     * Checks that invalid inputs are handled correctly.
     */
    @Test
    @DisplayName("Illegal find test")
    public void illegalFindTest() {
        UnionFind uf = new UnionFind(4);
        try {
            uf.find(10);
            fail("Cannot find an out of range vertex!");
        } catch (IllegalArgumentException e) {
            // Expected.
        }
        try {
            uf.union(1, 10);
            fail("Cannot union with an out of range vertex!");
        } catch (IllegalArgumentException e) {
            // Expected.
        }
    }

    /**
     * Checks that union is done correctly (including the tie-breaking scheme).
     */
    @Test
    @DisplayName("Basic union test")
    public void basicUnionTest() {
        UnionFind uf = new UnionFind(10);
        uf.union(0, 1);
        assertThat(uf.find(0)).isEqualTo(1);
        uf.union(2, 3);
        assertThat(uf.find(2)).isEqualTo(3);
        uf.union(0, 2);
        assertThat(uf.find(1)).isEqualTo(3);

        uf.union(4, 5);
        uf.union(6, 7);
        uf.union(8, 9);
        uf.union(4, 8);
        uf.union(4, 6);

        assertThat(uf.find(5)).isEqualTo(9);
        assertThat(uf.find(7)).isEqualTo(9);
        assertThat(uf.find(8)).isEqualTo(9);

        uf.union(9, 2);
        assertThat(uf.find(3)).isEqualTo(9);
    }

    /**
     * Unions the same item with itself. Calls on find and checks that the outputs are correct.
     */
    @Test
    @DisplayName("Same union test")
    public void sameUnionTest() {
        UnionFind uf = new UnionFind(4);
        uf.union(1, 1);
        for (int i = 0; i < 4; i += 1) {
            assertThat(uf.find(i)).isEqualTo(i);
        }
    }

    /**
     * Checks that find compresses the path from each item it is called on to the root.
     */
    @Test
    @DisplayName("Path compression test")
    public void pathCompressionTest() {
        UnionFind uf = new UnionFind(8);
        uf.union(0, 1);
        uf.union(2, 3);
        uf.union(4, 5);
        uf.union(6, 7);

        uf.union(0, 2);
        uf.union(5, 7);
        uf.union(7, 3);

        String errorMsg = "Union calls that have been made, in this order: \n";
        errorMsg += "union(0, 1)\nunion(2, 3)\nunion(4, 5)\nunion(6, 7)\nunion(0, 2)\nunion(5, 7)\nunion(7, 3)\n";

        String methodCalls = "";
        assertWithMessage(errorMsg + "\nfind(0) returns incorrect output\n").that(uf.find(0)).isEqualTo(3);

        methodCalls += "\nMethod calls that have been made up this point:\n";
        methodCalls += "find(0)\n";

        assertWithMessage(errorMsg + methodCalls + "\nfind(4) returns incorrect output\n").that(uf.find(4)).isEqualTo(3);
        methodCalls += "find(4)\n";

        assertWithMessage(errorMsg + methodCalls + "\nfind(6) returns incorrect output\n").that(uf.find(6)).isEqualTo(3);
        methodCalls += "find(6)\n";

        for (int i = 0; i < 8; i++) {
            if (i != 3) {
                assertWithMessage(errorMsg + methodCalls + "\nparent(" + i + ") returns incorrect output\n")
                        .that(uf.parent(i)).isEqualTo(3);
            } else {
                assertWithMessage(errorMsg + methodCalls + "\nparent(" + i + ") returns incorrect output\n")
                        .that(uf.parent(i)).isEqualTo(-8);
            }
        }
    }

    /**
     * Checks that union (which calls find) also compresses paths.
     */
    @Test
    @DisplayName("Union path compression test")
    public void unionPathCompressionTest() {
        UnionFind uf = new UnionFind(8);
        String errorMsg = " Union calls that have been made, in this order: \n";

        uf.union(0, 1);
        uf.union(2, 3);
        uf.union(0, 2);

        uf.union(4, 5);
        uf.union(6, 7);
        uf.union(4, 6);
        uf.union(0, 4);

        errorMsg += "union(0, 1)\nunion(2, 3)\nunion(0, 2)\nunion(4, 5)\nunion(6, 7)\nunion(4, 6)\nunion(0, 4)\n";

        assertWithMessage(errorMsg + "\nparent(0) returns incorrect output\n").that(uf.parent(0)).isEqualTo(3);
        assertWithMessage(errorMsg + "\nparent(1) returns incorrect output\n").that(uf.parent(1)).isEqualTo(3);
        assertWithMessage(errorMsg + "\nparent(2) returns incorrect output\n").that(uf.parent(2)).isEqualTo(3);
        assertWithMessage(errorMsg + "\nparent(3) returns incorrect output\n").that(uf.parent(3)).isEqualTo(7);

        assertWithMessage(errorMsg + "\nparent(4) returns incorrect output\n").that(uf.parent(4)).isEqualTo(7);
        assertWithMessage(errorMsg + "\nparent(5) returns incorrect output\n").that(uf.parent(5)).isEqualTo(7);
        assertWithMessage(errorMsg + "\nparent(6) returns incorrect output\n").that(uf.parent(6)).isEqualTo(7);

        uf.union(0, 4);
        errorMsg += "More union calls have been made, in this order: \n";
        errorMsg += "union(0, 4)\n";
        assertWithMessage(errorMsg + "\nparent(0) returns incorrect output\n").that(uf.parent(0)).isEqualTo(7);

        uf.union(1, 5);
        errorMsg += "union(1, 5)\n";
        assertWithMessage(errorMsg + "\nparent(1) returns incorrect output\n").that(uf.parent(1)).isEqualTo(7);

        uf.union(2, 6);
        errorMsg += "union(2, 6)\n";
        assertWithMessage(errorMsg + "\nparent(2) returns incorrect output\n").that(uf.parent(2)).isEqualTo(7);

        assertWithMessage(errorMsg + "\nparent(7) returns incorrect output\n").that(uf.parent(7)).isEqualTo(-8);
        assertWithMessage(errorMsg + "\nsizeOf(7) returns incorrect output\n").that(uf.sizeOf(7)).isEqualTo(8);
    }

    /**
     * Makes calls to every method between and after unioning items.
     */
    @Test
    @DisplayName("Complete test of all methods")
    public void completeMethodTest() {
        UnionFind uf = new UnionFind(5);
        String errorMsg = "This test makes calls to all your methods between and after unioning items. Make sure to test your methods locally!";
        errorMsg += " Union calls that have been made, in this order: \n";

        uf.union(0, 1);
        errorMsg += "union(0, 1)\n";

        assertWithMessage(errorMsg + "\nfind(1) returns incorrect output\n").that(uf.find(1)).isEqualTo(1);
        assertWithMessage(errorMsg + "\nfind(0) returns incorrect output\n").that(uf.find(0)).isEqualTo(1);
        assertWithMessage(errorMsg + "\nsizeOf(1) returns incorrect output\n").that(uf.sizeOf(1)).isEqualTo(2);
        assertWithMessage(errorMsg + "\nsizeOf(0) returns incorrect output\n").that(uf.sizeOf(0)).isEqualTo(2);
        assertWithMessage(errorMsg + "\nconnected(0, 1) returns incorrect output\n").that(uf.connected(0, 1)).isTrue();
        assertWithMessage(errorMsg + "\nconnected(0, 2) returns incorrect output\n").that(uf.connected(0, 2)).isFalse();

        uf.union(3, 2);
        errorMsg += "union(3, 2)\n";
        assertWithMessage(errorMsg + "\nfind(2) returns incorrect output\n").that(uf.find(2)).isEqualTo(2);
        assertWithMessage(errorMsg + "\nfind(3) returns incorrect output\n").that(uf.find(3)).isEqualTo(2);
        assertWithMessage(errorMsg + "\nsizeOf(2) returns incorrect output\n").that(uf.sizeOf(2)).isEqualTo(2);
        assertWithMessage(errorMsg + "\nsizeOf(3) returns incorrect output\n").that(uf.sizeOf(3)).isEqualTo(2);
        assertWithMessage(errorMsg + "\nconnected(0, 1) returns incorrect output\n").that(uf.connected(0, 1)).isTrue();
        assertWithMessage(errorMsg + "\nconnected(3, 2) returns incorrect output\n").that(uf.connected(3, 2)).isTrue();
        assertWithMessage(errorMsg + "\nconnected(0, 2) returns incorrect output\n").that(uf.connected(0, 2)).isFalse();

        uf.union(2, 1);
        errorMsg += "union(2, 1)\n";
        assertWithMessage(errorMsg + "\nparent(3) returns incorrect output\n").that(uf.parent(3)).isEqualTo(2);
        assertWithMessage(errorMsg + "\nfind(3) returns incorrect output\n").that(uf.find(3)).isEqualTo(1);
        assertWithMessage(errorMsg + "\nparent(2) returns incorrect output\n").that(uf.parent(2)).isEqualTo(1);
        assertWithMessage(errorMsg + "\nfind(2) returns incorrect output\n").that(uf.find(2)).isEqualTo(1);
        assertWithMessage(errorMsg + "\nsizeOf(2) returns incorrect output\n").that(uf.sizeOf(2)).isEqualTo(4);
        assertWithMessage(errorMsg + "\nsizeOf(1) returns incorrect output\n").that(uf.sizeOf(1)).isEqualTo(4);
        assertWithMessage(errorMsg + "\nsizeOf(0) returns incorrect output\n").that(uf.sizeOf(0)).isEqualTo(4);
        assertWithMessage(errorMsg + "\nsizeOf(3) returns incorrect output\n").that(uf.sizeOf(3)).isEqualTo(4);
        assertWithMessage(errorMsg + "\nconnected(0, 1) returns incorrect output\n").that(uf.connected(0, 1)).isTrue();
        assertWithMessage(errorMsg + "\nconnected(3, 2) returns incorrect output\n").that(uf.connected(3, 2)).isTrue();
        assertWithMessage(errorMsg + "\nconnected(0, 2) returns incorrect output\n").that(uf.connected(0, 2)).isTrue();

        uf.union(0, 4);
        uf.union(4, 0);
        errorMsg += "union(0, 4)\n";
        errorMsg += "union(4, 0)\n";
        assertWithMessage(errorMsg + "\nfind(0) returns incorrect output\n").that(uf.find(0)).isEqualTo(1);
        assertWithMessage(errorMsg + "\nfind(4) returns incorrect output\n").that(uf.find(4)).isEqualTo(1);
        assertWithMessage(errorMsg + "\nfind(3) returns incorrect output\n").that(uf.find(3)).isEqualTo(1);
        assertWithMessage(errorMsg + "\nsizeOf(2) returns incorrect output\n").that(uf.sizeOf(2)).isEqualTo(5);
        assertWithMessage(errorMsg + "\nsizeOf(4) returns incorrect output\n").that(uf.sizeOf(4)).isEqualTo(5);

        for (int i = 0; i < 5; i++) {
            for (int j = i; j < 5; j++) {
                assertWithMessage("Fully connected disjoint set not showing connected for all!").that(uf.connected(i, j)).isTrue();
            }
        }
    }

    /**
     * Write your own tests below here. The given tests cover the lab's requirements, but writing a
     * small test of your own (and drawing out the array on paper) is the fastest way to find out
     * why one of the tests above is failing.
     */

}
