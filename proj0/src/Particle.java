import edu.princeton.cs.algs4.StdRandom;

import java.awt.*;
import java.util.Map;

public class Particle {
    public ParticleFlavor flavor;
    public int lifespan;

    public static final int PLANT_LIFESPAN = 150;
    public static final int FLOWER_LIFESPAN = 75;
    public static final int FIRE_LIFESPAN = 10;
    public static final Map<ParticleFlavor, Integer> LIFESPANS =
            Map.of(ParticleFlavor.FLOWER, FLOWER_LIFESPAN,
                   ParticleFlavor.PLANT, PLANT_LIFESPAN,
                   ParticleFlavor.FIRE, FIRE_LIFESPAN);

    public Particle(ParticleFlavor flavor) {
        this.flavor = flavor;
        lifespan = -1;
    }

    public Color color() {
        if (flavor == ParticleFlavor.EMPTY) {
            return Color.BLACK;
        }
        return Color.GRAY;
    }

    public void moveInto(Particle other) {
    }

    public void fall(Map<Direction, Particle> neighbors) {
    }

    public void flow(Map<Direction, Particle> neighbors) {
    }

    public void grow(Map<Direction, Particle> neighbors) {
    }

    public void burn(Map<Direction, Particle> neighbors) {
    }

    public void action(Map<Direction, Particle> neighbors) {
    }
}