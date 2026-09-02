package bomb;

import common.IntList;
import edu.princeton.cs.algs4.StdRandom;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class Bomb {
    // DO NOT MODIFY THIS FILE

    /** The phases of this bomb that have been defused so far. */
    private final Set<Integer> defused = new HashSet<>();

    /** Returns true iff the given phase of this bomb has been defused. */
    public boolean isDefused(int phase) {
        return defused.contains(phase);
    }

    private String shufflePassword(String s) {
        String code = "" + s.hashCode();
        StdRandom.setSeed(1337);
        char[] chars = code.toCharArray();
        StdRandom.shuffle(chars);
        return String.valueOf(chars);
    }

    private IntList shufflePasswordIntList(String s) {
        String code = "" + s.hashCode();
        StdRandom.setSeed(61833);
        char[] chars = code.toCharArray();
        StdRandom.shuffle(chars);

        IntList curr = null;
        for (int i = chars.length - 1; i >= 0; i -= 1) {
            curr = new IntList(Integer.parseInt(String.valueOf(chars[i])), curr);
        }

        return curr;
    }

    public void phase0(String password) {
        String correctPassword = shufflePassword("hello");
        if (correctPassword.equals(password)) {
            defused.add(0);
        }
    }

    public void phase1(IntList password) {
        IntList correctIntListPassword = shufflePasswordIntList("bye");
        if (correctIntListPassword.equals(password)) {
            defused.add(1);
        }
    }

    public void phase2(String password) {
        Random r = new Random(1337);
        Set<Integer> numbers = new LinkedHashSet<>();
        while (numbers.size() < 100000) {
            numbers.add(r.nextInt());
        }

        boolean correct = false;
        int i = 0;
        for (int number : numbers) {
            if (i == 1337 && String.valueOf(number).equals(password)) {
                correct = true;
            }
            i += 1;
        }
        if (correct) {
            defused.add(2);
        }
    }
}
