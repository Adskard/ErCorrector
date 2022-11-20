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
import model.Generalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Log
public class UsageDefectChecker {

    public static Defect checkCardinality(Diagram diagram, DefectType defectType,
                                          UsageConfigValue<Cardinality> value){
        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = UsageDefect.<Cardinality>usageBuilder();

        List<Cardinality> expectedCardinalities = value.getExpected();

        List<Cardinality> presentCardinalities = diagram.getEdges().stream()
                .filter(Connection::isRelationshipConnection)
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

    public static Defect checkCardinalityPairs(Diagram diagram, DefectType defectType,
                                          UsageConfigValue<CardinalityPair> value){
        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = UsageDefect.<CardinalityPair>usageBuilder();

        List<CardinalityPair> expected = value.getExpected();


        List<CardinalityPair> allPairs = diagram.getRelationships().stream()
                .map(relationship -> {
                    List<Cardinality> cardinalities = relationship.getConnections().stream()
                            .filter(Connection::isRelationshipConnection)
                            .map(Connection::getCardinality)
                            .distinct()
                            .collect(Collectors.toList());

                    return CardinalityPair.fromCardinalityList(cardinalities);
                })
                .reduce(new ArrayList<>(), (finalList, partial)->{finalList.addAll(partial);
                    return finalList;});

        List<CardinalityPair> actual = allPairs.stream()
                .distinct()
                .collect(Collectors.toList());

        List<CardinalityPair> missingCardinalities = expected.stream()
                .filter(pair -> !actual.contains(pair))
                .collect(Collectors.toList());

        if(!missingCardinalities.isEmpty()){
            info.append(String.format("Missing cardinality pairs: %s", missingCardinalities));
            defectPresence = true;
        }

        float taskPoints = value.getPoints();

        return resultingDefectBuilder
                .type(defectType)
                .present(defectPresence)
                .points(taskPoints)
                .additionalInfo(info.toString())
                .expected(expected)
                .actual(actual)
                .build();
    }

    public static Defect checkHierarchy(Diagram diagram, DefectType defectType,
                                          UsageConfigValue<HierarchyPair> value){
        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = UsageDefect.<HierarchyPair>usageBuilder();

        List<HierarchyPair> expected = value.getExpected();

        List<HierarchyPair> actual = diagram.getEdges().stream()
                .filter(Connection::isGeneralization)
                .map(connection -> (Generalization)connection)
                .map(gen -> new HierarchyPair(gen.getCoverage(), gen.getDisjointness()))
                .distinct()
                .collect(Collectors.toList());

        List<HierarchyPair> missing = expected.stream()
                .filter(hierarchyPair -> !actual.contains(hierarchyPair))
                .collect(Collectors.toList());

        if(!missing.isEmpty()){
            info.append(String.format("Missing Hierarchy usages: %s", missing));
            defectPresence = true;
        }

        float taskPoints = value.getPoints();

        return resultingDefectBuilder
                .type(defectType)
                .present(defectPresence)
                .points(taskPoints)
                .additionalInfo(info.toString())
                .expected(expected)
                .actual(actual)
                .build();
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
