package timing;

import java.util.ArrayList;
import java.util.List;

/** The results of a timing experiment: a named list of trials, one per input size. */
public class TimingData {

    /** One row of a timing table: ops operations on an input of size N took seconds. */
    public static class Trial {
        private final int N;
        private final int ops;
        private final double seconds;

        public Trial(int N, int ops, double seconds) {
            this.N = N;
            this.ops = ops;
            this.seconds = seconds;
        }

        /** The size of the input (e.g. how many elements the data structure held). */
        public int N() {
            return N;
        }

        /** How many operations were timed in this trial. */
        public int ops() {
            return ops;
        }

        /** The total time all ops operations took, in seconds. */
        public double seconds() {
            return seconds;
        }

        /** The average time per operation, in microseconds. */
        public double usPerOp() {
            return seconds / ops * 1e6;
        }
    }

    private final String name;
    private final List<Trial> trials;

    public TimingData(String name) {
        this.name = name;
        this.trials = new ArrayList<>();
    }

    /** Records one trial. Experiments call this once per value of N. */
    public void add(int N, int ops, double seconds) {
        trials.add(new Trial(N, ops, seconds));
    }

    public String name() {
        return name;
    }

    public List<Trial> trials() {
        return trials;
    }
}
