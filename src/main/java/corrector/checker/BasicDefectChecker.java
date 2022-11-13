package corrector.checker;

import corrector.configuration.value.ConfigValue;
import corrector.defect.BasicDefect;
import corrector.defect.Defect;
import enums.Cardinality;
import enums.DefectType;
import model.Attribute;
import model.Connection;
import model.Diagram;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class BasicDefectChecker {
    public static Defect checkMultivaluedAttributes(Diagram diagram, DefectType defectType, ConfigValue value){

        StringBuilder info = new StringBuilder();
        boolean defectPresence = false;
        List<Attribute> problematicAttributes = new LinkedList<>();

        var resultingDefectBuilder = BasicDefect.<Attribute>basicBuilder();

        float taskPoints = value.getPoints();

        List<Connection> connectionsWithIllegalCardinality = diagram.getEdges().stream()
                .filter(Connection::isAttributeConnection)
                .filter(Connection::hasCardinality)
                .filter(connection -> connection.getCardinality().equals(Cardinality.ONE))
                .collect(Collectors.toList());

        if(!connectionsWithIllegalCardinality.isEmpty()){
            problematicAttributes.addAll(connectionsWithIllegalCardinality.stream()
                    .map(connection -> (Attribute)connection.getSource())
                    .collect(Collectors.toList()));

            info.append(String.format("Multivalued attributes: %s with incorrect cardinality 1..1", problematicAttributes));
            defectPresence = true;
        }

        return resultingDefectBuilder.type(defectType)
                .points(taskPoints)
                .additionalInfo(info.toString())
                .incorrectObjects(problematicAttributes)
                .present(defectPresence)
                .build();
    }


}
