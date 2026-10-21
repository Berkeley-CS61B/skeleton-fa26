package lab9lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/** A minimum priority queue of Strings whose priorities are given separately
 *  from the items ("extrinsic"), so an item's priority can be changed after
 *  it has been added. This is exactly what Dijkstra's algorithm needs.
 *
 *  Implemented as a binary heap stored in an ArrayList, plus a map from each
 *  item to its index in the heap so changePriority can find it quickly.
 *  add, removeSmallest, and changePriority all take O(log N) time. */
public class ExtrinsicMinPQ {
    private final List<String> items = new ArrayList<>();      // heap order
    private final List<Integer> priorities = new ArrayList<>(); // parallel to items
    private final Map<String, Integer> indexOf = new HashMap<>();

    /** Adds x with the given priority.
     *  Throws IllegalArgumentException if x is already present. */
    public void add(String x, int priority) {
        if (contains(x)) {
            throw new IllegalArgumentException("already in the queue: " + x);
        }
        items.add(x);
        priorities.add(priority);
        indexOf.put(x, items.size() - 1);
        swim(items.size() - 1);
    }

    /** Returns true if x is in the queue. */
    public boolean contains(String x) {
        return indexOf.containsKey(x);
    }

    /** Returns the item with the smallest priority without removing it.
     *  Throws NoSuchElementException if the queue is empty. */
    public String getSmallest() {
        if (size() == 0) {
            throw new NoSuchElementException("queue is empty");
        }
        return items.get(0);
    }

    /** Removes and returns the item with the smallest priority.
     *  Throws NoSuchElementException if the queue is empty. */
    public String removeSmallest() {
        String smallest = getSmallest();
        int last = items.size() - 1;
        swap(0, last);
        items.remove(last);
        priorities.remove(last);
        indexOf.remove(smallest);
        if (!items.isEmpty()) {
            sink(0);
        }
        return smallest;
    }

    /** Changes the priority of x to p.
     *  Throws NoSuchElementException if x is not in the queue. */
    public void changePriority(String x, int p) {
        if (!contains(x)) {
            throw new NoSuchElementException("not in the queue: " + x);
        }
        int i = indexOf.get(x);
        int old = priorities.get(i);
        priorities.set(i, p);
        if (p < old) {
            swim(i);
        } else {
            sink(i);
        }
    }

    /** Returns the number of items in the queue. */
    public int size() {
        return items.size();
    }

    private void swim(int i) {
        while (i > 0 && priorities.get(i) < priorities.get(parent(i))) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    private void sink(int i) {
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;
            if (left < size() && priorities.get(left) < priorities.get(smallest)) {
                smallest = left;
            }
            if (right < size() && priorities.get(right) < priorities.get(smallest)) {
                smallest = right;
            }
            if (smallest == i) {
                return;
            }
            swap(i, smallest);
            i = smallest;
        }
    }

    private static int parent(int i) {
        return (i - 1) / 2;
    }

    private void swap(int i, int j) {
        String si = items.get(i);
        String sj = items.get(j);
        int pi = priorities.get(i);
        items.set(i, sj);
        items.set(j, si);
        priorities.set(i, priorities.get(j));
        priorities.set(j, pi);
        indexOf.put(sj, i);
        indexOf.put(si, j);
    }
}
