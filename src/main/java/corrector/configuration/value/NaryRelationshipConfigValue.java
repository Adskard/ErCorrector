package corrector.configuration.value;


import lombok.Getter;

@Getter
public class NaryRelationshipConfigValue extends QuantityConfigValue{
    private final int connections;

    public NaryRelationshipConfigValue(float points, int min, int max, int connections) {
        super(points, min, max);
        this.connections = connections;
    }
}
