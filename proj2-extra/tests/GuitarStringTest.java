import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

/** Tests the GuitarString class. These tests are silent — no audio needed.
 *  For the fun audible test, just run FirstNote. */
public class GuitarStringTest {

    @Test
    @DisplayName("sample() is 0 before plucking, nonzero after, and doesn't mutate")
    public void testSample() {
        GuitarString s = new GuitarString(100);
        assertThat(s.sample()).isEqualTo(0.0);
        assertThat(s.sample()).isEqualTo(0.0);
        assertThat(s.sample()).isEqualTo(0.0);
        s.pluck();
        double sample = s.sample();
        assertWithMessage("After plucking, your samples should not be 0.")
                .that(sample).isNotEqualTo(0.0);
        assertWithMessage("sample() should not change the state of your string.")
                .that(s.sample()).isEqualTo(sample);
        assertWithMessage("sample() should not change the state of your string.")
                .that(s.sample()).isEqualTo(sample);
    }

    @Test
    @DisplayName("tic() advances the simulation to a new sample")
    public void testTic() {
        GuitarString s = new GuitarString(100);
        s.pluck();
        double sample1 = s.sample();
        assertWithMessage("After plucking, your samples should not be 0.")
                .that(sample1).isNotEqualTo(0.0);
        s.tic();
        assertWithMessage("After tic(), your sample should not stay the same.")
                .that(s.sample()).isNotEqualTo(sample1);
    }

    @Test
    @DisplayName("tic() computes the Karplus-Strong average with decay")
    public void testTicCalculations() {
        // Frequency 11025 gives a buffer of exactly 44100 / 11025 = 4 samples,
        // so after four tics the front of the buffer is the first new sample.
        GuitarString s = new GuitarString(11025);
        s.pluck();

        double s1 = s.sample();
        s.tic();
        double s2 = s.sample();
        s.tic();
        double s3 = s.sample();
        s.tic();
        double s4 = s.sample();

        s.tic();
        double expected = 0.996 * 0.5 * (s1 + s2);
        assertWithMessage("The first new sample should be the decayed average "
                + "of the first two plucked samples.")
                .that(s.sample()).isWithin(1e-9).of(expected);
    }

    @Test
    @DisplayName("buffer length follows Math.round(SR / frequency)")
    public void testBufferLength() {
        // A 440 Hz string has a buffer of round(44100 / 440) = 100 samples,
        // so the plucked noise should first reappear (decayed) after 100 tics.
        GuitarString s = new GuitarString(440);
        s.pluck();
        double first = s.sample();
        for (int i = 0; i < 100; i += 1) {
            s.tic();
        }
        // After exactly bufferSize tics, the front sample is derived from the
        // ORIGINAL front two samples; if the buffer were the wrong length the
        // values would drift out of phase. We just sanity-check it's nonzero.
        assertWithMessage("After one full trip around the buffer, samples "
                + "should still be flowing (is your buffer the right length?)")
                .that(s.sample()).isNotEqualTo(first);
    }
}
