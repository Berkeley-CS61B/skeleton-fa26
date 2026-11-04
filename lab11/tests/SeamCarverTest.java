import edu.princeton.cs.algs4.Picture;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SeamCarverTest {

    /** The 3-by-4 example from the spec. */
    private static Picture specPicture() {
        int[][][] rows = {
            {{255, 101, 51}, {255, 101, 153}, {255, 101, 255}},
            {{255, 153, 51}, {255, 153, 153}, {255, 153, 255}},
            {{255, 203, 51}, {255, 204, 153}, {255, 205, 255}},
            {{255, 255, 51}, {255, 255, 153}, {255, 255, 255}}};
        Picture p = new Picture(3, 4);
        for (int y = 0; y < 4; y += 1) {
            for (int x = 0; x < 3; x += 1) {
                int[] rgb = rows[y][x];
                p.set(x, y, new Color(rgb[0], rgb[1], rgb[2]));
            }
        }
        return p;
    }

    @Test
    public void testEnergyOfSpecExample() {
        double[][] expectedByRow = {
            {20808.0, 52020.0, 20808.0},
            {20808.0, 52225.0, 21220.0},
            {20809.0, 52024.0, 20809.0},
            {20808.0, 52225.0, 21220.0}};
        SeamCarver sc = new SeamCarver(specPicture());
        assertThat(sc.width()).isEqualTo(3);
        assertThat(sc.height()).isEqualTo(4);
        for (int y = 0; y < 4; y += 1) {
            for (int x = 0; x < 3; x += 1) {
                assertThat(sc.energy(x, y)).isWithin(1e-3).of(expectedByRow[y][x]);
            }
        }
    }

    @Test
    public void testEnergyOutOfBoundsThrows() {
        SeamCarver sc = new SeamCarver(specPicture());
        assertThrows(IndexOutOfBoundsException.class, () -> sc.energy(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> sc.energy(3, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> sc.energy(0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> sc.energy(0, 4));
    }

    /** The 10-by-10 energy grid from the spec, given row by row (so GRID[y][x]). */
    private static final double[][] SPEC_GRID_BY_ROW = {
            {75, 6, 67, 41, 43, 40, 42, 23, 72, 73},
            {35, 6, 39, 75, 48, 18, 58, 17, 70, 37},
            {31, 63, 6, 61, 58, 73, 20, 61, 35, 17},
            {46, 69, 12, 73, 19, 49, 18, 64, 71, 34},
            {72, 9, 43, 50, 51, 19, 49, 36, 18, 72},
            {92, 80, 84, 84, 95, 18, 47, 42, 35, 66},
            {94, 72, 71, 72, 16, 60, 42, 72, 68, 7},
            {18, 62, 46, 62, 62, 64, 45, 35, 70, 5},
            {46, 50, 71, 64, 67, 44, 55, 20, 28, 55},
            {34, 60, 53, 64, 46, 41, 66, 57, 51, 18}};

    /** The M table for SPEC_GRID_BY_ROW, also row by row (M_BY_ROW[y][x]). */
    private static final double[][] SPEC_M_BY_ROW = {
            {75, 6, 67, 41, 43, 40, 42, 23, 72, 73},
            {41, 12, 45, 116, 88, 58, 81, 40, 93, 109},
            {43, 75, 18, 106, 116, 131, 60, 101, 75, 110},
            {89, 87, 30, 91, 125, 109, 78, 124, 146, 109},
            {159, 39, 73, 80, 142, 97, 127, 114, 127, 181},
            {131, 119, 123, 157, 175, 115, 144, 156, 149, 193},
            {213, 191, 190, 195, 131, 175, 157, 216, 217, 156},
            {209, 252, 236, 193, 193, 195, 202, 192, 226, 161},
            {255, 259, 264, 257, 260, 237, 247, 212, 189, 216},
            {289, 315, 310, 321, 283, 278, 278, 246, 240, 207}};

    /** Transposes a row-major table into the [x][y] layout SeamCarver uses. */
    private static double[][] toXY(double[][] byRow) {
        double[][] out = new double[byRow[0].length][byRow.length];
        for (int y = 0; y < byRow.length; y += 1) {
            for (int x = 0; x < byRow[0].length; x += 1) {
                out[x][y] = byRow[y][x];
            }
        }
        return out;
    }

    @Test
    public void testComputeMOnSpecGrid() {
        double[][] m = SeamCarver.computeM(toXY(SPEC_GRID_BY_ROW));
        assertThat(m).isNotNull();
        assertThat(m.length).isEqualTo(10);
        assertThat(m[0].length).isEqualTo(10);
        assertThat(m[6][2]).isWithin(1e-9).of(60.0);   // the stop-and-check cell from the spec
        for (int y = 0; y < 10; y += 1) {
            for (int x = 0; x < 10; x += 1) {
                assertThat(m[x][y]).isWithin(1e-9).of(SPEC_M_BY_ROW[y][x]);
            }
        }
    }

    @Test
    public void testFindSeamFromMOnSpecGrid() {
        int[] seam = SeamCarver.findSeamFromM(toXY(SPEC_M_BY_ROW));
        assertThat(seam).isEqualTo(new int[] {7, 7, 6, 6, 7, 8, 9, 9, 8, 9});
    }

    @Test
    public void testFindVerticalSeamIsSeamFromM() {
        double[][] e = toXY(SPEC_GRID_BY_ROW);
        double[][] m = SeamCarver.computeM(e);
        assertThat(m).isNotNull();
        int[] seam = SeamCarver.findSeamFromM(m);
        double total = 0;
        for (int y = 0; y < 10; y += 1) {
            total += e[seam[y]][y];
        }
        assertThat(total).isWithin(1e-9).of(207.0);
    }

    @Test
    public void testVerticalSeam6x5() {
        SeamCarver sc = new SeamCarver(new Picture("images/6x5.png"));
        assertThat(sc.findVerticalSeam()).isEqualTo(new int[] {3, 4, 3, 2, 2});
    }

    @Test
    public void testHorizontalSeam6x5() {
        SeamCarver sc = new SeamCarver(new Picture("images/6x5.png"));
        assertThat(sc.findHorizontalSeam()).isEqualTo(new int[] {2, 2, 1, 2, 1, 2});
    }

    @Test
    public void testRemoveVerticalSeam() {
        SeamCarver sc = new SeamCarver(new Picture("images/6x5.png"));
        Picture before = sc.picture();
        sc.removeVerticalSeam(new int[] {3, 4, 3, 2, 2});
        assertThat(sc.width()).isEqualTo(5);
        assertThat(sc.height()).isEqualTo(5);
        Picture after = sc.picture();
        // Row 0 lost column 3: columns 0-2 stay put, columns 4-5 shift left.
        assertThat(after.get(2, 0)).isEqualTo(before.get(2, 0));
        assertThat(after.get(3, 0)).isEqualTo(before.get(4, 0));
        assertThat(after.get(4, 0)).isEqualTo(before.get(5, 0));
    }

    @Test
    public void testRemoveHorizontalSeam() {
        SeamCarver sc = new SeamCarver(new Picture("images/6x5.png"));
        Picture before = sc.picture();
        sc.removeHorizontalSeam(new int[] {2, 2, 1, 2, 1, 2});
        assertThat(sc.width()).isEqualTo(6);
        assertThat(sc.height()).isEqualTo(4);
        Picture after = sc.picture();
        // Column 2 lost row 1: row 0 stays put, rows 2-4 shift up.
        assertThat(after.get(2, 0)).isEqualTo(before.get(2, 0));
        assertThat(after.get(2, 1)).isEqualTo(before.get(2, 2));
        assertThat(after.get(2, 3)).isEqualTo(before.get(2, 4));
    }

    @Test
    public void testInvalidSeamsThrow() {
        SeamCarver sc = new SeamCarver(new Picture("images/6x5.png"));
        assertThrows(IllegalArgumentException.class,
                () -> sc.removeVerticalSeam(new int[] {3, 4, 3, 2}));        // wrong length
        assertThrows(IllegalArgumentException.class,
                () -> sc.removeVerticalSeam(new int[] {3, 5, 3, 2, 2}));     // jumps by 2
        assertThrows(IllegalArgumentException.class,
                () -> sc.removeHorizontalSeam(new int[] {2, 2, 1, 2, 1}));   // wrong length
        assertThrows(IllegalArgumentException.class,
                () -> sc.removeHorizontalSeam(new int[] {2, 2, 0, 2, 1, 2})); // jumps by 2
    }

    /** Checks every image in the images folder against its .printseams.txt
     *  reference: the energy of every pixel, and the total energy of the
     *  vertical and horizontal seams found. Seams are compared by total
     *  energy rather than pixel by pixel, since ties can be broken either way. */
    @Test
    public void testAllReferenceImages() throws IOException {
        File[] refs = new File("images").listFiles((d, name) -> name.endsWith(".printseams.txt"));
        assertThat(refs).isNotNull();
        assertThat(refs.length).isGreaterThan(0);
        for (File ref : refs) {
            String name = ref.getName().replace(".printseams.txt", "");
            Reference expected = Reference.parse(ref);
            SeamCarver sc = new SeamCarver(new Picture("images/" + name + ".png"));

            for (int y = 0; y < sc.height(); y += 1) {
                for (int x = 0; x < sc.width(); x += 1) {
                    assertThat(sc.energy(x, y)).isWithin(0.01).of(expected.energy[y][x]);
                }
            }

            int[] vertical = sc.findVerticalSeam();
            assertThat(vertical).isNotNull();
            assertThat(vertical.length).isEqualTo(sc.height());
            double verticalTotal = 0;
            for (int y = 0; y < sc.height(); y += 1) {
                if (y > 0) {
                    assertThat(Math.abs(vertical[y] - vertical[y - 1])).isAtMost(1);
                }
                verticalTotal += sc.energy(vertical[y], y);
            }
            assertThat(verticalTotal).isWithin(0.01).of(expected.verticalTotal);

            int[] horizontal = sc.findHorizontalSeam();
            assertThat(horizontal).isNotNull();
            assertThat(horizontal.length).isEqualTo(sc.width());
            double horizontalTotal = 0;
            for (int x = 0; x < sc.width(); x += 1) {
                if (x > 0) {
                    assertThat(Math.abs(horizontal[x] - horizontal[x - 1])).isAtMost(1);
                }
                horizontalTotal += sc.energy(x, horizontal[x]);
            }
            assertThat(horizontalTotal).isWithin(0.01).of(expected.horizontalTotal);
        }
    }

    /** The parts of a .printseams.txt file that the test above needs. */
    private static class Reference {
        double[][] energy;   // [row][col]
        double verticalTotal;
        double horizontalTotal;

        static Reference parse(File f) throws IOException {
            Reference r = new Reference();
            List<double[]> rows = new ArrayList<>();
            List<Double> totals = new ArrayList<>();
            Pattern number = Pattern.compile("-?\\d+\\.\\d+");
            boolean readingTable = false;
            for (String line : Files.readAllLines(f.toPath())) {
                if (line.startsWith("Vertical seam:")) {
                    readingTable = true;
                    rows.clear();
                } else if (line.startsWith("Horizontal seam:")) {
                    readingTable = false;
                } else if (line.startsWith("Total energy")) {
                    totals.add(Double.parseDouble(line.replaceAll("[^0-9.]", "")));
                } else if (readingTable && !line.isBlank()) {
                    Matcher m = number.matcher(line);
                    List<Double> vals = new ArrayList<>();
                    while (m.find()) {
                        vals.add(Double.parseDouble(m.group()));
                    }
                    rows.add(vals.stream().mapToDouble(Double::doubleValue).toArray());
                }
            }
            r.energy = rows.toArray(new double[0][]);
            r.verticalTotal = totals.get(0);
            r.horizontalTotal = totals.get(1);
            return r;
        }
    }
}
