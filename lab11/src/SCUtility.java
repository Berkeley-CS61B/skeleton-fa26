import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdRandom;

import java.awt.Color;

/** A few helpers for testing and debugging a SeamCarver. */
public class SCUtility {

    /** A w-by-h picture of random colors. */
    public static Picture randomPicture(int w, int h) {
        Picture picture = new Picture(w, h);
        for (int x = 0; x < w; x += 1) {
            for (int y = 0; y < h; y += 1) {
                int r = StdRandom.uniformInt(256);
                int g = StdRandom.uniformInt(256);
                int b = StdRandom.uniformInt(256);
                picture.set(x, y, new Color(r, g, b));
            }
        }
        return picture;
    }

    /** The energies of every pixel of sc's current picture, indexed [x][y]. */
    public static double[][] toEnergyMatrix(SeamCarver sc) {
        double[][] a = new double[sc.width()][sc.height()];
        for (int x = 0; x < sc.width(); x += 1) {
            for (int y = 0; y < sc.height(); y += 1) {
                a[x][y] = sc.energy(x, y);
            }
        }
        return a;
    }

    /** A grayscale picture of sc's energies: brighter means higher energy. */
    public static Picture toEnergyPicture(SeamCarver sc) {
        return doubleToPicture(toEnergyMatrix(sc));
    }

    /** Converts a [x][y] table of non-negative values into a grayscale picture,
     *  scaled so that the largest value is white. */
    public static Picture doubleToPicture(double[][] values) {
        int width = values.length;
        int height = values[0].length;
        Picture picture = new Picture(width, height);

        double maxVal = 0;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                maxVal = Math.max(maxVal, values[x][y]);
            }
        }
        if (maxVal == 0) {
            return picture;  // all black
        }
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                float gray = (float) (values[x][y] / maxVal);
                picture.set(x, y, new Color(gray, gray, gray));
            }
        }
        return picture;
    }

    /** A copy of picture with the given seam painted red. */
    public static Picture seamOverlay(Picture picture, boolean isHorizontal, int[] seam) {
        Picture overlaid = new Picture(picture);
        if (isHorizontal) {
            for (int x = 0; x < picture.width(); x += 1) {
                overlaid.set(x, seam[x], Color.RED);
            }
        } else {
            for (int y = 0; y < picture.height(); y += 1) {
                overlaid.set(seam[y], y, Color.RED);
            }
        }
        return overlaid;
    }
}
