package timing;

/** The functions whose runtimes we measure in this lab. They don't do anything
 *  useful; they exist so that we have something to time. */
public class FunctionsToBeTimed {

    /** Computes the nth Fibonacci number using a slow naive recursive strategy. */
    public static int fib(int n) {
        if (n < 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        return fib(n - 1) + fib(n - 2);
    }

    /** Adds j to sum a lot of times and returns the total. How does its
     *  runtime grow with N? */
    public static long sp16fp9p1(int N) {
        long sum = 0;
        for (int i = 0; i < N; i += 1) {
            for (int j = 1; j < N; j = j + 2) {
                sum += j;
            }
        }
        return sum;
    }

    /** Adds j to sum for every i and every power-of-two j below N, and returns
     *  the total. How does its runtime grow with N? */
    public static long sp16fp9p2(int N) {
        long sum = 0;
        for (int i = 0; i < N; i += 1) {
            for (int j = 1; j < N; j = j * 2) {
                sum += j;
            }
        }
        return sum;
    }

    /** Does N steps of work, then calls itself twice on N / 2: the same shape
     *  as mergesort. Returns a sum of everything it added up. How does its
     *  runtime grow with N? */
    public static long msf(int N) {
        if (N <= 1) {
            return 1;
        }
        long sum = 0;
        for (int i = 0; i < N; i += 1) {
            sum += i;
        }
        return sum + msf(N / 2) + msf(N / 2);
    }

    /** Adds j to sum for every j below i, for i = 1, 2, 4, ... up to N * N,
     *  and returns the total. How does its runtime grow with N? */
    public static long sp16fp9p5(int N) {
        long sum = 0;
        for (long i = 1; i <= (long) N * N; i *= 2) {
            for (long j = 0; j < i; j += 1) {
                sum += j;
            }
        }
        return sum;
    }
}
