import java.util.Random;

public class IntListMystery {

    /**
     *  Builds an IntList containing some mystery numbers.
     *  You don't have to understand how all this code works.
     */
    public static void mystery() {
        Random r = new Random(829);
        IntList L = null;

        int idx = 0;
        int nextInt;
        while (idx < 1000) {
            nextInt = r.nextInt(0, 800);
            L = new IntList(nextInt, L);
            idx += 1;
        }
    }

    /**
     *  Returns the first five numbers in the mystery IntList.
     */
    public static int[] firstFiveNumbers() {
        // TODO: Replace the 0s with the numbers you found during debugging.
        return new int[]{0, 0, 0, 0, 0};
    }

    /**
     *  Returns the 500th number added to the mystery IntList.
     */
    public static int middleNumber() {
        // TODO: Replace the 0 with the number you found during debugging.
        return 0;
    }

    public static void main(String[] args) {
        mystery();
    }
}
