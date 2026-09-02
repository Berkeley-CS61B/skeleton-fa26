package syntaxexercises;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Exercise 3: Maps.
 *
 * Reminder of how common Python dict operations look in Java:
 *
 *     Java                                       Python
 *     Map<String, Integer> m = new HashMap<>();  m = {}
 *     m.put("hello", 5);                         m["hello"] = 5
 *     m.get("hello")                             m["hello"]
 *     m.size()                                   len(m)
 *     m.containsKey("hello")                     "hello" in m
 *     for (String key : m.keySet()) { ... }      for key in m.keys(): ...
 */
public class MapExercises {

    /*
     * Translate the Python function below into Java.
     *
     *     def count_words(L):
     *         counts = {}
     *         for word in L:
     *             if word not in counts:
     *                 counts[word] = 0
     *             counts[word] = counts[word] + 1
     *         return counts
     *
     *     count_words(["hi", "bye", "hi"])  # {"hi": 2, "bye": 1}
     */
    /** Returns a map from each string in L to the number of times it appears in L. */
    public static Map<String, Integer> countWords(List<String> L) {
        // TODO
        return null;
    }
}
