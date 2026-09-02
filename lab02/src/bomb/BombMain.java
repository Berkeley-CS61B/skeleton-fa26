package bomb;

import common.IntList;

public class BombMain {
    /** Defuses the given phase of bomb b. */
    public static void answers(Bomb b, int phase) {
        // TODO: Find the correct inputs (passwords) to each phase using debugging techniques
        if (phase == 0) {
            b.phase0("Figure this out. I wonder where the phases are defined...");
        }
        if (phase == 1) {
            b.phase1(null); // Figure this out too
        }
        if (phase == 2) {
            b.phase2("Figure this out. I wonder where the phases are defined...");
        }
    }
}
