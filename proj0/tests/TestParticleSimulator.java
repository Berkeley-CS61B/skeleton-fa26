import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestParticleSimulator {

    @Test
    public void testFallVisual() {
        // Arrange: A 3x5 grid with sand (s) suspended over empty space (.)
        // and a barrier (b) at the bottom.
        String initialBoard = """
            s.s
            s.s
            ...
            ...
            bbb
            """;

        ParticleSimulator sim = fromBoardString(initialBoard);

        // Act: Run 1 tick
        sim.tick();

        String expectedAfter1Tick = """
            ...
            s.s
            s.s
            ...
            bbb
            """;

        // Assert: Verify state after 1 tick
        assertThat(sim.toString().trim()).isEqualTo(expectedAfter1Tick.trim());

        // Act: Run 2nd tick
        sim.tick();

        String expectedAfter2Ticks = """
            ...
            ...
            s.s
            s.s
            bbb
            """;

        // Assert: Verify state after 2 ticks
        assertThat(sim.toString().trim()).isEqualTo(expectedAfter2Ticks.trim());
    }

    @Test
    public void testBarrierDoesntFall() {
        String initialBoard = """
            s..
            ...
            bs.
            ...
            """;

        ParticleSimulator sim = fromBoardString(initialBoard);

        sim.tick();

        String expectedBoard = """
            ...
            s..
            b..
            .s.
            """;

        assertThat(sim.toString().trim()).isEqualTo(expectedBoard.trim());
    }

    @Test
    public void testTickWithFlow() {
        // Arrange:
        // Col 0: Stacked Sand (s) on Barrier -> Should be Stable
        // Col 2: Water (w) on Barrier -> Should Flow
        // Col 4: Sand (s) in Air -> Should Fall
        String startState = """
            s...s
            s.w..
            bbbbb
            """;

        // Possibility 1: Water stays put (or moves Right then Left)
        // Sand falls.
        String expectStay = """
            s....
            s.w.s
            bbbbb
            """;

        // Possibility 2: Water flows Left.
        // Sand falls.
        String expectLeft = """
            s....
            sw..s
            bbbbb
            """;

        // Possibility 3: Water flows Right ONCE (Right then Stay).
        // Sand falls.
        String expectRightSingle = """
            s....
            s..ws
            bbbbb
            """;

        // Possibility 4: Water flows Right TWICE (Right then Right).
        // Water ends up under the Sand (at 4,1), blocking the Sand at (4,2).
        String expectRightDouble = """
            s...s
            s...w
            bbbbb
            """;

        int countStay = 0;
        int countLeft = 0;
        int countRightSingle = 0;
        int countRightDouble = 0;

        // Act: Run 1000 simulations
        for (int i = 0; i < 1000; i++) {
            ParticleSimulator sim = fromBoardString(startState);
            sim.tick();
            String result = sim.toString().trim();

            if (result.equals(expectStay.trim())) {
                countStay += 1;
            } else if (result.equals(expectLeft.trim())) {
                countLeft += 1;
            } else if (result.equals(expectRightSingle.trim())) {
                countRightSingle += 1;
            } else if (result.equals(expectRightDouble.trim())) {
                countRightDouble += 1;
            } else {
                throw new AssertionError("Unexpected board state:\n" + result);
            }
        }

        // Assert:
        // 1. Left (~33%): > 240 is safe.
        assertThat(countLeft).isGreaterThan(240);

        // 2. Stay (~44%): 1/3 (Stay) + 1/9 (Right-then-Left) = 4/9. > 240 is safe.
        assertThat(countStay).isGreaterThan(240);

        // 3. Right Single (~11%): 1/3 (Right) * 1/3 (Stay) = 1/9.
        // Expected ~111. Threshold 50 is safe.
        assertThat(countRightSingle).isGreaterThan(50);

        // 4. Right Double (~11%): 1/3 (Right) * 1/3 (Right) = 1/9.
        // Expected ~111. Threshold 50 is safe.
        assertThat(countRightDouble).isGreaterThan(50);
    }

    @Test
    public void testFallingWaterDoesNotFlow() {
        // Arrange:
        // Water (w) suspended in the center.
        // It has empty space below it (so it MUST fall).
        // It has empty space to the sides (so it COULD flow, if logic was wrong).
        String startState = """
            ...
            .w.
            ...
            bbb
            """;

        // Expected Behavior:
        // The water drops exactly one spot (to the center bottom).
        // It should NOT move Left or Right after falling.
        String expectedState = """
            ...
            ...
            .w.
            bbb
            """;

        for (int i = 0; i < 100; i++) {
            ParticleSimulator sim = fromBoardString(startState);
            sim.tick();

            String result = sim.toString().trim();
            assertThat(result).isEqualTo(expectedState.trim());
        }
    }


    @Test
    public void testGrow() {
        String startState = """
        ...
        .p.
        bbb
        """.trim();


        // The list of REQUIRED growth outcomes
        List<String> expectedGrowthStates = new ArrayList<>();

        expectedGrowthStates.add("""
        ...
        .p.
        bbb
        """.trim()); // no growth

        expectedGrowthStates.add("""
        ...
        pp.
        bbb
        """.trim()); // Left

        expectedGrowthStates.add("""
        .p.
        .p.
        bbb
        """.trim()); // Up

        expectedGrowthStates.add("""
        pp.
        .p.
        bbb
        """.trim()); // Up + Left

        expectedGrowthStates.add("""
        ...
        .pp
        bbb
        """.trim()); // Right

        expectedGrowthStates.add("""
        ..p
        .pp
        bbb
        """.trim()); // Right + Up

        expectedGrowthStates.add("""
        .p.
        .pp
        bbb
        """.trim()); // Up, Right (fall)

        expectedGrowthStates.add("""
        .pp
        .pp
        bbb
        """.trim()); // Right, Up, Left



        // --- ACT ---
        Set<String> observedStates = new HashSet<>();

        for (int i = 0; i < 10000; i++) {
            ParticleSimulator sim = fromBoardString(startState);
            sim.tick();
            observedStates.add(sim.toString().trim());
        }

        // --- ASSERT 1: CHECK FOR MISSING STATES ---
        for (String expected : expectedGrowthStates) {
            assertWithMessage("""
        Test Failed: A required growth state was never observed.
        Missing State:
        %s
        """, expected)
                    .that(observedStates)
                    .contains(expected);
        }

        // --- ASSERT 2: CHECK FOR UNEXPECTED (INVALID) STATES ---

        // Create a "White List" of all valid outcomes (Growth + No Change)
        Set<String> validStates = new HashSet<>(expectedGrowthStates);

        for (String observed : observedStates) {
            assertWithMessage("""
        Test Failed: An invalid/impossible state was generated.
        Unexpected State:
        %s
        """, observed)
                    .that(validStates)
                    .contains(observed);
        }
    }

    @Test
    public void testLifeSpan() {
        // 1. Check initial lifespans
        Particle fire = new Particle(ParticleFlavor.FIRE);
        Particle plant = new Particle(ParticleFlavor.PLANT);
        Particle flower = new Particle(ParticleFlavor.FLOWER);

        assertThat(fire.lifespan).isEqualTo(Particle.FIRE_LIFESPAN);
        assertThat(plant.lifespan).isEqualTo(Particle.PLANT_LIFESPAN);
        assertThat(flower.lifespan).isEqualTo(Particle.FLOWER_LIFESPAN);

        // 2. Tick "fbpbzb" and check decreased
        //
        ParticleSimulator sim = fromBoardString("fbpbz");

        // Before tick, check they are there and have full lifespan
        assertThat(sim.particles[0][0].flavor).isEqualTo(ParticleFlavor.FIRE);
        assertThat(sim.particles[0][0].lifespan).isEqualTo(Particle.FIRE_LIFESPAN);
        assertThat(sim.particles[2][0].flavor).isEqualTo(ParticleFlavor.PLANT);
        assertThat(sim.particles[2][0].lifespan).isEqualTo(Particle.PLANT_LIFESPAN);
        assertThat(sim.particles[4][0].flavor).isEqualTo(ParticleFlavor.FLOWER);
        assertThat(sim.particles[4][0].lifespan).isEqualTo(Particle.FLOWER_LIFESPAN);

        sim.tick();

        // Check lifespans decreased
        assertThat(sim.particles[0][0].lifespan).isEqualTo(Particle.FIRE_LIFESPAN - 1);
        assertThat(sim.particles[2][0].lifespan).isEqualTo(Particle.PLANT_LIFESPAN - 1);
        assertThat(sim.particles[4][0].lifespan).isEqualTo(Particle.FLOWER_LIFESPAN - 1);

        // 3. Make sure they die after the right number of ticks
        // Fire had 10, 1 tick -> 9. 9 more ticks -> 0 (dies).
        for (int i = 0; i < Particle.FIRE_LIFESPAN - 1; i++) {
            sim.tick();
        }
        assertThat(sim.particles[0][0].flavor).isEqualTo(ParticleFlavor.EMPTY);

        // Flower had 75. 10 ticks so far -> 65. 65 more ticks -> 0.
        for (int i = 0; i < Particle.FLOWER_LIFESPAN - Particle.FIRE_LIFESPAN; i++) {
            sim.tick();
        }
        assertThat(sim.particles[4][0].flavor).isEqualTo(ParticleFlavor.EMPTY);

        // Plant had 150. 75 ticks so far -> 75. 75 more ticks -> 0.
        for (int i = 0; i < Particle.PLANT_LIFESPAN - Particle.FLOWER_LIFESPAN; i++) {
            sim.tick();
        }
        assertThat(sim.particles[2][0].flavor).isEqualTo(ParticleFlavor.EMPTY);
    }

    @Test
    public void testBurn() {
        // Arrange: Barriers on top and bottom to restrict growth/fall
        String startState = """
        bbb
        pfz
        bbb
        """.trim();

        String stateNeither = """
        bbb
        pfz
        bbb
        """.trim();

        String statePlantOnly = """
        bbb
        ffz
        bbb
        """.trim();

        String stateFlowerOnly = """
        bbb
        pff
        bbb
        """.trim();

        String stateBoth = """
        bbb
        fff
        bbb
        """.trim();

        int countNeither = 0;
        int countPlantOnly = 0;
        int countFlowerOnly = 0;
        int countBoth = 0;

        // --- ACT ---
        // Run 1000 simulations
        for (int i = 0; i < 1000; i++) {
            ParticleSimulator sim = fromBoardString(startState);
            sim.tick();
            String result = sim.toString().trim();

            if (result.equals(stateNeither)) {
                countNeither++;
            } else if (result.equals(statePlantOnly)) {
                countPlantOnly++;
            } else if (result.equals(stateFlowerOnly)) {
                countFlowerOnly++;
            } else if (result.equals(stateBoth)) {
                countBoth++;
            } else {
                throw new AssertionError("Unexpected board state:\n" + result);
            }
        }

        // --- ASSERT ---
        // Probabilities: Neither (36%), PlantOnly (24%), FlowerOnly (24%), Both (16%)
        assertWithMessage("Neither should burn ~36% (expected ~360)").that(countNeither).isAtLeast(250);
        assertWithMessage("Only the plant should burn ~24% (expected ~240)").that(countPlantOnly).isAtLeast(150);
        assertWithMessage("Only the flower should burn ~24% (expected ~240)").that(countFlowerOnly).isAtLeast(150);
        assertWithMessage("Both should burn ~16% (expected ~160)").that(countBoth).isAtLeast(100);
    }

    @Test
    public void testConstructor_initializesEmptyGrid_usingIndices() {
        ParticleSimulator simulator = new ParticleSimulator(20, 30);

        // 1. Verify the outer array length (Width)
        assertThat(simulator.particles).hasLength(20);

        // 2. Iterate using Integer Indices
        for (int x = 0; x < simulator.width; x++) {

            // Verify the inner array length (Height) for this column
            assertThat(simulator.particles[x]).hasLength(30);

            for (int y = 0; y < simulator.height; y++) {
                Particle particle = simulator.particles[x][y];

                // Verify the particle is not null
                assertThat(particle).isNotNull();

                // Verify the particle is initialized to EMPTY
                assertWithMessage("Particle at x=%s, y=%s should be EMPTY", x, y)
                        .that(particle.flavor)
                        .isEqualTo(ParticleFlavor.EMPTY);
            }
        }
    }

    @Test
    public void testValidIndex() {
        // Arrange: Create a grid of 10x20
        int width = 10;
        int height = 20;
        ParticleSimulator sim = new ParticleSimulator(width, height);

        // Assert: Valid Indices (Inside bounds)
        assertThat(sim.validIndex(0, 0)).isTrue();             // Bottom-Left Corner
        assertThat(sim.validIndex(9, 19)).isTrue();            // Top-Right Corner (width-1, height-1)
        assertThat(sim.validIndex(5, 10)).isTrue();            // Middle

        // Assert: Invalid Indices (Outside bounds)
        assertThat(sim.validIndex(-1, 0)).isFalse();           // Negative X
        assertThat(sim.validIndex(0, -1)).isFalse();           // Negative Y
        assertThat(sim.validIndex(10, 0)).isFalse();           // X == Width (Off by one)
        assertThat(sim.validIndex(0, 20)).isFalse();           // Y == Height (Off by one)
        assertThat(sim.validIndex(100, 100)).isFalse();        // Far out of bounds
    }

    @Test
    public void testTick_updatesParticlesBottomUp() {
        // Arrange: Create a tall, narrow grid (1 wide, 3 high)
        // Coordinates: (0,0) is bottom, (0,2) is top
        ParticleSimulator sim = new ParticleSimulator(1, 3);

        // Set up a stack of sand with a gap at the bottom
        sim.particles[0][2] = new Particle(ParticleFlavor.SAND);  // Top
        sim.particles[0][1] = new Particle(ParticleFlavor.SAND);  // Middle
        sim.particles[0][0] = new Particle(ParticleFlavor.EMPTY); // Bottom

        // Act: Run one simulation step
        sim.tick();

        // Assert: Both particles should have moved down one step

        // 1. The top spot (0,2) should now be empty
        assertThat(sim.particles[0][2].flavor).isEqualTo(ParticleFlavor.EMPTY);

        // 2. The middle spot (0,1) now contains the sand that used to be at the top
        assertThat(sim.particles[0][1].flavor).isEqualTo(ParticleFlavor.SAND);

        // 3. The bottom spot (0,0) catches the middle falling sand
        assertThat(sim.particles[0][0].flavor).isEqualTo(ParticleFlavor.SAND);
    }

    private ParticleSimulator fromBoardString(String board) {
        String[] lines = board.trim().split("\\n");
        int height = lines.length;
        int width = lines[0].trim().length();

        ParticleSimulator sim = new ParticleSimulator(width, height);

        for (int i = 0; i < height; i++) {
            String line = lines[i].trim();
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                int y = height - 1 - i;
                ParticleFlavor flavor = ParticleSimulator.LETTER_TO_PARTICLE.get(c);
                sim.particles[x][y] = new Particle(flavor);
            }
        }
        return sim;
    }

    @Test
    public void testGrowFlower() {
        String startState = """
    ...
    .z.
    bbb
    """.trim();

        // The list of REQUIRED growth outcomes
        List<String> expectedGrowthStates = new ArrayList<>();

        expectedGrowthStates.add("""
    ...
    .z.
    bbb
    """.trim()); // no growth

        expectedGrowthStates.add("""
    ...
    zz.
    bbb
    """.trim()); // Left

        expectedGrowthStates.add("""
    .z.
    .z.
    bbb
    """.trim()); // Up

        expectedGrowthStates.add("""
    zz.
    .z.
    bbb
    """.trim()); // Up + Left

        expectedGrowthStates.add("""
    ...
    .zz
    bbb
    """.trim()); // Right

        expectedGrowthStates.add("""
    ..z
    .zz
    bbb
    """.trim()); // Right + Up

        expectedGrowthStates.add("""
    .z.
    .zz
    bbb
    """.trim()); // Up, Right (fall)

        expectedGrowthStates.add("""
    .zz
    .zz
    bbb
    """.trim()); // Right, Up, Left



        // --- ACT ---
        Set<String> observedStates = new HashSet<>();

        for (int i = 0; i < 10000; i++) {
            ParticleSimulator sim = fromBoardString(startState);
            sim.tick();
            observedStates.add(sim.toString().trim());
        }

        // --- ASSERT 1: CHECK FOR MISSING STATES ---
        for (String expected : expectedGrowthStates) {
            assertWithMessage("""
    Test Failed: A required growth state was never observed.
    Missing State:
    %s
    """, expected)
                    .that(observedStates)
                    .contains(expected);
        }

        // --- ASSERT 2: CHECK FOR UNEXPECTED (INVALID) STATES ---

        // Create a "White List" of all valid outcomes (Growth + No Change)
        Set<String> validStates = new HashSet<>(expectedGrowthStates);

        for (String observed : observedStates) {
            assertWithMessage("""
    Test Failed: An invalid/impossible state was generated.
    Unexpected State:
    %s
    """, observed)
                    .that(validStates)
                    .contains(observed);
        }
    }

    @Test
    public void testGrowDoesNotOverwriteOccupiedNeighbors() {
        // Arrange: a plant boxed in on every side by NON-EMPTY cells.
        //   UP    = SAND   (cannot fall: the plant occupies the cell below it)
        //   LEFT  = WATER  (cannot fall/flow: BARRIER below + BARRIER/plant on its sides)
        //   RIGHT = WATER  (same)
        //   DOWN  = BARRIER
        // A correct grow() only writes into cells whose flavor == EMPTY, so with
        // none of the four neighbours EMPTY the board must be completely static.
        // A grow() that instead grows into anything "not a BARRIER" (or that skips
        // the emptiness check entirely) will overwrite the SAND or WATER here.
        // This is the same class of bug that otherwise only shows up in testBurn,
        // where the plant/flower would grow into an adjacent FIRE particle.
        String startState = """
        sss
        wpw
        bbb
        """.trim();

        for (int i = 0; i < 2000; i++) {
            ParticleSimulator sim = fromBoardString(startState);
            sim.tick();
            String result = sim.toString().trim();

            assertWithMessage("""
        Test Failed: grow() overwrote an occupied (non-EMPTY) neighbour.
        grow() must only write into a neighbouring cell whose flavor is EMPTY.
        Starting board:
        %s
        Board after one tick:
        %s
        """, startState, result)
                    .that(result)
                    .isEqualTo(startState);
        }
    }

    @Test
    public void testGrowGivesFreshLifespan() {
        // Grown particle receives a fresh lifespan, not the parent's decayed lifespan
        // How many ticks to let the boxed-in parent decay before opening a
        // neighbor. Chosen so the parent's lifespan (150 - 5 = 145) is clearly
        // and unambiguously different from a fresh PLANT_LIFESPAN (150) --
        // if the bug is present (child inherits parent's *current* lifespan),
        // the child would show 145 (or less), not 150.
        final int DECAY_TICKS = 5;
        // Upper bound on ticks to wait for growth once a neighbor is opened.
        // grow() only attempts a specific direction with probability 1/10 per
        // tick, so P(no growth in 200 ticks) = 0.9^200, effectively 0.
        final int MAX_TICKS_TO_WAIT_FOR_GROWTH = 200;
        // Repeat the whole experiment several times for robustness against
        // any single unlucky run, since growth timing is random.
        final int TRIALS = 200;

        for (int trial = 0; trial < TRIALS; trial++) {
            // Arrange: plant boxed in on all four sides -- cannot fall or
            // grow anywhere yet, so it will only decay while we tick it.
            String startState = """
        bbb
        bpb
        bbb
        """.trim();
            ParticleSimulator sim = fromBoardString(startState);

            // Sanity check the parent starts at full lifespan.
            assertWithMessage("Test setup assumption failed: parent should start at full PLANT_LIFESPAN")
                    .that(sim.particles[1][1].lifespan)
                    .isEqualTo(Particle.PLANT_LIFESPAN);

            // Act 1: let the parent decay while it has nowhere to grow.
            for (int i = 0; i < DECAY_TICKS; i++) {
                sim.tick();
            }

            int parentLifespanAfterDecay = sim.particles[1][1].lifespan;
            assertWithMessage("""
        Test setup assumption failed: parent's lifespan should have decayed
        below PLANT_LIFESPAN after boxed-in ticks, so this test can tell
        a fresh lifespan apart from an inherited (decayed) one.
        """)
                    .that(parentLifespanAfterDecay)
                    .isLessThan(Particle.PLANT_LIFESPAN);

            // Act 2: open exactly the LEFT neighbor (x-1, same y) so growth
            // becomes possible. LEFT is used specifically because, given
            // tick()'s traversal order (x ascending, then y ascending), the
            // LEFT cell (smaller x) is always processed *before* the parent
            // within the same tick -- so if it's grown into during the
            // parent's action(), it will NOT also get decrementLifespan()'d
            // later in that same tick. (RIGHT/UP are processed *after* the
            // parent and would be decremented once more in the same tick;
            // DOWN would trigger fall() instead of grow(), since fall() is
            // checked before grow() in action().)
            sim.particles[0][1].flavor = ParticleFlavor.EMPTY;
            sim.particles[0][1].lifespan = -1;

            // Act 3: tick until the opened neighbor becomes a PLANT (i.e.
            // until growth into it occurs), then stop.
            boolean grew = false;
            for (int i = 0; i < MAX_TICKS_TO_WAIT_FOR_GROWTH; i++) {
                sim.tick();
                if (sim.particles[0][1].flavor == ParticleFlavor.PLANT) {
                    grew = true;
                    break;
                }
            }

            assertWithMessage("""
        Test Failed: the opened neighbor never grew into a PLANT within
        %s ticks. This should be astronomically unlikely if grow() is
        implemented correctly -- check that growth is happening at all.
        """, MAX_TICKS_TO_WAIT_FOR_GROWTH)
                    .that(grew)
                    .isTrue();

            // Assert: the newly grown particle must have a FRESH PLANT_LIFESPAN,
            // not the parent's decayed lifespan at the moment of growth.
            int childLifespan = sim.particles[0][1].lifespan;
            assertWithMessage("""
        Test Failed: grown particle did not receive a fresh PLANT_LIFESPAN.
        Parent's lifespan at time of growth was (at most): %s
        Child's lifespan after growth was: %s
        Expected child's lifespan to be the full PLANT_LIFESPAN (%s),
        not inherited from the parent's already-decayed lifespan.
        """, parentLifespanAfterDecay, childLifespan, Particle.PLANT_LIFESPAN)
                    .that(childLifespan)
                    .isEqualTo(Particle.PLANT_LIFESPAN);
        }
    }
}
