package demo;

import main.NGramMap;
import main.TimeSeries;
import browser.Plotter;
import org.knowm.xchart.XYChart;

import static main.Main.*;
import java.util.ArrayList;

public class PlotDemo {
    public static final int YEAR_1900 = 1900;
    public static final int YEAR_1950 = 1950;

    public static void main(String[] args) {

        NGramMap ngm = new NGramMap(WORD_HISTORY_SIZE14377_FILE, YEAR_HISTORY_FILE);
        ArrayList<String> words = new ArrayList<>();
        words.add("cat");
        words.add("dog");

        ArrayList<TimeSeries> lts = new ArrayList<>();
        for (String word : words) {
            lts.add(ngm.weightHistory(word, YEAR_1900, YEAR_1950));
        }

        XYChart chart = Plotter.generateTimeSeriesChart(words, lts);
        String s = Plotter.encodeChartAsString(chart);
        System.out.println(s);

        // you can also do this to display locally:
        // Plotter.displayChart(chart);

    }
}
