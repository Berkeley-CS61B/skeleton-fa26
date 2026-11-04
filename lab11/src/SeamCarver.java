import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

/** Resizes a picture one seam at a time using the seam carving technique.
 *  Pixel (x, y) is column x, row y, with (0, 0) in the upper left. */
public class SeamCarver {
    private Picture picture;
    /** energy[x][y] is the dual-gradient energy of pixel (x, y). Recomputed
     *  from scratch whenever the picture changes. */
    private double[][] energy;

    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
        computeEnergies();
    }

    /** Returns a copy of the current picture. */
    public Picture picture() {
        return new Picture(picture);
    }

    /** Width of the current picture, in pixels. */
    public int width() {
        return picture.width();
    }

    /** Height of the current picture, in pixels. */
    public int height() {
        return picture.height();
    }

    /** Energy of pixel (x, y): the sum of the squared x-gradient and the
     *  squared y-gradient. Throws IndexOutOfBoundsException if (x, y) is
     *  outside the picture. */
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) {
            throw new IndexOutOfBoundsException("(" + x + ", " + y + ") is outside a "
                    + width() + "-by-" + height() + " picture");
        }
        return energy[x][y];
    }

    /** Fills in the energy table for the current picture. Neighbors at the
     *  border wrap around to the opposite edge. */
    private void computeEnergies() {
        int w = width();
        int h = height();
        energy = new double[w][h];
        for (int x = 0; x < w; x += 1) {
            for (int y = 0; y < h; y += 1) {
                Color left = picture.get(Math.floorMod(x - 1, w), y);
                Color right = picture.get((x + 1) % w, y);
                Color up = picture.get(x, Math.floorMod(y - 1, h));
                Color down = picture.get(x, (y + 1) % h);
                energy[x][y] = gradientSquared(left, right) + gradientSquared(up, down);
            }
        }
    }

    /** The squared difference between two colors, summed over R, G, and B. */
    private static double gradientSquared(Color a, Color b) {
        int dr = a.getRed() - b.getRed();
        int dg = a.getGreen() - b.getGreen();
        int db = a.getBlue() - b.getBlue();
        return dr * dr + dg * dg + db * db;
    }

    /** Returns a minimum-energy vertical seam: an array of length height()
     *  where entry y is the column of the pixel to remove from row y.
     *  Provided: it just glues together the two methods you write. */
    public int[] findVerticalSeam() {
        double[][] m = computeM(energy);
        if (m == null) {
            return null;
        }
        return findSeamFromM(m);
    }

    /** Given a w-by-h energy table e, indexed e[x][y] like the energy field,
     *  returns the w-by-h table M, also indexed M[x][y], where M[x][y] is the
     *  total energy of the cheapest vertical path that starts anywhere in the
     *  top row and ends at pixel (x, y). Row 0 of M is a copy of row 0 of e.
     *  Every later entry is e[x][y] plus the smallest of the (up to) three
     *  entries of M directly above it: M[x-1][y-1], M[x][y-1], and
     *  M[x+1][y-1], skipping any that would be off the left or right edge.
     *  Fill M one row at a time, top to bottom (no recursion). */
    public static double[][] computeM(double[][] e) {
        // e.length is the width and e[0].length is
        // the height. This is exactly the "Compute M" / "Next row" animation
        // from the spec.
        int w = e.length;
        int h = e[0].length;

        //TODO: Initialize m, the 2d array you are returning

        //TODO: Populate the first row. HINT: What stays constant: x or y?

        //TODO: Uncomment (Highlight, then CMD or CTRL + /), then iterate through the rest
        // for (int y = _; y < _; y += 1) {
        //     for (int x = _; x < _; x += 1) {
        //         //TODO: Find the minimum of the three tiles "above".
        //         //Remember to consider cases where we are on the edge


        //         //TODO: Set m[x][y] to be "e[x][y] + above" (what you just calculated)

        //     }
        // }

        //TODO: Return m
        return null;
    }

    /** Given a table M as produced by computeM, returns the vertical seam it
     *  encodes: an array of length h whose entry y is the column of the seam
     *  in row y. The seam ends at the smallest entry of the bottom row. From
     *  there, walk back up: the seam's pixel in row y - 1 is whichever of the
     *  (up to) three entries of M directly above the current pixel (above-left,
     *  above, above-right, skipping any off the edge) is smallest. When there
     *  are ties, any minimum-energy seam is acceptable. */
    public static int[] findSeamFromM(double[][] m) {
        // Read the seam off of M, bottom row to top row, exactly as you
        // did by clicking in the spec. You do not need an edgeTo array.
        int w = m.length;
        int h = m[0].length;

        //TODO: Initialize seam, the array you are returning

        //TODO: Set the last element of the seam to be the x-index of the smallest value in the bottom row
        //Hint: Find the minimum value while also keeping track of the x-index corresponding to that minimum value

        // //TODO: Uncomment (Highlight, then CMD or CTRL + /), then iterate upwards from the 2nd-to-last row
        // for (int y = _; y >= _; y -= 1) {
        //     //TODO: Set the value of seam[y] to be the x-index of the lowest of the
        //     //(up-to) three values around seam[y+1]
        //     //Hint: This is the same formula as earlier, but with only 3 elements

        // }
        //TODO: Return the seam
        return null;
    }

    /** Returns a minimum-energy horizontal seam: an array of length width()
     *  where entry x is the row of the pixel to remove from column x. Works by
     *  transposing the energy table (so rows become columns) and running your
     *  computeM and findSeamFromM on it. Provided; no need to change it. */
    public int[] findHorizontalSeam() {
        int w = width();
        int h = height();
        double[][] transposed = new double[h][w];
        for (int x = 0; x < w; x += 1) {
            for (int y = 0; y < h; y += 1) {
                transposed[y][x] = energy[x][y];
            }
        }
        double[][] m = computeM(transposed);
        if (m == null) {
            return null;
        }
        return findSeamFromM(m);
    }

    /** Removes the given horizontal seam from the current picture. */
    public void removeHorizontalSeam(int[] seam) {
        checkSeam(seam, width(), height());
        picture = SeamRemover.removeHorizontalSeam(picture, seam);
        computeEnergies();
    }

    /** Removes the given vertical seam from the current picture. */
    public void removeVerticalSeam(int[] seam) {
        checkSeam(seam, height(), width());
        picture = SeamRemover.removeVerticalSeam(picture, seam);
        computeEnergies();
    }

    /** Throws IllegalArgumentException unless seam has the expected length,
     *  every entry is in [0, range), and consecutive entries differ by at
     *  most 1. */
    private static void checkSeam(int[] seam, int expectedLength, int range) {
        if (seam == null) {
            throw new IllegalArgumentException("seam is null");
        }
        if (seam.length != expectedLength) {
            throw new IllegalArgumentException("seam has length " + seam.length
                    + " but should have length " + expectedLength);
        }
        for (int i = 0; i < seam.length; i += 1) {
            if (seam[i] < 0 || seam[i] >= range) {
                throw new IllegalArgumentException("seam entry " + i + " is " + seam[i]
                        + ", which is outside [0, " + range + ")");
            }
            if (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException("seam entries " + (i - 1) + " and " + i
                        + " differ by more than 1 (" + seam[i - 1] + " and " + seam[i] + ")");
            }
        }
    }
}
