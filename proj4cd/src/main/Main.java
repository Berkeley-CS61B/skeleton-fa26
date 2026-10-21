package main;

import browser.NgordnetServer;
import org.slf4j.LoggerFactory;
// Feel free to copy over your handlers from 4A and import them here!

public class Main {
    private static final String PREFIX = "./data/";

    /** NGrams Files */
    private static final String WORD_HISTORY_EECS_FILE = PREFIX + "word_history_eecs.csv";
    public static final String WORD_HISTORY_SIZE3_FILE = PREFIX + "word_history_size3.csv";
    public static final String WORD_HISTORY_SIZE4_FILE = PREFIX + "word_history_size4.csv";
    public static final String WORD_HISTORY_SIZE1291_FILE = PREFIX + "word_history_size1291.csv";
    public static final String WORD_HISTORY_SIZE14377_FILE = PREFIX + "word_history_size14377.csv";
    public static final String YEAR_HISTORY_FILE = PREFIX + "year_history.csv";

    /** Wordnet Files */
    private static final String SYNSETS_EECS_FILE = PREFIX + "synsets_eecs.txt";
    private static final String HYPONYMS_EECS_FILE = PREFIX + "hyponyms_eecs.txt";
    public static final String SYNSETS_SIZE16_FILE = PREFIX + "synsets_size16.txt";
    public static final String HYPONYMS_SIZE16_FILE = PREFIX + "hyponyms_size16.txt";
    public static final String SYNSETS_SIZE1000_FILE = PREFIX + "synsets_size1000.txt";
    public static final String HYPONYMS_SIZE1000_FILE = PREFIX +  "hyponyms_size1000.txt";
    public static final String SYNSETS_SIZE82191_FILE = PREFIX + "synsets_size82191.txt";
    public static final String HYPONYMS_SIZE82191_FILE = PREFIX +  "hyponyms_size82191.txt";


    static {
        LoggerFactory.getLogger(Main.class).info("\033[1;38mChanging text color to white");
    }
    public static void main(String[] args) {
        NgordnetServer hns = new NgordnetServer();

        // TODO: modify HyponymsHandler
        // hns.register("hyponyms", new HyponymsHandler());
        // Register all handlers before calling startUp.
        hns.startUp();

        System.out.println("Finished server startup! Visit http://localhost:4567/ngordnet.html");
    }
}