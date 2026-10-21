package lab9algorithms;

import lab9lib.Lab9Graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Depth first search on a Lab9Graph from a single start node.
 *
 *  All of the work happens in the constructor, which runs the search and
 *  records what it finds in the instance variables below. The query methods
 *  are already written: they just read off the answers. Visit the neighbors
 *  of each node in the order given by g.neighbors(). */
public class Lab9DFS {
    private final Lab9Graph g;
    /** The nodes visited so far. */
    private final Set<String> marked = new HashSet<>();
    /** Each node, added as soon as it is visited. */
    private final List<String> preOrder = new ArrayList<>();
    /** Each node, added after all of its neighbors have been explored. */
    private final List<String> postOrder = new ArrayList<>();

    /** Runs depth first search on g starting from start.
     *  Throws IllegalArgumentException if start is not a node of g. */
    public Lab9DFS(Lab9Graph g, String start) {
        if (!g.nodes().contains(start)) {
            throw new IllegalArgumentException("no such node: " + start);
        }
        this.g = g;
        // TODO: Run the search from start, filling in marked, preOrder, and postOrder.
    }

    /** Returns true if s was reached by the search, i.e. there is a path
     *  from start to s. Throws IllegalArgumentException if s is not a node
     *  of the graph. */
    public boolean reachable(String s) {
        if (!g.nodes().contains(s)) {
            throw new IllegalArgumentException("no such node: " + s);
        }
        return marked.contains(s);
    }

    /** Returns the DFS preorder of the nodes reachable from start: a node is
     *  added to the list as soon as it is visited, before its neighbors are
     *  explored. The first node in the list is always start. */
    public List<String> preOrder() {
        return new ArrayList<>(preOrder);
    }

    /** Returns the DFS postorder of the nodes reachable from start: a node
     *  is added to the list only after all of its not-yet-visited neighbors
     *  have been fully explored. The last node in the list is always start. */
    public List<String> postOrder() {
        return new ArrayList<>(postOrder);
    }
}
