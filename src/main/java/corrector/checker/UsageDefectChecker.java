package corrector.checker;

import corrector.configuration.value.QuantityConfigValue;
import corrector.configuration.value.UsageConfigValue;
import corrector.defect.Defect;
import corrector.defect.UsageDefect;
import corrector.struct.CardinalityPair;
import corrector.struct.HierarchyPair;
import enums.Cardinality;
import enums.DefectType;
import lombok.extern.java.Log;
import model.Connection;
import model.Diagram;

import java.util.List;
import java.util.stream.Collectors;

@Log
public class UsageDefectChecker {

    public static Defect checkCardinality(Diagram diagram, DefectType defectType,
                                          UsageConfigValue<Cardinality> value){
        return null;
    }

    public static Defect checkCardinalityPairs(Diagram diagram, DefectType defectType,
                                          UsageConfigValue<CardinalityPair> value){
        return null;
    }

    public static Defect checkHierarchy(Diagram diagram, DefectType defectType,
                                          UsageConfigValue<HierarchyPair> value){
        return null;
    }

    public static Defect checkMultivaluedAttributeCardinality(Diagram diagram, DefectType defectType,
                                                              UsageConfigValue<Cardinality> value){

        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = UsageDefect.<Cardinality>usageBuilder();

        List<Cardinality> expectedCardinalities = value.getExpected();

        //Remove 1..1 cardinality as that is not a legal multivalued attribute
        expectedCardinalities.remove(Cardinality.ONE);

        List<Cardinality> presentCardinalities = diagram.getEdges().stream()
                .filter(Connection::isAttributeConnection)
                .filter(Connection::hasCardinality)
                .map(Connection::getCardinality)
                .distinct()
                .collect(Collectors.toList());

        List<Cardinality> missingCardinalities = expectedCardinalities.stream()
                .filter(cardinality -> !presentCardinalities.contains(cardinality))
                .collect(Collectors.toList());

        if(!missingCardinalities.isEmpty()){
            info.append(String.format("Missing cardinalities: %s", missingCardinalities));
            defectPresence = true;
        }

        float taskPoints = value.getPoints();

        return resultingDefectBuilder
                .type(defectType)
                .present(defectPresence)
                .points(taskPoints)
                .additionalInfo(info.toString())
                .expected(expectedCardinalities)
                .actual(presentCardinalities)
                .build();
    }
}
