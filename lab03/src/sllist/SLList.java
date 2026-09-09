package sllist;

public class SLList {
    private class IntNode {
        public int item;
        public IntNode next;

        public IntNode(int item, IntNode next) {
            this.item = item;
            this.next = next;
        }
    }

    private IntNode sentinel;
    public int size;

    public SLList() {
        sentinel = new IntNode(63, null);
        size = 0;
    }

    public void addFirst(int x) {
        sentinel.next = new IntNode(x, sentinel.next);
        size += 1;
    }

    /** Inserts x at the given position. If position is past the end of the
     *  list, inserts at the end. You may assume position is non-negative.
     *  Example: if the SLList is 5 -> 6 -> 2, then insert(10, 1)
     *  results in 5 -> 10 -> 6 -> 2, and insert(10, 7) results in
     *  5 -> 6 -> 2 -> 10. */
    public void insert(int x, int position) {
        // TODO: Fill in this method
    }

    /** Returns the items of this list as an array (provided for testing). */
    public int[] toArray() {
        int[] result = new int[size];
        IntNode p = sentinel.next;
        for (int i = 0; i < result.length; i += 1) {
            result[i] = p.item;
            p = p.next;
        }
        return result;
    }
}
