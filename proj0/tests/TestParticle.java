import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class TestParticle {

    @Test
    public void testColor() {
        Particle emptyParticle = new Particle(ParticleFlavor.EMPTY);
        assertThat(emptyParticle.color()).isEqualTo(Color.BLACK);
        Particle sandParticle = new Particle(ParticleFlavor.SAND);
        assertThat(sandParticle.color()).isEqualTo(Color.YELLOW);
        Particle barrierParticle = new Particle(ParticleFlavor.BARRIER);
        assertThat(barrierParticle.color()).isEqualTo(Color.GRAY);
        Particle waterParticle = new Particle(ParticleFlavor.WATER);
        assertThat(waterParticle.color()).isEqualTo(Color.BLUE);
        Particle fountainParticle = new Particle(ParticleFlavor.FOUNTAIN);
        assertThat(fountainParticle.color()).isEqualTo(Color.CYAN);
        Particle plantParticle = new Particle(ParticleFlavor.PLANT);
        assertThat(plantParticle.color()).isEqualTo(new Color(0, 255, 0));
        Particle fireParticle = new Particle(ParticleFlavor.FIRE);
        assertThat(fireParticle.color()).isEqualTo(new Color(255, 0, 0));
        Particle flowerParticle = new Particle(ParticleFlavor.FLOWER);
        assertThat(flowerParticle.color()).isEqualTo(new Color(255, 141, 161));
    }

    @Test
    public void testMoveInto() {
        Particle particle_a = new Particle(ParticleFlavor.FIRE);
        particle_a.lifespan = 10;
        Particle particle_b = new Particle(ParticleFlavor.EMPTY);
        particle_b.lifespan = -1;

        particle_a.moveInto(particle_b);
        assertThat(particle_a.flavor).isEqualTo(ParticleFlavor.EMPTY);
        assertThat(particle_a.lifespan).isEqualTo(-1);

        assertThat(particle_b.flavor).isEqualTo(ParticleFlavor.FIRE);
        assertThat(particle_b.lifespan).isEqualTo(10);
    }

    @Test
    public void testFall() {
        // Arrange: Initialize a small 2x2 simulator
        ParticleSimulator sim = new ParticleSimulator(2, 2);

        // --- Scenario 1: Fall into Empty Space ---
        // Setup: Place SAND at (0, 1) and ensure (0, 0) is EMPTY
        // Note that 0, 0 is the bottom left, and 0, 1 is the top left.
        sim.particles[0][1] = new Particle(ParticleFlavor.SAND);
        sim.particles[0][0] = new Particle(ParticleFlavor.EMPTY);

        // Get real neighbors for the particle at (0, 1)
        Map<Direction, Particle> neighbors1 = sim.getNeighbors(0, 1);

        // Act: Tell the particle at (0, 1) to fall
        sim.particles[0][1].fall(neighbors1);

        // Assert:
        // 1. Old position (0, 1) should now be EMPTY
        assertThat(sim.particles[0][1].flavor).isEqualTo(ParticleFlavor.EMPTY);
        // 2. New position (0, 0) should now be SAND
        assertThat(sim.particles[0][0].flavor).isEqualTo(ParticleFlavor.SAND);


        // --- Scenario 2: Blocked by Barrier ---
        // Setup: Place SAND at (1, 1) and BARRIER at (1, 0)
        sim.particles[1][1] = new Particle(ParticleFlavor.SAND);
        sim.particles[1][0] = new Particle(ParticleFlavor.BARRIER);

        // Get real neighbors for the particle at (1, 1)
        Map<Direction, Particle> neighbors2 = sim.getNeighbors(1, 1);

        // Act: Tell the particle at (1, 1) to fall
        sim.particles[1][1].fall(neighbors2);

        // Assert:
        // 1. Position (1, 1) stays SAND (blocked)
        assertThat(sim.particles[1][1].flavor).isEqualTo(ParticleFlavor.SAND);
        // 2. Position (1, 0) stays BARRIER
        assertThat(sim.particles[1][0].flavor).isEqualTo(ParticleFlavor.BARRIER);
    }

    @Test
    public void testFlow() {
        int movedLeftCount = 0;
        int movedRightCount = 0;
        int stayedCount = 0;

        for (int i = 0; i < 1000; i++) {
            // Setup: Water in center, Empty spaces on sides
            Particle center = new Particle(ParticleFlavor.WATER);
            Particle left = new Particle(ParticleFlavor.EMPTY);
            Particle right = new Particle(ParticleFlavor.EMPTY);

            // Create neighbors map
            Map<Direction, Particle> neighbors = new HashMap<>();
            neighbors.put(Direction.LEFT, left);
            neighbors.put(Direction.RIGHT, right);
            neighbors.put(Direction.DOWN, new Particle(ParticleFlavor.BARRIER)); // Just to complete the map
            neighbors.put(Direction.UP, new Particle(ParticleFlavor.EMPTY));

            // Act: Attempt to flow
            center.flow(neighbors);

            // Check where the water went
            if (left.flavor == ParticleFlavor.WATER) {
                movedLeftCount += 1;
            } else if (right.flavor == ParticleFlavor.WATER) {
                movedRightCount += 1;
            } else if (center.flavor == ParticleFlavor.WATER) {
                stayedCount += 1;
            }
        }

        // Check that we get lots of lefts, rights, and stays.
        // Note: Chance of a false positive is around 1 in 4 billion.
        assertThat(movedLeftCount).isGreaterThan(240);
        assertThat(movedRightCount).isGreaterThan(240);
        assertThat(stayedCount).isGreaterThan(240);


        // --- Part 2: Verify Safety (Do not overwrite blocks) ---
        // We surround water with barriers and run flow() many times.
        // It should NEVER overwrite a barrier.

        for (int i = 0; i < 10; i++) {
            Particle center = new Particle(ParticleFlavor.WATER);
            Particle leftBarrier = new Particle(ParticleFlavor.BARRIER);
            Particle rightBarrier = new Particle(ParticleFlavor.BARRIER);

            Map<Direction, Particle> blockedNeighbors = new HashMap<>();
            blockedNeighbors.put(Direction.LEFT, leftBarrier);
            blockedNeighbors.put(Direction.RIGHT, rightBarrier);

            center.flow(blockedNeighbors);

            // Assert: Neighbors remain Barriers, Center remains Water
            assertThat(leftBarrier.flavor).isEqualTo(ParticleFlavor.BARRIER);
            assertThat(rightBarrier.flavor).isEqualTo(ParticleFlavor.BARRIER);
            assertThat(center.flavor).isEqualTo(ParticleFlavor.WATER);
        }
    }
}
