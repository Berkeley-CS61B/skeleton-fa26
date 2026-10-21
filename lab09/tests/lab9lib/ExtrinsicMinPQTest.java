package lab9lib;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Random;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** ExtrinsicMinPQ is provided for you; these tests just show how it behaves. */
public class ExtrinsicMinPQTest {

    @Test
    @DisplayName("Items come out in priority order")
    public void basicOrder() {
        ExtrinsicMinPQ pq = new ExtrinsicMinPQ();
        pq.add("c", 3);
        pq.add("a", 1);
        pq.add("d", 4);
        pq.add("b", 2);
        assertThat(pq.size()).isEqualTo(4);
        assertThat(pq.getSmallest()).isEqualTo("a");
        assertThat(pq.removeSmallest()).isEqualTo("a");
        assertThat(pq.removeSmallest()).isEqualTo("b");
        assertThat(pq.removeSmallest()).isEqualTo("c");
        assertThat(pq.removeSmallest()).isEqualTo("d");
        assertThat(pq.size()).isEqualTo(0);
        assertThrows(NoSuchElementException.class, pq::removeSmallest);
    }

    @Test
    @DisplayName("changePriority moves items up and down")
    public void changePriority() {
        ExtrinsicMinPQ pq = new ExtrinsicMinPQ();
        pq.add("x", 10);
        pq.add("y", 20);
        pq.add("z", 30);
        pq.changePriority("z", 5);
        assertThat(pq.getSmallest()).isEqualTo("z");
        pq.changePriority("z", 25);
        assertThat(pq.removeSmallest()).isEqualTo("x");
        assertThat(pq.removeSmallest()).isEqualTo("y");
        assertThat(pq.removeSmallest()).isEqualTo("z");
        assertThrows(NoSuchElementException.class, () -> pq.changePriority("nope", 1));
    }

    @Test
    @DisplayName("Duplicates are rejected, contains works")
    public void duplicates() {
        ExtrinsicMinPQ pq = new ExtrinsicMinPQ();
        pq.add("x", 1);
        assertThat(pq.contains("x")).isTrue();
        assertThat(pq.contains("y")).isFalse();
        assertThrows(IllegalArgumentException.class, () -> pq.add("x", 2));
    }

    @Test
    @DisplayName("Random operations agree with a slow reference")
    public void randomized() {
        Random r = new Random(61);
        ExtrinsicMinPQ pq = new ExtrinsicMinPQ();
        java.util.Map<String, Integer> ref = new java.util.HashMap<>();
        for (int step = 0; step < 2000; step++) {
            int op = r.nextInt(3);
            String x = "n" + r.nextInt(50);
            if (op == 0 && !ref.containsKey(x)) {
                int p = r.nextInt(1000);
                pq.add(x, p);
                ref.put(x, p);
            } else if (op == 1 && ref.containsKey(x)) {
                int p = r.nextInt(1000);
                pq.changePriority(x, p);
                ref.put(x, p);
            } else if (op == 2 && !ref.isEmpty()) {
                int min = ref.values().stream().min(Integer::compare).get();
                String out = pq.removeSmallest();
                assertThat(ref.get(out)).isEqualTo(min);
                ref.remove(out);
            }
            assertThat(pq.size()).isEqualTo(ref.size());
        }
    }
}
