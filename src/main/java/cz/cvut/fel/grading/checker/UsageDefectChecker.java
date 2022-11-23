package cz.cvut.fel.grading.checker;

import cz.cvut.fel.grading.configuration.value.CardinalityPairUsageConfigValue;
import cz.cvut.fel.grading.configuration.value.CardinalityUsageConfigValue;
import cz.cvut.fel.grading.configuration.value.HierarchyPairUsageConfigValue;
import cz.cvut.fel.grading.defect.Defect;
import cz.cvut.fel.grading.defect.UsageDefect;
import cz.cvut.fel.grading.struct.CardinalityPair;
import cz.cvut.fel.grading.struct.HierarchyPair;
import cz.cvut.fel.enums.Cardinality;
import cz.cvut.fel.enums.DefectType;
import lombok.extern.java.Log;
import cz.cvut.fel.model.Edge;
import cz.cvut.fel.model.Diagram;
import cz.cvut.fel.model.Generalization;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class UsageDefectChecker is an aggregation of static functions
 * that produce UsageDefects by checking a given diagram for their presence.
 * @author Adam Skarda
 */
@Log
public class UsageDefectChecker {

    /**
     * Checks the uses of cardinalities on relationship connections
     * in comparison to the expected cardinalities set in configuration
     * @param diagram Diagram to be checked for actual cardinality uses
     * @param defectType Type of defect checked
     * @param value configuration value with expected uses
     * @return Defect describing the expected and actual usages
     */
    public static Defect checkCardinality(Diagram diagram, DefectType defectType,
                                          CardinalityUsageConfigValue value){
        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = UsageDefect.<Cardinality>usageBuilder();

        List<Cardinality> expectedCardinalities = value.getExpected();

        List<Cardinality> presentCardinalities = diagram.getEdges().stream()
                .filter(Edge::isRelationshipConnection)
                .filter(Edge::hasCardinality)
                .map(Edge::getCardinality)
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

    /**
     * Checks the uses of CardinalityPairs i.e. pairs of cardinalities connecting
     * two entities through a relationship in comparison to expected uses.
     * @param diagram Diagram to be checked for actual cardinality uses
     * @param defectType Type of defect checked
     * @param value configuration value with expected uses
     * @return Defect describing the expected and actual usages
     */
    public static Defect checkCardinalityPairs(Diagram diagram, DefectType defectType,
                                          CardinalityPairUsageConfigValue value){
        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = UsageDefect.<CardinalityPair>usageBuilder();

        List<CardinalityPair> expected = value.getExpected();


        List<CardinalityPair> allPairs = diagram.getRelationships().stream()
                .map(relationship -> {
                    List<Cardinality> cardinalities = relationship.getEdges().stream()
                            .filter(Edge::isRelationshipConnection)
                            .map(Edge::getCardinality)
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


    /**
     * Checks hierarchy type usages in a given diagram in comparison
     * to expected uses of hierarchies. A hierarchy type is specified
     * by a HierarchyPair which is pairing of Coverage and Disjointness information.
     * @param diagram Diagram to be checked for actual cardinality uses
     * @param defectType Type of defect checked
     * @param value configuration value with expected uses
     * @return Defect describing the expected and actual usages
     */
    public static Defect checkHierarchy(Diagram diagram, DefectType defectType,
                                          HierarchyPairUsageConfigValue value){
        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = UsageDefect.<HierarchyPair>usageBuilder();

        List<HierarchyPair> expected = value.getExpected();

        List<HierarchyPair> actual = diagram.getEdges().stream()
                .filter(Edge::isGeneralization)
                .map(edge -> (Generalization)edge)
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

    /**
     * Checks the Cardinality usages on connections to multivalued attributes in
     * comparison to expected usages.
     * @param diagram Diagram to be checked for actual cardinality uses
     * @param defectType Type of defect checked
     * @param value configuration value with expected uses
     * @return Defect describing the expected and actual usages
     */
    public static Defect checkMultivaluedAttributeCardinality(Diagram diagram, DefectType defectType,
                                                              CardinalityUsageConfigValue value){

        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = UsageDefect.<Cardinality>usageBuilder();

        List<Cardinality> expectedCardinalities = value.getExpected();

        //Remove 1..1 cardinality as that is not a legal multivalued attribute
        expectedCardinalities.remove(Cardinality.ONE);

        List<Cardinality> presentCardinalities = diagram.getEdges().stream()
                .filter(Edge::isAttributeConnection)
                .filter(Edge::hasCardinality)
                .map(Edge::getCardinality)
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
