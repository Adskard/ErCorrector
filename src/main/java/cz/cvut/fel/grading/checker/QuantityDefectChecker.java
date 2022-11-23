package cz.cvut.fel.grading.checker;

import cz.cvut.fel.grading.configuration.value.NaryRelationshipConfigValue;
import cz.cvut.fel.grading.configuration.value.QuantityConfigValue;
import cz.cvut.fel.grading.defect.Defect;
import cz.cvut.fel.grading.defect.QuantityDefect;
import cz.cvut.fel.enums.DefectType;
import cz.cvut.fel.exception.ConfigurationException;
import cz.cvut.fel.model.*;

import java.util.function.Supplier;

/**
 * Class QuantityDefectChecker is aggregation of static methods
 * that perform checks on a Diagram,
 * which produce QuantityDefects as a result.
 * @author Adam Skarda
 */
public class QuantityDefectChecker {

    /**
     * Template method for duplicate code reduction.
     * @param defectType type of defect checked
     * @param value configuration value for checks
     * @param actualCountSupplier Function that supplies the actual number of occurrences
     * @return QuantityDefect describing the expected and actual number of occurrences
     * @throws ConfigurationException if QuantityConfigValue contains invalid minimum and maximum number of occurrences
     */
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

    /**
     * Counts the number of multivalued attribute occurrences.
     * @param diagram Diagram to be checked for occurrences
     * @param defectType Type of defect checked
     * @param value configuration value for this check
     * @return QuantityDefect describing the expected and actual number of occurrences
     * @throws ConfigurationException if QuantityConfigValue contains invalid minimum and maximum number of occurrences
     */
    public static Defect checkMultivaluedAttributeCount(Diagram diagram, DefectType defectType,
                                                        QuantityConfigValue value) throws ConfigurationException{

        Supplier<Long> actualCount = () -> diagram.getEdges()
                .stream()
                .filter(Edge::isAttributeConnection)
                .filter(Edge::hasCardinality)
                .count();

        return quantityDefectTemplate(defectType, value, actualCount);
    }

    /**
     * Counts the number of entities in a given diagram.
     * @param diagram Diagram to be checked for occurrences
     * @param defectType Type of defect checked
     * @param value configuration value for this check
     * @return QuantityDefect describing the expected and actual number of occurrences
     * @throws ConfigurationException if QuantityConfigValue contains invalid minimum and maximum number of occurrences
     */
    public static Defect checkEntityCount(Diagram diagram, DefectType defectType,
                                                QuantityConfigValue value) throws ConfigurationException{

        Supplier<Long> actualCount = ()-> diagram.getVertices().stream()
                    .filter(Vertex::isEntity)
                    .count();

        return quantityDefectTemplate(defectType, value, actualCount);
    }

    /**
     * Counts the number of relationships in a given diagram.
     * @param diagram Diagram to be checked for occurrences
     * @param defectType Type of defect checked
     * @param value configuration value for this check
     * @return QuantityDefect describing the expected and actual number of occurrences
     * @throws ConfigurationException if QuantityConfigValue contains invalid minimum and maximum number of occurrences
     */
    public static Defect checkRelationshipCount(Diagram diagram, DefectType defectType,
                                                QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actualCount = () -> (long) diagram.getRelationships().size();

        return quantityDefectTemplate(defectType, value, actualCount);
    }

    /**
     * Counts the number of recursive relationship in a given diagram.
     * @param diagram Diagram to be checked for occurrences
     * @param defectType Type of defect checked
     * @param value configuration value for this check
     * @return QuantityDefect describing the expected and actual number of occurrences
     * @throws ConfigurationException if QuantityConfigValue contains invalid minimum and maximum number of occurrences
     */
    public static Defect checkRecursiveRelationshipCount(Diagram diagram, DefectType defectType,
                                                         QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actualCount = () -> diagram.getRelationships().stream()
                .filter(Relationship::isRecursive)
                .count();

        return quantityDefectTemplate(defectType, value, actualCount);
    }

    /**
     * Counts the number of structured attributes in a diagram
     * @param diagram Diagram to be checked for occurrences
     * @param defectType Type of defect checked
     * @param value configuration value for this check
     * @return QuantityDefect describing the expected and actual number of occurrences
     * @throws ConfigurationException if QuantityConfigValue contains invalid minimum and maximum number of occurrences
     */
    public static Defect checkStructuredAttributeCount(Diagram diagram, DefectType defectType,
                                                       QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actualCount = () -> diagram.getAttributes().stream()
                .filter(attribute ->
                        attribute.getAdjacentVertices().stream()
                                .anyMatch(vertex -> vertex.isEntity() || vertex.isRelationship()))
                .filter(attribute -> attribute.getAdjacentVertices().stream()
                        .anyMatch(Vertex::isAttribute))
                .count();

        return quantityDefectTemplate(defectType, value, actualCount);
    }

