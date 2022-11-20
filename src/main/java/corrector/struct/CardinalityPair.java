package corrector.struct;

import enums.Cardinality;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Log
public class CardinalityPair {
    private final Cardinality first;
    private final Cardinality second;

    public CardinalityPair(Cardinality first, Cardinality second) {
        this.first = first;
        this.second = second;
    }

    public static List<CardinalityPair> fromCardinalityList(List<Cardinality> cardinalities){
        List<Cardinality> distinctCardinalities = cardinalities.stream().distinct().collect(Collectors.toList());
        List<CardinalityPair> list = new LinkedList<>();

        for(int i = 0; i < distinctCardinalities.size(); i++){
            for(int j = i; j < distinctCardinalities.size(); j++){
                list.add(new CardinalityPair(distinctCardinalities.get(i), distinctCardinalities.get(j)));
            }
        }

        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardinalityPair that = (CardinalityPair) o;
        return (first == that.first && second == that.second) ||
                (first == that.second && second == that.first);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "["
                + first +
                "]:[" + second +
                ']';
    }
}
