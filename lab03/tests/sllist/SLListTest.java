package sllist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class SLListTest {
    /** Returns the SLList 5 -> 6 -> 2 from the spec's example. */
    private SLList exampleList() {
        SLList L = new SLList();
        L.addFirst(2);
        L.addFirst(6);
        L.addFirst(5);
        return L;
    }

    @Test
    @DisplayName("insert into the middle: insert(10, 1) on 5 -> 6 -> 2")
    public void testInsertMiddle() {
        SLList L = exampleList();
        L.insert(10, 1);
        assertThat(L.toArray()).asList().containsExactly(5, 10, 6, 2).inOrder();
    }

    @Test
    @DisplayName("insert at position 0 inserts at the front")
    public void testInsertFront() {
        SLList L = exampleList();
        L.insert(10, 0);
        assertThat(L.toArray()).asList().containsExactly(10, 5, 6, 2).inOrder();
    }

    @Test
    @DisplayName("insert past the end appends: insert(10, 7) on 5 -> 6 -> 2")
    public void testInsertPastEnd() {
        SLList L = exampleList();
        L.insert(10, 7);
        assertThat(L.toArray()).asList().containsExactly(5, 6, 2, 10).inOrder();
    }

    @Test
    @DisplayName("insert into an empty list")
    public void testInsertEmpty() {
        SLList L = new SLList();
        L.insert(42, 0);
        assertThat(L.toArray()).asList().containsExactly(42).inOrder();
    }

    @Test
    @DisplayName("insert updates size")
    public void testInsertUpdatesSize() {
        SLList L = exampleList();
        L.insert(10, 1);
        L.insert(11, 0);
        assertThat(L.size).isEqualTo(5);
    }
}
