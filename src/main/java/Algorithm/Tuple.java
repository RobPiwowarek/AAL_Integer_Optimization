package Algorithm;

import java.util.Comparator;

class Tuple {
    public static Comparator<Tuple> ascending = (o1, o2) -> Double.compare(o2.value, o1.value);
    static Comparator<Tuple> descending = Comparator.comparingDouble(o -> o.value);
    Integer key;
    Double value;

    Tuple(Integer key, Double value) {
        this.key = key;
        this.value = value;
    }
}
