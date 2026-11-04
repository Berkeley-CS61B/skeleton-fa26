/** Run this file. It opens a page in your browser where you can drop in a
 *  photo, pick a target width and height, and watch your SeamCarver shrink
 *  the photo one seam at a time. The page keeps working as you fill in
 *  SeamCarver; stop and re-run after each change. */
public class SeamCarverVisualizer {
    /** The image shown when the page first opens. You can drop any other image
     *  onto the page once it is up. */
    static final String STARTING_IMAGE = "images/HJoceanSmall.png";

    void main() {
        SeamCarverVisualizerInternals.show(STARTING_IMAGE);
    }
}
