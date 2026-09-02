package syntaxexercises;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class ListExercisesTest {
    @Test
    @DisplayName("evens: keeps only the even integers, in order")
    public void testEvens() {
        List<Integer> L = new ArrayList<>(List.of(3, 4, 7, 10));
        assertThat(ListExercises.evens(L)).containsExactly(4, 10).inOrder();
        // evens should build a new list, not change the one it was given
        assertThat(L).containsExactly(3, 4, 7, 10).inOrder();

        assertThat(ListExercises.evens(List.of(1, 3, 5))).isEmpty();
        assertThat(ListExercises.evens(List.of())).isEmpty();
    }

    @Test
    @DisplayName("doubleAll: doubles every element in place")
    public void testDoubleAll() {
        List<Integer> L = new ArrayList<>(List.of(1, 2, 3));
        ListExercises.doubleAll(L);
        assertThat(L).containsExactly(2, 4, 6).inOrder();

        List<Integer> empty = new ArrayList<>();
        ListExercises.doubleAll(empty);
        assertThat(empty).isEmpty();
    }
}