    /**
     * Counts the number of attributes in a given diagram.
     * @param diagram Diagram to be checked for occurrences
     * @param defectType Type of defect checked
     * @param value configuration value for this check
     * @return QuantityDefect describing the expected and actual number of occurrences
     * @throws ConfigurationException if QuantityConfigValue contains invalid minimum and maximum number of occurrences
     */
    public static Defect checkAttributeCount(Diagram diagram, DefectType defectType,
                                             QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actualCount = () -> (long) diagram.getAttributes().size();

        return quantityDefectTemplate(defectType, value, actualCount);
    }

    /**
     * Counts the number of N-ary relationships in a given diagram.
     * N-arity of a relationship is given by configuration.
     * @param diagram Diagram to be checked for occurrences
     * @param defectType Type of defect checked
     * @param value configuration value for this check
     * @return QuantityDefect describing the expected and actual number of occurrences
     * @throws ConfigurationException if QuantityConfigValue contains invalid minimum and maximum number of occurrences
     */
    public static Defect checkWeakEntityCount(Diagram diagram, DefectType defectType,
                                              QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actual = () ->
                diagram.getEntities().stream()
                        .filter(Entity::getIsWeak)
                        .count();

        return quantityDefectTemplate(defectType, value, actual);
    }

    /**
     *
     * @param diagram Diagram to be checked for occurrences
     * @param defectType Type of defect checked
     * @param value configuration value for this check
     * @return QuantityDefect describing the expected and actual number of occurrences
     * @throws ConfigurationException if QuantityConfigValue contains invalid minimum and maximum number of occurrences
     */
    public static Defect checkNaryRelationshipCount(Diagram diagram, DefectType defectType,
                                                    NaryRelationshipConfigValue value) throws ConfigurationException{

        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();
        var resultingDefectBuilder = QuantityDefect.quantityBuilder();

        int expectedMin = value.getMin();
        int expectedMax = value.getMax();

        if(expectedMax < expectedMin){
            info.append(String.format("Minimum count %s is higher than maximum %s", expectedMin, expectedMax));
            throw new ConfigurationException(info.toString());
        }

        long actualCount = diagram.getRelationships().stream()
                .filter(relationship ->
                        value.getEdges() <= relationship.getAdjacentVertices().stream()
                            .filter(Vertex::isEntity)
                            .distinct().count())
                .count();

        if(actualCount < expectedMin || actualCount > expectedMax){
            info.append(String.format("Counting relationships with at least %s edges.", value.getEdges()));
            info.append(String.format(" Expected %s  <%s, %s> was %s", defectType.getMessage(),
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

    /**
     * Counts the number of entities with multiple identifiers.
     * @param diagram Diagram to be checked for occurrences
     * @param defectType Type of defect checked
     * @param value configuration value for this check
     * @return QuantityDefect describing the expected and actual number of occurrences
     * @throws ConfigurationException if QuantityConfigValue contains invalid minimum and maximum number of occurrences
     */
    public static Defect checkMultipleIdentificationsCount(Diagram diagram, DefectType defectType,
                                                           QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actualCount = () -> diagram.getEntities().stream()
                .filter(entity -> entity.getKeys().size() > 1)
                .count();

        return quantityDefectTemplate(defectType, value, actualCount);
    }

    /**
     * Counts the number of composite keys in a given diagram.
     * @param diagram Diagram to be checked for occurrences
     * @param defectType Type of defect checked
     * @param value configuration value for this check
     * @return QuantityDefect describing the expected and actual number of occurrences
     * @throws ConfigurationException if QuantityConfigValue contains invalid minimum and maximum number of occurrences
     */
    public static Defect checkCompositeCount(Diagram diagram, DefectType defectType,
                                             QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actual = () ->
                (long) diagram.getComposites().size()
                +
                diagram.getAttributes().stream()
                        .filter(Attribute::isStructured)
                        .filter(Attribute::getIsKey)
                        .filter(Attribute::isSimple)
                        .count();

        return quantityDefectTemplate(defectType, value, actual);
    }

    /**
     * Counts the number of Hierarchies in a given diagram.
     * More specifically counts the number of ancestors.
     * @param diagram Diagram to be checked for occurrences
     * @param defectType Type of defect checked
     * @param value configuration value for this check
     * @return QuantityDefect describing the expected and actual number of occurrences
     * @throws ConfigurationException if QuantityConfigValue contains invalid minimum and maximum number of occurrences
     */
    public static Defect checkHierarchyCount(Diagram diagram, DefectType defectType,
                                             QuantityConfigValue value) throws ConfigurationException{
        Supplier<Long> actualCount = () -> diagram.getEdges().stream()
                .filter(Edge::isGeneralization)
                .map(Edge::getTarget)
                .distinct()
                .count();

        return quantityDefectTemplate(defectType, value, actualCount);
    }
}
