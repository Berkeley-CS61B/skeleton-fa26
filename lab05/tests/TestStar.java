import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static com.google.common.truth.Truth.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestStar {

    /**
     * Convenience builder: only mass matters for compareTo in these tests.
     */
    private static Star S(String name, double massMsun) {
        return new Star(name, /*distanceLy*/ 0.0, /*mass*/ massMsun, /*mV*/ 0.0);
    }

    // -------- compareTo (by mass) --------

    // TODO: Uncomment the tests below when you're ready to run them.

//    @Test
//    @Order(0)
//    @DisplayName("compareTo: equal masses -> 0")
//    public void testCompareToEqualMasses() {
//        Star a = S("Alpha", 1.00);
//        Star b = S("Beta", 1.00);
//
//        int result = a.compareTo(b);
//        assertThat(result).isEqualTo(0);
//    }
//
//    @Test
//    @Order(1)
//    @DisplayName("compareTo: first heavier -> positive")
//    public void testCompareToFirstHeavierPositive() {
//        Star a = S("Alpha", 1.10);
//        Star b = S("Beta", 0.90);
//
//        int result = a.compareTo(b);
//        assertThat(result).isGreaterThan(0);
//    }
//
//    @Test
//    @Order(2)
//    @DisplayName("compareTo: first lighter -> negative")
//    public void testCompareToFirstLighterNegative() {
//        Star a = S("Alpha", 0.75);
//        Star b = S("Beta", 0.80);
//
//        int result = a.compareTo(b);
//        assertThat(result).isLessThan(0);
//    }
}
