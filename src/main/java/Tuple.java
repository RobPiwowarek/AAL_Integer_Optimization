import java.util.Comparator;

public class Tuple {
    Integer key;
    Double value;

    public Tuple(Integer key, Double value) {
        this.key = key;
        this.value = value;
    }

    public static Comparator<Tuple> descending = Comparator.comparingDouble(o -> o.value);

    public static Comparator<Tuple> ascending = (o1, o2) -> Double.compare(o2.value, o1.value);
}
