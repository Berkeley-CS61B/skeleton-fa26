package demo;

import edu.princeton.cs.algs4.In;

import static main.Main.*;

public class FileReadDemo {
    public static void main(String[] args) {
        In in = new In(WORD_HISTORY_SIZE3_FILE);
        int i = 0;

        while (!in.isEmpty()) {
            i += 1;
            String nextLine = in.readLine();
            System.out.print("Line " + i + " is: ");
            System.out.println(nextLine);
            System.out.print("After splitting on tab characters, the first word is: ");
            String[] splitLine = nextLine.split("\t");
            System.out.println(splitLine[0]);
        }
    }
}
