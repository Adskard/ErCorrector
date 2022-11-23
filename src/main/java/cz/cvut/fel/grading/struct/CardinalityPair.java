package cz.cvut.fel.grading.struct;

import cz.cvut.fel.enums.Cardinality;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class CardinalityPair is used as a helper and utility
 * class to group pairs of Cardinalities.
 * Its main usage is in Defect checking.
 * @author Adam Skarda
 * @see cz.cvut.fel.grading.checker.UsageDefectChecker
 */
@Getter
@Log
public class CardinalityPair {
    private final Cardinality first;
    private final Cardinality second;

    /**
     * General constructor for this class.
     * Params first and second are interchangeable their ordering has
     * no effect on any processes by default.
     * @param first pair participant
     * @param second pair participant
     */
    public CardinalityPair(Cardinality first, Cardinality second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Creates all possible non-repeating pairs from a given list of cardinalities.
     * Where a pair can be reflexive i.e. 0..1 and 0..1 is also a pair.
     *
     * @param cardinalities cardinalities to be paired
     * @return All possible CardinalityPairs
     */
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
