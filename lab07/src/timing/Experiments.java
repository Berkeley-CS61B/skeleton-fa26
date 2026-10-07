package timing;

import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.Random;

public class Experiments {

    private static void printTimingTable(TimingData data) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        IO.println("------------------------------------------------------------");
        for (TimingData.Trial t : data.trials()) {
            System.out.printf("%12d %12.2f %12d %12.2f\n", t.N(), t.seconds(), t.ops(), t.usPerOp());
        }
    }

    public static TimingData exampleListExperiment() {
        TimingData td = new TimingData("ArrayList contains");

        // A single contains call on a small list takes about a microsecond,
        // too little for our stopwatch to see, so each trial times 10000 of them.
        int ops = 10000;

        for (int N = 1000; N <= 512000; N *= 2) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < N; i += 1) {
                list.add(i);
            }
            Random random = new Random(61);

            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < ops; j += 1) {
                list.contains(random.nextInt(N));
            }
            td.add(N, ops, sw.elapsedTime());
        }

        return td;
    }

    public static TimingData exampleFibonacciExperiment() {
        TimingData td = new TimingData("Naive Recursive Fibonacci");

        // One call of fib(N) per trial is enough: for these N, a single call
        // takes milliseconds to a fraction of a second, long enough to measure.
        int ops = 1;

        for (int N = 25; N <= 40; N++) {
            Stopwatch sw = new Stopwatch();
            int fib = FunctionsToBeTimed.fib(N);
            td.add(N, ops, sw.elapsedTime());
        }

        return td;
    }

    public static TimingData timeSp16fp9p1() {
        TimingData td = new TimingData("sp16fp9p1");

        // TODO: YOUR CODE HERE

        return td;
    }

    public static TimingData timeSp16fp9p2() {
        TimingData td = new TimingData("sp16fp9p2");

        // TODO: YOUR CODE HERE

        return td;
    }

    public static TimingData timeMsf() {
        TimingData td = new TimingData("msf");

        // TODO: YOUR CODE HERE

        return td;
    }

    public static TimingData timeSp16fp9p5() {
        TimingData td = new TimingData("sp16fp9p5");

        // TODO: YOUR CODE HERE

        return td;
    }

    public static TimingData timeAListConstruction() {
        TimingData td = new TimingData("AList addLast construction");

        // TODO: YOUR CODE HERE

        return td;
    }

    void main() {
        // TODO: Modify the following line to change the experiment you're running
        TimingData td = exampleListExperiment();

        printTimingTable(td);
        Fit.plot(td);

        // Once you have timing data for sp16fp9p1, try drawing a best-fit curve over it, e.g.
        // Fit.plotWithFit(td, Fit.Growth.N_SQUARED);
    }
}
