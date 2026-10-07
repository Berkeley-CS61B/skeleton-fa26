package timing;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Plotting and empirical curve fitting for a TimingData.
 *  This class is provided for you; you don't need to modify it. */
public class Fit {

    /** Candidate orders of growth to fit against. */
    public enum Growth {
        CONSTANT("1"),
        LOG_N("log N"),
        N("N"),
        N_LOG_N("N log N"),
        N_SQUARED("N^2"),
        N_CUBED("N^3"),
        TWO_TO_THE_N("2^N");

        private final String label;

        Growth(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }

        /** The value of this growth function at N. */
        public double apply(double N) {
            return switch (this) {
                case CONSTANT -> 1;
                case LOG_N -> log2(N);
                case N -> N;
                case N_LOG_N -> N * log2(N);
                case N_SQUARED -> N * N;
                case N_CUBED -> N * N * N;
                case TWO_TO_THE_N -> Math.pow(2, N);
            };
        }
    }

    /** Shows a plot of microseconds per operation against N. */
    public static void plot(TimingData td) {
        show(chart(td));
    }

    /** Saves the plot of td as a PNG file instead of showing it. */
    public static void savePlot(TimingData td, String fileName) throws IOException {
        BitmapEncoder.saveBitmap(chart(td), fileName, BitmapEncoder.BitmapFormat.PNG);
    }

    /** Shows the timing plot with the best-fitting curve of the form
     *  a * f(N) + b drawn over it, and prints the fit. */
    public static void plotWithFit(TimingData td, Growth f) {
        double[] fit = leastSquares(td, f);
        double a = fit[0];
        double b = fit[1];
        double rSquared = fit[2];
        if (Double.isNaN(a)) {
            IO.println("Can't fit " + f.label() + " to this data: the values of "
                    + f.label() + " are too large for these N.");
            plot(td);
            return;
        }
        String legend = String.format("best fit: %.3g * %s + %.3g   (R^2 = %.3f)",
                a, f.label(), b, rSquared);
        IO.println(td.name() + " | " + legend);

        List<TimingData.Trial> trials = td.trials();
        double minN = trials.get(0).N();
        double maxN = trials.get(trials.size() - 1).N();
        List<Double> xs = new ArrayList<>();
        List<Double> ys = new ArrayList<>();
        int points = 200;
        for (int i = 0; i <= points; i++) {
            double x = minN + (maxN - minN) * i / points;
            xs.add(x);
            ys.add(a * f.apply(x) + b);
        }
        XYChart chart = chart(td);
        XYSeries curve = chart.addSeries(legend, xs, ys);
        curve.setMarker(SeriesMarkers.NONE);
        show(chart);
    }

    /** Least-squares fit of (microseconds per op) = a * f(N) + b.
     *  Returns {a, b, R^2}; a is NaN if f(N) is not finite for some trial. */
    public static double[] leastSquares(TimingData td, Growth f) {
        List<TimingData.Trial> trials = td.trials();
        int n = trials.size();
        double[] x = new double[n];
        double[] y = new double[n];
        double meanX = 0;
        double meanY = 0;
        for (int i = 0; i < n; i++) {
            x[i] = f.apply(trials.get(i).N());
            y[i] = trials.get(i).usPerOp();
            if (!Double.isFinite(x[i])) {
                return new double[] {Double.NaN, Double.NaN, Double.NaN};
            }
            meanX += x[i];
            meanY += y[i];
        }
        meanX /= n;
        meanY /= n;

        double sxy = 0;
        double sxx = 0;
        double syy = 0;
        for (int i = 0; i < n; i++) {
            sxy += (x[i] - meanX) * (y[i] - meanY);
            sxx += (x[i] - meanX) * (x[i] - meanX);
            syy += (y[i] - meanY) * (y[i] - meanY);
        }
        double a = sxx == 0 ? 0 : sxy / sxx;
        double b = meanY - a * meanX;

        double ssRes = 0;
        for (int i = 0; i < n; i++) {
            double residual = y[i] - (a * x[i] + b);
            ssRes += residual * residual;
        }
        double rSquared = syy == 0 ? 1 : 1 - ssRes / syy;
        return new double[] {a, b, rSquared};
    }

    private static XYChart chart(TimingData td) {
        List<Integer> Ns = new ArrayList<>();
        List<Double> us = new ArrayList<>();
        for (TimingData.Trial t : td.trials()) {
            Ns.add(t.N());
            us.add(t.usPerOp());
        }
        XYChart chart = new XYChartBuilder().width(800).height(600)
                .title(td.name()).xAxisTitle("N").yAxisTitle("time (us per op)").build();
        XYSeries data = chart.addSeries("measured", Ns, us);
        data.setLineStyle(SeriesLines.NONE);
        data.setMarker(SeriesMarkers.CIRCLE);
        return chart;
    }

    private static void show(XYChart chart) {
        new SwingWrapper<>(chart).displayChart();
    }

    private static double log2(double v) {
        return Math.log(v) / Math.log(2);
    }
}
