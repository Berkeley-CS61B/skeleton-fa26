import edu.princeton.cs.algs4.Picture;

/** Removes a seam from a Picture. Both methods return a new Picture and leave
 *  the one they were given untouched. You don't need to modify this file. */
public class SeamRemover {

    /** Returns a copy of picture with the given horizontal seam removed. seam
     *  must have one entry per column: the row to delete in that column. */
    public static Picture removeHorizontalSeam(Picture picture, int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("seam is null");
        }
        if (picture.height() == 1) {
            throw new IllegalArgumentException("picture is only 1 pixel tall");
        }
        if (seam.length != picture.width()) {
            throw new IllegalArgumentException("seam length does not match picture width");
        }
        checkConnected(seam);

        Picture result = new Picture(picture.width(), picture.height() - 1);
        for (int x = 0; x < picture.width(); x += 1) {
            for (int y = 0; y < seam[x]; y += 1) {
                result.set(x, y, picture.get(x, y));
            }
            for (int y = seam[x] + 1; y < picture.height(); y += 1) {
                result.set(x, y - 1, picture.get(x, y));
            }
        }
        return result;
    }

    /** Returns a copy of picture with the given vertical seam removed. seam
     *  must have one entry per row: the column to delete in that row. */
    public static Picture removeVerticalSeam(Picture picture, int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("seam is null");
        }
        if (picture.width() == 1) {
            throw new IllegalArgumentException("picture is only 1 pixel wide");
        }
        if (seam.length != picture.height()) {
            throw new IllegalArgumentException("seam length does not match picture height");
        }
        checkConnected(seam);

        Picture result = new Picture(picture.width() - 1, picture.height());
        for (int y = 0; y < picture.height(); y += 1) {
            for (int x = 0; x < seam[y]; x += 1) {
                result.set(x, y, picture.get(x, y));
            }
            for (int x = seam[y] + 1; x < picture.width(); x += 1) {
                result.set(x - 1, y, picture.get(x, y));
            }
        }
        return result;
    }

    private static void checkConnected(int[] seam) {
        for (int i = 1; i < seam.length; i += 1) {
            if (Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException(
                        "invalid seam: consecutive entries differ by more than 1");
            }
        }
    }
}
