import java.util.Comparator;

public class Tuple {
    Integer key;
    Double value;

    public Tuple(Integer key, Double value) {
        this.key = key;
        this.value = value;
    }

    public static Comparator<Tuple> descending = new Comparator<Tuple>() {
        @Override
        public int compare(Tuple o1, Tuple o2) {
            return Double.compare(o1.value, o2.value);
        }
    };

    public static Comparator<Tuple> ascending = new Comparator<Tuple>() {
        @Override
        public int compare(Tuple o1, Tuple o2) {
            return Double.compare(o2.value, o1.value);
        }
    };
}
