import edu.princeton.cs.algs4.In;

import java.util.Comparator;

public class WordFinder {
    /**
     *  Returns the maximum string according to the provider comparator.
     *  If multiple strings are considered equal by c, return the first in
     *  the array.
     *  Use loops. Don't use Collections.max or similar.
     */
    public static String findMax(String[] strings, Comparator<String> c) {
        // TODO: Implement this.
        return null;
    }

    public static void main(String[] args) {
        In in = new In("data/mobydick.txt");
        String[] words = in.readAllStrings();

        // TODO: Print only the word with the most lower case vowels.
        //       Use your findMax method!
        //
        //       Start by creating a Comparator that compares based on lower case vowels.
        Comparator<String> vowelComparator = null;

        // Optional task: Play around with lists of words from Wikipedia articles.
        // String[] zebraWords = ParseUtils.fetchWords("https://en.wikipedia.org/wiki/zebra");
        // System.out.println(findMax(zebraWords, vowelComparator));
    }
}
