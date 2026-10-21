package lab9lib;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** An undirected graph with String nodes and integer edge weights.
 *
 *  Nodes and neighbors are reported in the order they were added, which
 *  makes output easy to read and tests easy to write. Adding an edge between
 *  nodes that don't exist yet adds those nodes automatically. */
public class Lab9Graph {
    /** adj.get(u) maps each neighbor v of u to the weight of edge u-v. */
    private final Map<String, Map<String, Integer>> adj = new LinkedHashMap<>();

    /** Adds node s. Does nothing if s is already in the graph. */
    public void addNode(String s) {
        if (!adj.containsKey(s)) {
            adj.put(s, new LinkedHashMap<>());
        }
    }

    /** Adds an undirected edge between u and v with weight w, replacing any
     *  existing edge between them. */
    public void addEdge(String u, String v, int w) {
        if (u.equals(v)) {
            throw new IllegalArgumentException("self-loops are not allowed: " + u);
        }
        addNode(u);
        addNode(v);
        adj.get(u).put(v, w);
        adj.get(v).put(u, w);
    }

    /** Returns true if there is an edge between u and v. */
    public boolean hasEdge(String u, String v) {
        return adj.containsKey(u) && adj.get(u).containsKey(v);
    }

    /** Returns the weight of the edge between u and v.
     *  Throws IllegalArgumentException if there is no such edge. */
    public int edgeWeight(String u, String v) {
        if (!hasEdge(u, v)) {
            throw new IllegalArgumentException("no edge between " + u + " and " + v);
        }
        return adj.get(u).get(v);
    }

    /** Returns the neighbors of u in the order their edges were added.
     *  Throws IllegalArgumentException if u is not in the graph. */
    public List<String> neighbors(String u) {
        if (!adj.containsKey(u)) {
            throw new IllegalArgumentException("no such node: " + u);
        }
        return new ArrayList<>(adj.get(u).keySet());
    }

    /** Returns all nodes in the order they were added. */
    public List<String> nodes() {
        return new ArrayList<>(adj.keySet());
    }
}
