package lab10algorithms;

import lab10lib.Lab10Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/** Breadth first search on a Lab10Graph from a single start node.
 *
 *  All of the work happens in the constructor, which runs the search and
 *  records what it finds. The query methods then just read off the answers.
 *  Add the neighbors of each node to the queue in the order given by
 *  g.neighbors(). */
public class Lab10BFS {
    private final Lab10Graph g;
    private final Map<String, Integer> distTo = new HashMap<>();   // also serves as the marked set
    private final List<String> order = new ArrayList<>();

    /** Runs breadth first search on g starting from start.
     *  Throws IllegalArgumentException if start is not a node of g. */
    public Lab10BFS(Lab10Graph g, String start) {
        if (!g.nodes().contains(start)) {
            throw new IllegalArgumentException("no such node: " + start);
        }
        this.g = g;
        Queue<String> fringe = new ArrayDeque<>();
        distTo.put(start, 0);
        fringe.add(start);
        while (!fringe.isEmpty()) {
            String u = fringe.remove();
            order.add(u);
            for (String v : g.neighbors(u)) {
                if (!distTo.containsKey(v)) {
                    distTo.put(v, distTo.get(u) + 1);
                    fringe.add(v);
                }
            }
        }
    }

    /** Returns true if s was reached by the search, i.e. there is a path
     *  from start to s. Throws IllegalArgumentException if s is not a node
     *  of the graph. */
    public boolean reachable(String s) {
        if (!g.nodes().contains(s)) {
            throw new IllegalArgumentException("no such node: " + s);
        }
        return distTo.containsKey(s);
    }

    /** Returns the distance from start to s, i.e. the number of edges on a
     *  shortest path from start to s, or -1 if s is not reachable from
     *  start. The distance to start itself is 0. Throws
     *  IllegalArgumentException if s is not a node of the graph. */
    public int distTo(String s) {
        if (!g.nodes().contains(s)) {
            throw new IllegalArgumentException("no such node: " + s);
        }
        if (!distTo.containsKey(s)) {
            return -1;
        }
        return distTo.get(s);
    }

    /** Returns the BFS order of the nodes reachable from start: the order
     *  in which nodes were removed from the queue. The first node in the
     *  list is always start. */
    public List<String> order() {
        return new ArrayList<>(order);
    }
}
