package corrector.checker;

import corrector.configuration.value.NaryRelationshipConfigValue;
import corrector.configuration.value.QuantityConfigValue;
import corrector.defect.Defect;
import corrector.defect.QuantityDefect;
import enums.DefectType;
import exception.ConfigurationException;
import model.*;
import java.util.function.Supplier;


public class QuantityDefectChecker {

    private static Defect quantityDefectTemplate(
            DefectType defectType, QuantityConfigValue value, Supplier<Long> actualCountSupplier)
            throws ConfigurationException{
        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();
        var resultingDefectBuilder = QuantityDefect.quantityBuilder();

        int expectedMin = value.getMin();
        int expectedMax = value.getMax();

        if(expectedMax < expectedMin){
            info.append(String.format("Minimum count %s is higher than maximum %s", expectedMin, expectedMax));
            throw new ConfigurationException(info.toString());
        }

        long actualCount = actualCountSupplier.get();

        if(actualCount < expectedMin || actualCount > expectedMax){
            info.append(String.format("Expected %s  <%s, %s> was %s", defectType.getMessage(),
                    expectedMin, expectedMax, actualCount));
            defectPresence = true;
        }

        float taskPoints = value.getPoints();


        return resultingDefectBuilder
                .type(defectType)
                .present(defectPresence)
                .points(taskPoints)
                .additionalInfo(info.toString())
                .min(expectedMin)
                .max(expectedMax)
                .actual((int)actualCount)
                .build();
    }

    public static Defect checkMultivaluedAttributeCount(Diagram diagram, DefectType defectType,
                                                        QuantityConfigValue value) throws ConfigurationException{

        Supplier<Long> actualCount = () -> diagram.getEdges()
                .stream()
                .filter(Connection::isAttributeConnection)
                .filter(Connection::hasCardinality)
                .count();

        return quantityDefectTemplate(defectType, value, actualCount);
    }

    public static Defect checkEntityCount(Diagram diagram, DefectType defectType,
                                                QuantityConfigValue value) throws ConfigurationException{

        Supplier<Long> actualCount = ()-> diagram.getVertices().stream()
                    .filter(DataClass::isEntity)
                    .count();

        return quantityDefectTemplate(defectType, value, actualCount);
    }

    public static Defect checkRelationshipCount(Diagram diagram, DefectType defectType,
                                                QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actualCount = () -> (long) diagram.getRelationships().size();

        return quantityDefectTemplate(defectType, value, actualCount);
    }

    public static Defect checkRecursiveRelationshipCount(Diagram diagram, DefectType defectType,
                                                         QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actualCount = () -> diagram.getRelationships().stream()
                .filter(Relationship::isRecursive)
                .count();

        return quantityDefectTemplate(defectType, value, actualCount);
    }

    public static Defect checkStructuredAttributeCount(Diagram diagram, DefectType defectType,
                                                       QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actualCount = () -> diagram.getAttributes().stream()
                .filter(attribute ->
                        attribute.getAdjacentDataClasses().stream()
                                .anyMatch(dataClass -> dataClass.isEntity() || dataClass.isRelationship()))
                .filter(attribute -> attribute.getAdjacentDataClasses().stream()
                        .anyMatch(DataClass::isAttribute))
                .count();

        return quantityDefectTemplate(defectType, value, actualCount);
    }

    public static Defect checkAttributeCount(Diagram diagram, DefectType defectType,
                                             QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actualCount = () -> (long) diagram.getAttributes().size();

        return quantityDefectTemplate(defectType, value, actualCount);
    }

    public static Defect checkWeakEntityCount(Diagram diagram, DefectType defectType,
                                              QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actual = () ->
                diagram.getEntities().stream()
                        .filter(Entity::getIsWeak)
                        .count();

        return quantityDefectTemplate(defectType, value, actual);
    }

    public static Defect checkNaryRelationshipCount(Diagram diagram, DefectType defectType,
                                                    NaryRelationshipConfigValue value) throws ConfigurationException{

        Supplier<Long> actualRelationshipCount = () -> diagram.getRelationships().stream()
                .filter(relationship -> value.getConnections() <= relationship.getAdjacentDataClasses()
                        .stream()
                        .filter(DataClass::isEntity)
                        .distinct().count())
                .count();

        return quantityDefectTemplate(defectType, value, actualRelationshipCount);
    }

    public static Defect checkMultipleIdentificationsCount(Diagram diagram, DefectType defectType,
                                                           QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actualCount = () -> diagram.getEntities().stream()
                .filter(entity -> entity.getKeys().size() > 1)
                .count();

        return quantityDefectTemplate(defectType, value, actualCount);
    }

    public static Defect checkCompositeCount(Diagram diagram, DefectType defectType,
                                             QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actual = () ->
                (long) diagram.getComposites().size();

        return quantityDefectTemplate(defectType, value, actual);
    }

    public static Defect checkHierarchyCount(Diagram diagram, DefectType defectType,
                                             QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actualCount = () -> diagram.getEdges().stream()
                .filter(Connection::isGeneralization)
                .map(Connection::getTarget)
                .distinct()
                .count();

        return quantityDefectTemplate(defectType, value, actualCount);
    }
}
