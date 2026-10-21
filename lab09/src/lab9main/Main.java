package lab9main;

import lab9lib.Lab9Graph;

/** Run this file. It builds a toy graph of US cities, where each edge
 *  weight is the approximate driving distance in miles, and opens a page in
 *  your browser that shows the orders computed by Lab9DFS, Lab9BFS, and
 *  Lab9Dijkstras (plus the shortest paths tree for Dijkstra's). The page
 *  keeps working as you fill in those classes; stop and re-run after each
 *  change. */
public class Main {

    public static Lab9Graph cityGraph() {
        Lab9Graph g = new Lab9Graph();
        g.addEdge("Berkeley", "Sacramento", 80);
        g.addEdge("Berkeley", "Los Angeles", 380);
        g.addEdge("Los Angeles", "Las Vegas", 270);
        g.addEdge("Los Angeles", "Phoenix", 370);
        g.addEdge("Las Vegas", "Salt Lake City", 420);
        g.addEdge("Sacramento", "Portland", 580);
        g.addEdge("Portland", "Seattle", 175);
        g.addEdge("Seattle", "Salt Lake City", 830);
        g.addEdge("Salt Lake City", "Denver", 520);
        g.addEdge("Phoenix", "Denver", 820);
        g.addEdge("Denver", "Chicago", 1000);
        g.addEdge("Phoenix", "Dallas", 1065);
        g.addEdge("Dallas", "Chicago", 925);
        g.addEdge("Chicago", "New York", 790);
        g.addEdge("New York", "Boston", 215);
        g.addEdge("Dallas", "Atlanta", 780);
        g.addEdge("Atlanta", "New York", 880);
        return g;
    }

    void main() {
        Lab9Graph g = cityGraph();
        IO.println("Nodes: " + g.nodes());
        IO.println("Neighbors of Denver: " + g.neighbors("Denver"));
        IO.println("Chicago to New York is " + g.edgeWeight("Chicago", "New York") + " miles.");
        GraphVisualizerInternals.show(g);
    }
}
