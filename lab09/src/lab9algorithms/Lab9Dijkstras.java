package lab9algorithms;

import lab9lib.ExtrinsicMinPQ;
import lab9lib.Lab9Graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Computes shortest paths from a single source node of a Lab9Graph using
 *  Dijkstra's algorithm, treating each edge weight as a distance.
 *
 *  All of the work happens in the constructor, which builds the shortest
 *  paths tree and records it in the instance variables below. The three
 *  query methods then just read off the answers. ExtrinsicMinPQ (provided)
 *  is the priority queue you'll want for the fringe: it supports
 *  changePriority, which Dijkstra's relies on. */
public class Lab9Dijkstras {
    private final String source;
    /** The best known distance from source to each node reached so far. */
    private final Map<String, Integer> distTo = new HashMap<>();
    /** The node we came from on the best known path to each node. */
    private final Map<String, String> edgeTo = new HashMap<>();

    /** Runs Dijkstra's algorithm on g starting from source.
     *  Throws IllegalArgumentException if source is not a node of g. */
    public Lab9Dijkstras(Lab9Graph g, String source) {
        if (!g.nodes().contains(source)) {
            throw new IllegalArgumentException("no such node: " + source);
        }
        this.source = source;
        // TODO: Replace this with your implementation.
    }

    /** Returns true if there is a path from the source to v. The source
     *  always has a path to itself. */
    public boolean hasPathTo(String v) {
        // TODO: Replace this with your implementation.
        return false;
    }

    /** Returns the total weight of the shortest path from the source to v.
     *  Throws IllegalArgumentException if there is no path to v. */
    public int distTo(String v) {
        // TODO: Replace this with your implementation.
        return 0;
    }

    /** Returns the shortest path from the source to v as a list of nodes,
     *  starting with the source and ending with v. The path to the source
     *  itself is the one-element list [source].
     *  Throws IllegalArgumentException if there is no path to v. */
    public List<String> shortestPathTo(String v) {
        // TODO: Replace this with your implementation.
        return null;
    }
}
