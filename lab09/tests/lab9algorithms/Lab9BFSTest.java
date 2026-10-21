package lab9algorithms;

import lab9lib.Lab9Graph;
import lab9main.Main;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Lab9BFSTest {

    @Test
    @DisplayName("Single node: reachable from itself, order is [solo]")
    public void tiny() {
        Lab9BFS b = new Lab9BFS(Lab9DFSTest.single(), "solo");
        assertThat(b.reachable("solo")).isTrue();
        assertThat(b.order()).containsExactly("solo").inOrder();
    }

    @Test
    @DisplayName("Lecture tree: DBFACEG from D, ABDCFEG from A")
    public void lectureTreeOrders() {
        assertThat(new Lab9BFS(Lab9DFSTest.lectureTree(), "D").order())
                .containsExactly("D", "B", "F", "A", "C", "E", "G").inOrder();
        assertThat(new Lab9BFS(Lab9DFSTest.lectureTree(), "A").order())
                .containsExactly("A", "B", "D", "C", "F", "E", "G").inOrder();
    }

    @Test
    @DisplayName("Lecture tree: distTo counts edges from the start")
    public void lectureTreeDistances() {
        Lab9BFS fromD = new Lab9BFS(Lab9DFSTest.lectureTree(), "D");
        assertThat(fromD.distTo("D")).isEqualTo(0);
        assertThat(fromD.distTo("B")).isEqualTo(1);
        assertThat(fromD.distTo("F")).isEqualTo(1);
        assertThat(fromD.distTo("A")).isEqualTo(2);
        assertThat(fromD.distTo("G")).isEqualTo(2);
        Lab9BFS fromA = new Lab9BFS(Lab9DFSTest.lectureTree(), "A");
        assertThat(fromA.distTo("A")).isEqualTo(0);
        assertThat(fromA.distTo("C")).isEqualTo(2);
        assertThat(fromA.distTo("G")).isEqualTo(4);
    }

    @Test
    @DisplayName("Cycle: every node visited exactly once")
    public void cycleOrder() {
        Lab9BFS b = new Lab9BFS(Lab9DFSTest.cycle(), "a");
        assertThat(b.order()).containsExactly("a", "b", "c", "d").inOrder();
        assertThat(b.distTo("c")).isEqualTo(1);   // via the a-c edge, not a-b-c
        assertThat(b.distTo("d")).isEqualTo(2);
    }

    @Test
    @DisplayName("Disconnected graph: only the reachable component is visited")
    public void disconnectedOrder() {
        Lab9BFS fromX = new Lab9BFS(Lab9DFSTest.disconnected(), "x");
        assertThat(fromX.order()).containsExactly("x").inOrder();
        assertThat(fromX.reachable("x")).isTrue();
        assertThat(fromX.reachable("p")).isFalse();
        assertThat(fromX.distTo("x")).isEqualTo(0);
        assertThat(fromX.distTo("p")).isEqualTo(-1);
        Lab9BFS fromQ = new Lab9BFS(Lab9DFSTest.disconnected(), "q");
        assertThat(fromQ.order()).containsExactly("q", "p").inOrder();
        assertThat(fromQ.reachable("p")).isTrue();
        assertThat(fromQ.reachable("y")).isFalse();
        assertThat(fromQ.distTo("p")).isEqualTo(1);
        assertThat(fromQ.distTo("y")).isEqualTo(-1);
    }

    @Test
    @DisplayName("City graph from Berkeley")
    public void city() {
        Lab9BFS b = new Lab9BFS(Main.cityGraph(), "Berkeley");
        assertThat(b.order()).containsExactly(
                "Berkeley", "Sacramento", "Los Angeles", "Portland", "Las Vegas", "Phoenix",
                "Seattle", "Salt Lake City", "Denver", "Dallas", "Chicago", "Atlanta", "New York",
                "Boston").inOrder();
        for (String v : Main.cityGraph().nodes()) {
            assertThat(b.reachable(v)).isTrue();
        }
        assertThat(b.distTo("Berkeley")).isEqualTo(0);
        assertThat(b.distTo("Los Angeles")).isEqualTo(1);
        assertThat(b.distTo("Phoenix")).isEqualTo(2);
        assertThat(b.distTo("Boston")).isEqualTo(6);   // fewest edges, not fewest miles
    }

    @Test
    @DisplayName("Every node appears exactly once from any start, with start first")
    public void everyNodeOnce() {
        Lab9Graph g = Main.cityGraph();
        for (String start : List.of("Berkeley", "Chicago", "Boston")) {
            Lab9BFS b = new Lab9BFS(g, start);
            assertThat(b.order()).containsExactlyElementsIn(g.nodes());
            assertThat(b.order().get(0)).isEqualTo(start);
        }
    }

    @Test
    @DisplayName("Start node and query arguments must be in the graph")
    public void badArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Lab9BFS(Lab9DFSTest.path(), "z"));
        Lab9BFS b = new Lab9BFS(Lab9DFSTest.path(), "a");
        assertThrows(IllegalArgumentException.class, () -> b.reachable("z"));
        assertThrows(IllegalArgumentException.class, () -> b.distTo("z"));
    }
}
