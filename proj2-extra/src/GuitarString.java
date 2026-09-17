/** A vibrating guitar string, simulated with the Karplus-Strong algorithm.
 *  The string's state lives in a Deque61B<Double> — this is where your
 *  ArrayDeque61B from Project 2 gets to make some noise.
 *
 *  Adapted from the Fall 2022 version of this assignment.
 */
public class GuitarString {
    /** Constants. Do not change. */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    // TODO: Declare a Deque61B<Double> called buffer.

    /** Creates a guitar string of the given frequency. */
    public GuitarString(double frequency) {
        // TODO: Instantiate buffer as your ArrayDeque61B, then fill it with
        //       zeros until its size is Math.round(SR / frequency) (cast the
        //       rounded result to an int). This size is the "length" of the
        //       simulated string: a shorter buffer vibrates faster, giving a
        //       higher pitch.
    }

    /** Plucks the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        // TODO: Replace every value in the buffer with random noise: remove
        //       each item and add a new random value between -0.5 and 0.5 in
        //       its place. You can get such a value with:
        //           double r = Math.random() - 0.5;
        //       Call Math.random() separately for every slot — the noise
        //       should be different in each position.
    }

    /** Advances the simulation one time step by performing one iteration of
     *  the Karplus-Strong algorithm. */
    public void tic() {
        // TODO: Remove the sample at the front of the buffer. Average it with
        //       the new front sample, multiply by DECAY, and add the result to
        //       the back of the buffer.
        //       **Do not call StdAudio.play() in here.**
    }

    /** Returns the sample at the front of the buffer. */
    public double sample() {
        // TODO: Return the sample at the front of the buffer, without
        //       changing the buffer.
        return 0;
    }
}
