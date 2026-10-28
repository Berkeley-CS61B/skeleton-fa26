import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;


public class TestSimpleOomage {

    @Test
    public void testHashCodeDeterministic() {
        SimpleOomage so = SimpleOomage.randomSimpleOomage();
        int hashCode = so.hashCode();
        for (int i = 0; i < 100; i += 1) {
            assertThat(so.hashCode()).isEqualTo(hashCode);
        }
    }

    @Test
    public void testHashCodePerfect() {
        /* TODO: Write a test that ensures the hashCode is perfect,
          meaning no two SimpleOomages should EVER have the same
          hashCode UNLESS they have the same red, blue, and green values!
         */
    }

    @Test
    public void testEquals() {
        SimpleOomage ooA = new SimpleOomage(5, 10, 20);
        SimpleOomage ooA2 = new SimpleOomage(5, 10, 20);
        SimpleOomage ooB = new SimpleOomage(50, 50, 50);
        assertThat(ooA).isEqualTo(ooA2);
        assertThat(ooA).isNotEqualTo(ooB);
        assertThat(ooA2).isNotEqualTo(ooB);
        assertThat(ooA).isNotEqualTo("ketchup");
    }

    /*
    @Test
    public void testHashCodeAndEqualsConsistency() {
        SimpleOomage ooA = new SimpleOomage(5, 10, 20);
        SimpleOomage ooA2 = new SimpleOomage(5, 10, 20);
        HashSet<SimpleOomage> hashSet = new HashSet<>();
        hashSet.add(ooA);
        assertThat(hashSet.contains(ooA2)).isTrue();
    }*/

    /* TODO: Uncomment this test after you finish haveNiceHashCodeSpread in OomageTestUtility */
    /*@Test
    public void testRandomOomagesHashCodeSpread() {
        List<Oomage> oomages = new ArrayList<>();
        int N = 10000;

        for (int i = 0; i < N; i += 1) {
            oomages.add(SimpleOomage.randomSimpleOomage());
        }

        assertThat(OomageTestUtility.haveNiceHashCodeSpread(oomages, 10)).isTrue();
    }*/
}
