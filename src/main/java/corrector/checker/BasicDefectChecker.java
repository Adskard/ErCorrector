package corrector.checker;

import corrector.configuration.value.ConfigValue;
import corrector.defect.BasicDefect;
import corrector.defect.Defect;
import enums.Cardinality;
import enums.DefectType;
import lombok.Data;
import lombok.extern.java.Log;
import model.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public class BasicDefectChecker {

    /**
     *
     * @param diagram
     * @param defectType
     * @param value
     * @return
     */
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

    /**
     * For finding diagram connectivity. Diagram should have only one component.
     * It achieves this by doing a DFS and comparing visited nodes to diagram vertices.
     */
    public static Defect checkDiagramComponent(Diagram diagram, DefectType defectType, ConfigValue value){

        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = BasicDefect.<DataClass>basicBuilder();

        List<DataClass> notInMainComponent = diagram.getMissingVerticesFromMainComponent();

        if(!notInMainComponent.isEmpty()){
            info.append(String.format("Vertices not in main component: %s", notInMainComponent));
            defectPresence = true;
        }

        return resultingDefectBuilder
                .type(defectType)
                .present(defectPresence)
                .additionalInfo(info.toString())
                .incorrectObjects(notInMainComponent)
                .build();
    }

    /**
     * For finding out if every non-weak entity has an identifier
     */
    public static Defect checkAllEntityIds(Diagram diagram, DefectType defectType, ConfigValue value){

        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = BasicDefect.<DataClass>basicBuilder();

        List<DataClass> entitiesWithoutKeys = diagram.getEntities().stream()
                .filter(entity -> entity.getKeys().isEmpty())
                .collect(Collectors.toList());

        if(!entitiesWithoutKeys.isEmpty()){
            defectPresence = true;
        }

        Float points = value.getPoints();

        return resultingDefectBuilder
                .type(defectType)
                .additionalInfo(info.toString())
                .present(defectPresence)
                .incorrectObjects(entitiesWithoutKeys)
                .points(points)
                .build();
    }

    /**
     * For finding out if weak entities are properly identified by their composite key.
     * Weak entity must be connected to relationship with non-weak entity with 1..1 cardinality
     */
    public static Defect checkWeakEntities(Diagram diagram, DefectType defectType, ConfigValue value){

        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = BasicDefect.<Entity>basicBuilder();
        //TODO
        List<Entity> incorrectUses = new LinkedList<>();

        Float points = value.getPoints();

        return resultingDefectBuilder
                .type(defectType)
                .additionalInfo(info.toString())
                .present(defectPresence)
                .incorrectObjects(incorrectUses)
                .points(points)
                .build();

    }

    /**
     * For finding out if cardinalities are present where they should be.
     * Entity - Relationship connections
     */
    public static Defect checkCardinalities(Diagram diagram, DefectType defectType, ConfigValue value){

        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = BasicDefect.<Connection>basicBuilder();

        List<Connection> connectionsWithoutCardinality = diagram.getEdges().stream()
                .filter(Connection::isRelationshipConnection)
                .filter(connection -> !connection.hasCardinality())
                .collect(Collectors.toList());

        if(!connectionsWithoutCardinality.isEmpty()){
            defectPresence = true;
        }

        Float points = value.getPoints();

        return resultingDefectBuilder
                .type(defectType)
                .additionalInfo(info.toString())
                .present(defectPresence)
                .incorrectObjects(connectionsWithoutCardinality)
                .points(points)
                .build();
    }

    /**
     * For finding out if there are duplicate entity and relationship names
     */
    public static Defect checkDuplicateNames(Diagram diagram, DefectType defectType, ConfigValue value){

        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = BasicDefect.<DataClass>basicBuilder();
        Set<DataClass> unique = new HashSet<>();

        List<DataClass> duplicate = diagram.getVertices().stream()
                .filter(dataClass -> !unique.add(dataClass))
                .collect(Collectors.toList());

        if(!duplicate.isEmpty()){
            defectPresence = true;
        }

        Float points = value.getPoints();

        return resultingDefectBuilder
                .type(defectType)
                .additionalInfo(info.toString())
                .present(defectPresence)
                .incorrectObjects(duplicate)
                .points(points)
                .build();
    }

    /**
     * For finding duplicate attribute names on one Entity or Relationship
     */
    public static Defect checkDuplicateAttributes(Diagram diagram, DefectType defectType, ConfigValue value){

        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = BasicDefect.<Attribute>basicBuilder();

        List<Attribute> duplicateAttributes = new LinkedList<>();

        diagram.getVertices().stream()
                .forEach(dataClass -> {
                    List<Attribute> duplicates = findDuplicateAttributesOnVertex(dataClass);

                    if(!duplicates.isEmpty()){
                        List<String> duplicateNames = duplicates.stream()
                                .map(Attribute::getName)
                                .collect(Collectors.toList());

                        duplicateAttributes.addAll(duplicates);

                        info.append(String.format("%s are duplicate attribute names on %s\n",
                                duplicateNames, dataClass.getName()));
                    }
                });

        if(!duplicateAttributes.isEmpty()){
            defectPresence = true;
        }

        Float points = value.getPoints();

        return resultingDefectBuilder
                .type(defectType)
                .additionalInfo(info.toString())
                .present(defectPresence)
                .incorrectObjects(duplicateAttributes)
                .points(points)
                .build();
    }

    /**
     *
     * @param vertex
     * @return
     */
    private static List<Attribute> findDuplicateAttributesOnVertex(DataClass vertex){
        List<Attribute> all = vertex.getAdjacentDataClasses().stream()
                .filter(DataClass::isAttribute)
                .map(dataClass -> (Attribute) dataClass)
                .collect(Collectors.toList());

        List<Attribute> distinct = vertex.getAdjacentDataClasses().stream()
                .filter(DataClass::isAttribute)
                .map(dataClass -> (Attribute) dataClass)
                .distinct()
                .collect(Collectors.toList());

        return all.stream()
                .filter(unique -> !distinct.contains(unique))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * For finding out if every vertex is named
     */
    public static Defect checkNamedVertices(Diagram diagram, DefectType defectType, ConfigValue value){

        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = BasicDefect.<DataClass>basicBuilder();

        List<DataClass> unnamedVertices = diagram.getVertices().stream()
                .filter(dataClass -> dataClass.getName().isEmpty() || dataClass.getName().isBlank())
                .collect(Collectors.toList());

        if(!unnamedVertices.isEmpty()){
            defectPresence = true;
        }

        Float points = value.getPoints();

        return resultingDefectBuilder
                .type(defectType)
                .additionalInfo(info.toString())
                .present(defectPresence)
                .incorrectObjects(unnamedVertices)
                .points(points)
                .build();
    }
}
