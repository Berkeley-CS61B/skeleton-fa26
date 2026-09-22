import edu.princeton.cs.algs4.In;

import java.util.List;

public class Star {
    private final String name;
    private final double distanceLy;
    private final double massMsun;
    private final double mV;

    public Star(String name, double distanceLy, double massMsun, double mV) {
        this.name = name;
        this.distanceLy = distanceLy;
        this.massMsun = massMsun;
        this.mV = mV;
    }

    @Override
    public String toString() {
        return String.format("Star{name='%s', distanceLy=%.6f, massMsun=%s, mV=%.2f}",
                name, distanceLy, String.format("%.3f", massMsun), mV);
    }

    // You do not need to modify anything above this line
    // (except the "public class Star" line).

    // TODO: Make stars comparable.

    public static void main(String[] args) {
        In in = new In("data/stars20.txt");
        List<Star> stars = ParseUtils.readCsv(in);

        // TODO: Print out the star with the greatest mass.

        // TODO: Comment this out after you print out the star with the greatest mass.
        System.out.println("Loaded " + stars.size() + " stars.");
        int show = 5;
        for (int i = 0; i < show; i += 1) {
            System.out.println(stars.get(i));
        }
    }
}
