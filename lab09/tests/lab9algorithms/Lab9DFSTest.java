package lab9algorithms;

import lab9lib.Lab9Graph;
import lab9main.Main;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Lab9DFSTest {

    /** The tree from lecture: D with children B (A, C) and F (E, G). */
    static Lab9Graph lectureTree() {
        Lab9Graph g = new Lab9Graph();
        g.addEdge("D", "B", 1);
        g.addEdge("B", "A", 1);
        g.addEdge("B", "C", 1);
        g.addEdge("D", "F", 1);
        g.addEdge("F", "E", 1);
        g.addEdge("F", "G", 1);
        return g;
    }

    /** A cycle a-b-c-a plus a dangling d off of c. */
    static Lab9Graph cycle() {
        Lab9Graph g = new Lab9Graph();
        g.addEdge("a", "b", 1);
        g.addEdge("b", "c", 1);
        g.addEdge("c", "a", 1);
        g.addEdge("c", "d", 1);
        return g;
    }

    /** Three components: x alone, the edge p-q, and y alone. */
    static Lab9Graph disconnected() {
        Lab9Graph g = new Lab9Graph();
        g.addNode("x");
        g.addEdge("p", "q", 1);
        g.addNode("y");
        return g;
    }

    static Lab9Graph path() {
        Lab9Graph g = new Lab9Graph();
        g.addEdge("a", "b", 1);
        g.addEdge("b", "c", 1);
        g.addEdge("c", "d", 1);
        return g;
    }

    static Lab9Graph single() {
        Lab9Graph g = new Lab9Graph();
        g.addNode("solo");
        return g;
    }

    @Test
    @DisplayName("Single node: reachable from itself, both orders are [solo]")
    public void tiny() {
        Lab9DFS d = new Lab9DFS(single(), "solo");
        assertThat(d.reachable("solo")).isTrue();
        assertThat(d.preOrder()).containsExactly("solo").inOrder();
        assertThat(d.postOrder()).containsExactly("solo").inOrder();
    }

    @Test
    @DisplayName("Path a-b-c-d from each end")
    public void pathBothEnds() {
        Lab9DFS fromA = new Lab9DFS(path(), "a");
        assertThat(fromA.preOrder()).containsExactly("a", "b", "c", "d").inOrder();
        assertThat(fromA.postOrder()).containsExactly("d", "c", "b", "a").inOrder();
        Lab9DFS fromD = new Lab9DFS(path(), "d");
        assertThat(fromD.preOrder()).containsExactly("d", "c", "b", "a").inOrder();
        assertThat(fromD.postOrder()).containsExactly("a", "b", "c", "d").inOrder();
    }

    @Test
    @DisplayName("Lecture tree: preorder DBACFEG, postorder ACBEGFD")
    public void lectureTreeOrders() {
        Lab9DFS d = new Lab9DFS(lectureTree(), "D");
        assertThat(d.preOrder()).containsExactly("D", "B", "A", "C", "F", "E", "G").inOrder();
        assertThat(d.postOrder()).containsExactly("A", "C", "B", "E", "G", "F", "D").inOrder();
    }

    @Test
    @DisplayName("Cycle: every node visited exactly once")
    public void cycleOrders() {
        Lab9DFS fromA = new Lab9DFS(cycle(), "a");
        assertThat(fromA.preOrder()).containsExactly("a", "b", "c", "d").inOrder();
        assertThat(fromA.postOrder()).containsExactly("d", "c", "b", "a").inOrder();
        Lab9DFS fromC = new Lab9DFS(cycle(), "c");
        assertThat(fromC.preOrder()).containsExactly("c", "b", "a", "d").inOrder();
    }

    @Test
    @DisplayName("Disconnected graph: only the reachable component is visited")
    public void disconnectedOrders() {
        Lab9DFS fromX = new Lab9DFS(disconnected(), "x");
        assertThat(fromX.preOrder()).containsExactly("x").inOrder();
        assertThat(fromX.reachable("x")).isTrue();
        assertThat(fromX.reachable("p")).isFalse();
        assertThat(fromX.reachable("y")).isFalse();
        Lab9DFS fromP = new Lab9DFS(disconnected(), "p");
        assertThat(fromP.preOrder()).containsExactly("p", "q").inOrder();
        assertThat(fromP.postOrder()).containsExactly("q", "p").inOrder();
        assertThat(fromP.reachable("q")).isTrue();
        assertThat(fromP.reachable("x")).isFalse();
    }

    @Test
    @DisplayName("City graph from Berkeley")
    public void city() {
        Lab9DFS d = new Lab9DFS(Main.cityGraph(), "Berkeley");
        assertThat(d.preOrder()).containsExactly(
                "Berkeley", "Sacramento", "Portland", "Seattle", "Salt Lake City", "Las Vegas",
                "Los Angeles", "Phoenix", "Denver", "Chicago", "Dallas", "Atlanta", "New York",
                "Boston").inOrder();
        assertThat(d.postOrder()).containsExactly(
                "Boston", "New York", "Atlanta", "Dallas", "Chicago", "Denver", "Phoenix",
                "Los Angeles", "Las Vegas", "Salt Lake City", "Seattle", "Portland",
                "Sacramento", "Berkeley").inOrder();
        for (String v : Main.cityGraph().nodes()) {
            assertThat(d.reachable(v)).isTrue();
        }
    }

    @Test
    @DisplayName("Every node appears exactly once from any start; start is first in pre, last in post")
    public void everyNodeOnce() {
        Lab9Graph g = Main.cityGraph();
        for (String start : List.of("Berkeley", "Chicago", "Boston")) {
            Lab9DFS d = new Lab9DFS(g, start);
            assertThat(d.preOrder()).containsExactlyElementsIn(g.nodes());
            assertThat(d.postOrder()).containsExactlyElementsIn(g.nodes());
            assertThat(d.preOrder().get(0)).isEqualTo(start);
            assertThat(d.postOrder().get(g.nodes().size() - 1)).isEqualTo(start);
        }
    }

    @Test
    @DisplayName("Start node and reachable() argument must be in the graph")
    public void badArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Lab9DFS(path(), "z"));
        assertThrows(IllegalArgumentException.class, () -> new Lab9DFS(new Lab9Graph(), "a"));
        Lab9DFS d = new Lab9DFS(path(), "a");
        assertThrows(IllegalArgumentException.class, () -> d.reachable("z"));
    }
}
