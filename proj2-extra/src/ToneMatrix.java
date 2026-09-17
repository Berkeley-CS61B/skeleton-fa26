import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/** A 16x16 tone matrix, built from scratch on top of StdDraw and your
 *  GuitarString. See the spec for the full description. The design is up to
 *  you — the snippets below are the only StdDraw magic you need. */
public class ToneMatrix {
    public static final int ROWS = 16;
    public static final int COLS = 16;

    /* ------------------------------------------------------------------
     * The two StdDraw primitives you need (given):
     *
     * 1. Drawing a filled rectangle. If you set the coordinate system with
     *
     *        StdDraw.setXscale(0, COLS);
     *        StdDraw.setYscale(0, ROWS);
     *
     *    then the cell in column c, row r (r = 0 at the BOTTOM) is a unit
     *    square you can draw with:
     *
     *        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);   // any color
     *        StdDraw.filledRectangle(c + 0.5, r + 0.5, 0.45, 0.45);
     *
     *    (The arguments are the CENTER x, center y, half-width, half-height.)
     *
     * 2. Registering clicks. Inside your main loop:
     *
     *        if (StdDraw.isMousePressed()) {
     *            int c = (int) StdDraw.mouseX();   // column clicked
     *            int r = (int) StdDraw.mouseY();   // row clicked
     *            ...
     *        }
     *
     *    Beware: isMousePressed() stays true for MANY loop iterations while
     *    the button is down. If you toggle a cell on every iteration it will
     *    flicker madly. You'll want to remember whether the mouse was already
     *    down on the previous iteration and only act on the transition.
     *    (Nice upgrade: also toggle when a held mouse crosses into a NEW
     *    cell - then you can paint patterns with one sweep.)
     *
     * Optional, for smoother animation: call StdDraw.enableDoubleBuffering()
     * once at startup, and StdDraw.show() after you finish redrawing a frame.
     * ------------------------------------------------------------------ */

    static void main() {
        // TODO: Build the tone matrix! You have complete design freedom.
        //       The behavior you're aiming for:
        //
        //   - A 16x16 grid of cells, each either on or off. Clicking a cell
        //     toggles it.
        //   - One GuitarString per ROW. Row r's frequency should be the rth
        //     note of a pentatonic scale (see the spec for the frequencies) —
        //     low notes at the bottom, high notes at the top.
        //   - A cursor sweeps across the columns, left to right, forever
        //     (wrapping around). When the cursor enters a column, every
        //     lit cell in that column plucks its row's string.
        //   - Meanwhile, on every iteration of your loop, play the sum of all
        //     strings' samples with StdAudio.play(...) and tic() every string
        //     — exactly like PentatonicKeyboard, just with 16 strings.
        //
        //       Timing hint: your loop plays 44100 samples per second, so
        //       counting samples IS your clock. Advancing the cursor once
        //       every ~6000 samples gives a pleasant tempo.
        //
        //       Drawing hint: redrawing the whole grid on every loop
        //       iteration will wreck your audio. Only redraw when something
        //       changed (a click happened or the cursor moved).
    }
}
