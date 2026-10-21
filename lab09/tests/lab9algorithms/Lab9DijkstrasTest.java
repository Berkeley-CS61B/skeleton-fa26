package lab9algorithms;

import lab9lib.Lab9Graph;
import lab9main.Main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Lab9DijkstrasTest {

    /** a-b-c-d is a chain of weight-1 edges, a-d is a heavy shortcut (10),
     *  b-d a medium one (5), e hangs off a, and z is unreachable. */
    private static Lab9Graph small() {
        Lab9Graph g = new Lab9Graph();
        g.addEdge("a", "b", 1);
        g.addEdge("b", "c", 1);
        g.addEdge("c", "d", 1);
        g.addEdge("a", "d", 10);
        g.addEdge("b", "d", 5);
        g.addEdge("a", "e", 3);
        g.addNode("z");
        return g;
    }

    @Test
    @DisplayName("Source has distance 0 and path [source]")
    public void source() {
        Lab9Dijkstras d = new Lab9Dijkstras(small(), "a");
        assertThat(d.hasPathTo("a")).isTrue();
        assertThat(d.distTo("a")).isEqualTo(0);
        assertThat(d.shortestPathTo("a")).containsExactly("a").inOrder();
    }

    @Test
    @DisplayName("Fewest edges is not the same as shortest path")
    public void weightsMatter() {
        Lab9Dijkstras d = new Lab9Dijkstras(small(), "a");
        // a-d directly costs 10, a-b-d costs 6, but a-b-c-d costs only 3.
        assertThat(d.distTo("d")).isEqualTo(3);
        assertThat(d.shortestPathTo("d")).containsExactly("a", "b", "c", "d").inOrder();
        assertThat(d.distTo("b")).isEqualTo(1);
        assertThat(d.distTo("c")).isEqualTo(2);
        assertThat(d.distTo("e")).isEqualTo(3);
        assertThat(d.shortestPathTo("e")).containsExactly("a", "e").inOrder();
    }

    @Test
    @DisplayName("Unreachable node: hasPathTo false, queries throw")
    public void unreachable() {
        Lab9Dijkstras d = new Lab9Dijkstras(small(), "a");
        assertThat(d.hasPathTo("z")).isFalse();
        assertThrows(IllegalArgumentException.class, () -> d.distTo("z"));
        assertThrows(IllegalArgumentException.class, () -> d.shortestPathTo("z"));
    }

    @Test
    @DisplayName("Different source, same graph")
    public void otherSource() {
        Lab9Dijkstras d = new Lab9Dijkstras(small(), "d");
        assertThat(d.distTo("a")).isEqualTo(3);
        assertThat(d.shortestPathTo("a")).containsExactly("d", "c", "b", "a").inOrder();
        assertThat(d.distTo("e")).isEqualTo(6);
        assertThat(d.shortestPathTo("e")).containsExactly("d", "c", "b", "a", "e").inOrder();
    }

    @Test
    @DisplayName("Source must be in the graph")
    public void badSource() {
        assertThrows(IllegalArgumentException.class, () -> new Lab9Dijkstras(small(), "nope"));
    }

    @Test
    @DisplayName("City graph from Berkeley")
    public void cityGraph() {
        Lab9Dijkstras d = new Lab9Dijkstras(Main.cityGraph(), "Berkeley");
        assertThat(d.distTo("Seattle")).isEqualTo(835);
        assertThat(d.shortestPathTo("Seattle"))
                .containsExactly("Berkeley", "Sacramento", "Portland", "Seattle").inOrder();
        assertThat(d.distTo("Denver")).isEqualTo(1570);
        assertThat(d.shortestPathTo("Denver"))
                .containsExactly("Berkeley", "Los Angeles", "Phoenix", "Denver").inOrder();
        assertThat(d.distTo("Chicago")).isEqualTo(2570);
        assertThat(d.distTo("Atlanta")).isEqualTo(2595);
        assertThat(d.distTo("Boston")).isEqualTo(3575);
        assertThat(d.shortestPathTo("Boston")).containsExactly("Berkeley", "Los Angeles", "Phoenix",
                "Denver", "Chicago", "New York", "Boston").inOrder();
        for (String v : Main.cityGraph().nodes()) {
            assertThat(d.hasPathTo(v)).isTrue();
        }
    }
}
