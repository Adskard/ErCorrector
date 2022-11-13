package corrector.struct;

import enums.Cardinality;
import lombok.Getter;

@Getter
public class CardinalityPair {
    private final Cardinality first;
    private final Cardinality second;

    public CardinalityPair(Cardinality first, Cardinality second) {
        this.first = first;
        this.second = second;
    }
}
