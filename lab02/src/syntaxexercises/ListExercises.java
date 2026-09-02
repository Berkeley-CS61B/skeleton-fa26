package syntaxexercises;

import java.util.ArrayList;
import java.util.List;

/*
 * Exercise 2: Lists.
 *
 * Reminder of how common Python list operations look in Java:
 *
 *     Java                                   Python
 *     List<String> lst = new ArrayList<>();  lst = []
 *     lst.add("zero");                       lst.append("zero")
 *     lst.set(0, "zed");                     lst[0] = "zed"
 *     lst.get(0)                             lst[0]
 *     lst.size()                             len(lst)
 *     lst.contains("one")                    "one" in lst
 *     for (String elem : lst) { ... }        for elem in lst: ...
 */
public class ListExercises {

    /*
     * (a) Translate the Python function below into Java.
     *
     *     def evens(L):
     *         result = []
     *         for x in L:
     *             if x % 2 == 0:
     *                 result.append(x)
     *         return result
     *
     *     evens([3, 4, 7, 10])  # [4, 10]
     */
    /** Returns a new list containing the even integers of L, in order. */
    public static List<Integer> evens(List<Integer> L) {
        // TODO
        return null;
    }

    /*
     * (b) Translate the Python function below into Java.
     *
     *     def double_all(L):
     *         for i in range(len(L)):
     *             L[i] = L[i] * 2
     *
     *     L = [1, 2, 3]
     *     double_all(L)
     *     L  # [2, 4, 6]
     */
    /** Doubles every integer in L, in place. */
    public static void doubleAll(List<Integer> L) {
        // TODO
    }
}
