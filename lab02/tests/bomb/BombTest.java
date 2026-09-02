package bomb;

import edu.princeton.cs.algs4.In;
import org.junit.jupiter.api.*;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.fail;

public class BombTest {
    // DO NOT MODIFY THIS FILE
    // You won't be able to find any passwords here, sorry!
    public static final String BOMB_FILE = "src/bomb/Bomb.java";

    @Test
    @Tag("phase0")
    @DisplayName("Bomb Phase 0")
    public void testBombPhase0() {
        checkIfModified();
        Bomb b = new Bomb();
        BombMain.answers(b, 0);
        assertWithMessage("Phase 0 went BOOM!").that(b.isDefused(0)).isTrue();
    }

    @Test
    @Tag("phase1")
    @DisplayName("Bomb Phase 1")
    public void testBombPhase1() {
        checkIfModified();
        Bomb b = new Bomb();
        BombMain.answers(b, 1);
        assertWithMessage("Phase 1 went BOOM!").that(b.isDefused(1)).isTrue();
    }

    @Test
    @Tag("phase2")
    @DisplayName("Bomb Phase 2")
    public void testBombPhase2() {
        checkIfModified();
        Bomb b = new Bomb();
        BombMain.answers(b, 2);
        assertWithMessage("Phase 2 went BOOM!").that(b.isDefused(2)).isTrue();
    }

    private static void checkIfModified() {
        String[] contents = new In(BOMB_FILE).readAllLines();
        if (String.join("cheese", contents).hashCode() % 891 != 209) {
            fail("Bomb.java has been modified. Please restore it to the original version.");
        }
    }
}
