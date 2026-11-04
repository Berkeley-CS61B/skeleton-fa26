import edu.princeton.cs.algs4.Picture;

/** Prints the energies of an image with a minimum energy vertical seam and
 *  horizontal seam marked by asterisks. Change FILENAME to try a different
 *  image. The expected output for each image in the images folder is in the
 *  matching .printseams.txt file (though if several seams tie, yours may
 *  legitimately differ). */
public class PrintSeams {
    static final String FILENAME = "images/6x5.png";

    private static final boolean HORIZONTAL = true;
    private static final boolean VERTICAL = false;

    private static void printSeam(SeamCarver carver, int[] seam, boolean direction) {
        double totalSeamEnergy = 0.0;
        for (int y = 0; y < carver.height(); y += 1) {
            for (int x = 0; x < carver.width(); x += 1) {
                double energy = carver.energy(x, y);
                String marker = " ";
                if ((direction == HORIZONTAL && y == seam[x])
                        || (direction == VERTICAL && x == seam[y])) {
                    marker = "*";
                    totalSeamEnergy += energy;
                }
                IO.print(String.format("%9.2f%s ", energy, marker));
            }
            IO.println();
        }
        IO.println(String.format("Total energy = %f", totalSeamEnergy));
        IO.println();
        IO.println();
    }

    void main() {
        Picture picture = new Picture(FILENAME);
        IO.println(FILENAME + " (" + picture.width() + "-by-" + picture.height() + " image)");
        IO.println();
        IO.println("The table gives the dual-gradient energies of each pixel.");
        IO.println("The asterisks denote a minimum energy vertical or horizontal seam.");
        IO.println();

        SeamCarver carver = new SeamCarver(picture);

        IO.print("Vertical seam: { ");
        int[] verticalSeam = carver.findVerticalSeam();
        for (int x : verticalSeam) {
            IO.print(x + " ");
        }
        IO.println("}");
        printSeam(carver, verticalSeam, VERTICAL);

        IO.print("Horizontal seam: { ");
        int[] horizontalSeam = carver.findHorizontalSeam();
        for (int y : horizontalSeam) {
            IO.print(y + " ");
        }
        IO.println("}");
        printSeam(carver, horizontalSeam, HORIZONTAL);
    }
}
