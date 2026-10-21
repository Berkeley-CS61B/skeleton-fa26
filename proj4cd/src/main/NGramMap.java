package main;
import edu.berkeley.eecs.inst.cs61b.ngrams.StaffNGramMap;

import java.util.TreeMap;

/** An object that provides utility methods for making queries on the
 *  Google NGrams dataset (or a subset thereof).
 *
 *  An NGramMap stores pertinent data from a "words file" and a "counts
 *  file". It is not a map in the strict sense, but it does provide additional
 *  functionality.
 *
 *  This is a stripped-down version of the staff solution for 4A. Feel free
 *  to replace this file with your own implementation.
 *
 *  @author Josh Hug
 */
public class NGramMap extends StaffNGramMap {

    public static final int MIN_YEAR = 1400;
    public static final int MAX_YEAR = 2100;

    /** Constructs an NGramMap from WORDHISTORYFILENAME and YEARHISTORYFILENAME. */
    public NGramMap(String wordHistoryFilename, String yearHistoryFilename) {
        super(wordHistoryFilename, yearHistoryFilename);
    }

    /**
     * Provides the history of WORD between STARTYEAR and ENDYEAR, inclusive of both ends. The
     * returned TimeSeries should be a copy, not a link to this NGramMap's TimeSeries. In other
     * words, changes made to the object returned by this function should not also affect the
     * NGramMap. This is also known as a "defensive copy". If the word is not in the data files,
     * returns an empty TimeSeries.
     */
    public TreeMap<Integer, Double> countHistory(String word, int startYear, int endYear) {
        return super.countHistory(word, startYear, endYear);
    }

    /**
     * Provides the history of WORD. The returned TimeSeries should be a copy, not a link to this
     * NGramMap's TimeSeries. In other words, changes made to the object returned by this function
     * should not also affect the NGramMap. This is also known as a "defensive copy". If the word
     * is not in the data files, returns an empty TimeSeries.
     */
    public TreeMap<Integer, Double> countHistory(String word) {
        return countHistory(word, MIN_YEAR, MAX_YEAR);
    }

    // TODO: Replace this file with your own implementation if you want all the methods of an NGramMap
}
