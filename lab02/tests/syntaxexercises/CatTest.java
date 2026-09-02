package syntaxexercises;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class CatTest {
    @Test
    @DisplayName("Cat: isHeavierThan")
    public void testIsHeavierThan() {
        Cat mochi = new Cat("Mochi", 12);
        Cat tofu = new Cat("Tofu", 9);
        assertThat(mochi.isHeavierThan(tofu)).isTrue();
        assertThat(tofu.isHeavierThan(mochi)).isFalse();
        assertThat(mochi.isHeavierThan(mochi)).isFalse();
    }

    @Test
    @DisplayName("Cat: totalWeight")
    public void testTotalWeight() {
        Cat mochi = new Cat("Mochi", 12);
        Cat tofu = new Cat("Tofu", 9);
        assertThat(Cat.totalWeight(mochi, tofu)).isEqualTo(21);
        assertThat(Cat.totalWeight(tofu, tofu)).isEqualTo(18);
    }

    @Test
    @DisplayName("Cat: main prints true then 21")
    public void testMain() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            Cat.main();
        } finally {
            System.setOut(originalOut);
        }
        List<String> lines = captured.toString().lines().toList();
        assertThat(lines).containsExactly("true", "21").inOrder();
    }
}
