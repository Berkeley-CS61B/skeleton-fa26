// This should say "package main" in your own HistoryHandler.java,
// since your file will be in the "main" package.
package demo;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import main.TimeSeries;
import browser.Plotter;
import org.knowm.xchart.XYChart;

import java.util.ArrayList;


public class DummyHistoryHandler extends NgordnetQueryHandler {

    public static final int YEAR_1400 = 1400;
    public static final int YEAR_1500 = 1500;
    public static final int NUM_1000 = 1000;
    public static final int NUM_500 = 500;
    public static final double NUM_100 = 100.0;
    public static final double NUM_1450 = 1450.0;

    @Override
    public String handle(NgordnetQuery q) {
        System.out.println("Got query that looks like:");
        System.out.println("Words: " + q.words());
        System.out.println("Start Year: " + q.startYear());
        System.out.println("End Year: " + q.endYear());

        System.out.println("But I'm totally ignoring that and just plotting a parabola\n"
                + "and a sine wave, because your job will be to figure out how to\n"
                + "actually use the query data.");

        TimeSeries parabola = new TimeSeries();
        for (int i = YEAR_1400; i < YEAR_1500; i += 1) {
            parabola.put(i, (i - NUM_1450) * (i - NUM_1450) + 3);
        }

        TimeSeries sinWave = new TimeSeries();
        for (int i = YEAR_1400; i < YEAR_1500; i += 1) {
            sinWave.put(i, NUM_1000 + NUM_500 * Math.sin(i / NUM_100 * 2 * Math.PI));
        }

        ArrayList<TimeSeries> lts = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        labels.add("parabola");
        labels.add("sine wave");

        lts.add(parabola);
        lts.add(sinWave);

        XYChart chart = Plotter.generateTimeSeriesChart(labels, lts);
        String encodedImage = Plotter.encodeChartAsString(chart);

        return encodedImage;
    }
}
