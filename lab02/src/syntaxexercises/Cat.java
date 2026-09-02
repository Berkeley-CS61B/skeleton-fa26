package syntaxexercises;

/*
 * Exercise 1: Class Creation.
 *
 * Translate the Python class below into Java by filling in this file.
 * You'll need to declare the fields, fill in the constructor, and
 * implement the two methods. Assume weight is an int.
 *
 *     class Cat:
 *         def __init__(self, name, weight):
 *             self.name = name
 *             self.weight = weight
 *
 *         def is_heavier_than(self, other):
 *             return self.weight > other.weight
 *
 *         @staticmethod
 *         def total_weight(c1, c2):
 *             return c1.weight + c2.weight
 *
 *     c1 = Cat("Mochi", 12)
 *     c2 = Cat("Tofu", 9)
 *     print(c1.is_heavier_than(c2))    # True
 *     print(Cat.total_weight(c1, c2))  # 21
 */
public class Cat {
    // TODO: Declare the fields name and weight.

    public Cat(String name, int weight) {
        // TODO: Store name and weight in the fields you declared.
    }

    /** Returns true if this cat weighs more than other. */
    public boolean isHeavierThan(Cat other) {
        // TODO
        return false;
    }

    /** Returns the combined weight of c1 and c2. */
    public static int totalWeight(Cat c1, Cat c2) {
        // TODO
        return 0;
    }

    /** Creates a cat named Mochi and a cat named Tofu, then prints the results of the same
     *  two calls as in the Python code above. (Java prints true, not True.) */
    public static void main() {
        // TODO
    }
}
