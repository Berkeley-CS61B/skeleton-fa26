import edu.princeton.cs.algs4.Picture;

/** Prints the energy of every pixel of an image. Change FILENAME to try a
 *  different image. The expected output for each image in the images folder
 *  is in the matching .printseams.txt file. */
public class PrintEnergy {
    static final String FILENAME = "images/3x4.png";

    void main() {
        Picture picture = new Picture(FILENAME);
        IO.println(FILENAME + " (" + picture.width() + "-by-" + picture.height() + " image)");
        IO.println();
        IO.println("The table gives the dual-gradient energies of each pixel.");
        IO.println();

        SeamCarver sc = new SeamCarver(picture);
        for (int y = 0; y < sc.height(); y += 1) {
            for (int x = 0; x < sc.width(); x += 1) {
                IO.print(String.format("%9.2f ", sc.energy(x, y)));
            }
            IO.println();
        }
    }
}
