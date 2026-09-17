import edu.princeton.cs.algs4.StdAudio;

/** Plays a single hard-coded note: concert A, 440 Hz.
 *  Once your GuitarString works, running this file should sound like a
 *  plucked guitar string. This client is given to you — read it, run it,
 *  enjoy it. */
public class FirstNote {
    static void main() {
        GuitarString stringA = new GuitarString(440.0);
        stringA.pluck();
        for (int i = 0; i < 50000; i += 1) {
            StdAudio.play(stringA.sample());
            stringA.tic();
        }
    }
}
