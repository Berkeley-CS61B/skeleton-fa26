package lab10algorithms;

import lab10lib.Lab10Graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Depth first search on a Lab10Graph from a single start node.
 *
 *  All of the work happens in the constructor, which runs the search and
 *  records what it finds in the instance variables below. The query methods
 *  then just read off the answers. Visit the neighbors of each node in the
 *  order given by g.neighbors(). */
public class Lab10DFS {
    private final Lab10Graph g;
    /** The nodes visited so far. */
    private final Set<String> marked = new HashSet<>();
    /** Each node, added as soon as it is visited. */
    private final List<String> preOrder = new ArrayList<>();
    /** Each node, added after all of its neighbors have been explored. */
    private final List<String> postOrder = new ArrayList<>();

    /** Runs depth first search on g starting from start.
     *  Throws IllegalArgumentException if start is not a node of g. */
    public Lab10DFS(Lab10Graph g, String start) {
        if (!g.nodes().contains(start)) {
            throw new IllegalArgumentException("no such node: " + start);
        }
        this.g = g;
        dfs(start);
    }

    private void dfs(String u) {
        marked.add(u);
        preOrder.add(u);
        for (String v : g.neighbors(u)) {
            if (!marked.contains(v)) {
                dfs(v);
            }
        }
        postOrder.add(u);
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
