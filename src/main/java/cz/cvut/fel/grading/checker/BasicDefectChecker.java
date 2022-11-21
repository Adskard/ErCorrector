package cz.cvut.fel.grading.checker;

import cz.cvut.fel.grading.configuration.value.ConfigValue;
import cz.cvut.fel.grading.defect.BasicDefect;
import cz.cvut.fel.grading.defect.Defect;
import cz.cvut.fel.enums.Cardinality;
import cz.cvut.fel.enums.DefectType;
import cz.cvut.fel.model.*;
import lombok.extern.java.Log;
import java.util.*;
import java.util.stream.Collectors;

@Log
public class BasicDefectChecker {


    public static Defect checkMultivaluedAttributes(Diagram diagram, DefectType defectType, ConfigValue value){

        StringBuilder info = new StringBuilder();
        boolean defectPresence = false;
        List<Attribute> problematicAttributes = new LinkedList<>();

        var resultingDefectBuilder = BasicDefect.<Attribute>basicBuilder();

        float taskPoints = value.getPoints();

        List<Connection> multivaluedConnections = diagram.getEdges().stream()
                .filter(Connection::isAttributeConnection)
                .filter(Connection::hasCardinality)
                .collect(Collectors.toList());

        if(multivaluedConnections.isEmpty()){
            info.append("No multivalued attributes present, no points added");
            return resultingDefectBuilder.type(defectType)
                    .points(taskPoints)
                    .additionalInfo(info.toString())
                    .incorrectObjects(problematicAttributes)
                    .present(true)
                    .build();
        }

        List<Connection> connectionsWithIllegalCardinality =  multivaluedConnections.stream()
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

        float points = value.getPoints();

        if(diagram.getVertices().isEmpty()){
            info.append("Diagram has no vertices");
            return resultingDefectBuilder
                    .type(defectType)
                    .present(true)
                    .additionalInfo(info.toString())
                    .points(points)
                    .incorrectObjects(new ArrayList<>())
                    .build();
        }

        List<DataClass> notInMainComponent = diagram.getMissingVerticesFromMainComponent();

        if(!notInMainComponent.isEmpty()){
            info.append(String.format("Vertices not in main component: %s", notInMainComponent));
            defectPresence = true;
        }



        return resultingDefectBuilder
                .type(defectType)
                .present(defectPresence)
                .additionalInfo(info.toString())
                .points(points)
                .incorrectObjects(notInMainComponent)
                .build();
    }

    /**
     * For finding out if every non-weak entity has an identifier
     */
    public static Defect checkAllEntityIds(Diagram diagram, DefectType defectType, ConfigValue value){

        boolean defectPresence = false;

        var resultingDefectBuilder = BasicDefect.<DataClass>basicBuilder();

        float points = value.getPoints();

        if(diagram.getVertices().isEmpty()){
            return resultingDefectBuilder
                    .type(defectType)
                    .present(true)
                    .additionalInfo("Diagram has no vertices")
                    .points(points)
                    .incorrectObjects(new ArrayList<>())
                    .build();
        }

        List<DataClass> entitiesWithoutKeys = diagram.getEntities().stream()
                .filter(entity -> !entity.hasIdentifier())
                .collect(Collectors.toList());

        if(!entitiesWithoutKeys.isEmpty()){
            defectPresence = true;
        }

        return resultingDefectBuilder
                .type(defectType)
                .additionalInfo("")
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

        var resultingDefectBuilder = BasicDefect.<Entity>basicBuilder();

        float points = value.getPoints();

        if(diagram.getEntities().stream().noneMatch(Entity::isWeakEntity)){

            return resultingDefectBuilder
                    .type(defectType)
                    .additionalInfo("No weak entities present in diagram")
                    .present(true)
                    .incorrectObjects(new ArrayList<>())
                    .points(points)
                    .build();
        }

        List<Entity> incorrectUses = diagram.getEntities().stream()
                .filter(Entity::isWeakEntity)
                .filter(entity -> entity.getKeys().stream()
                            .filter(key -> !key.isSimple())
                            .map(key -> (Composite) key)
                            .noneMatch(Composite::isWeakIdentifier))
                .collect(Collectors.toList());

        if(!incorrectUses.isEmpty()){
            defectPresence = true;
        }

        return resultingDefectBuilder
                .type(defectType)
                .additionalInfo("")
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

        var resultingDefectBuilder = BasicDefect.<Connection>basicBuilder();
        float points = value.getPoints();

        if(diagram.getEdges().stream().noneMatch(Connection::isRelationshipConnection)){
            return resultingDefectBuilder
                    .type(defectType)
                    .additionalInfo("No relationship connection present in diagram")
                    .present(true)
                    .incorrectObjects(new ArrayList<>())
                    .points(points)
                    .build();
        }

        List<Connection> connectionsWithoutCardinality = diagram.getEdges().stream()
                .filter(Connection::isRelationshipConnection)
                .filter(connection -> !connection.hasCardinality())
                .collect(Collectors.toList());

        if(!connectionsWithoutCardinality.isEmpty()){
            defectPresence = true;
        }

        return resultingDefectBuilder
                .type(defectType)
                .additionalInfo("")
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

        var resultingDefectBuilder = BasicDefect.<DataClass>basicBuilder();
        float points = value.getPoints();

        if(diagram.getEntities().isEmpty()){
            return resultingDefectBuilder
                    .type(defectType)
                    .additionalInfo("No vertices in diagram")
                    .present(true)
                    .incorrectObjects(new ArrayList<>())
                    .points(points)
                    .build();
        }

        Set<DataClass> unique = new HashSet<>();

        List<DataClass> duplicate = diagram.getVertices().stream()
                .filter(dataClass -> !unique.add(dataClass))
                .collect(Collectors.toList());

        if(!duplicate.isEmpty()){
            defectPresence = true;
        }

        return resultingDefectBuilder
                .type(defectType)
                .additionalInfo("")
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

        float points = value.getPoints();

        if(diagram.getAttributes().isEmpty()){
            info.append("No attributes in diagram");

            return resultingDefectBuilder
                    .type(defectType)
                    .additionalInfo(info.toString())
                    .present(true)
                    .incorrectObjects(new ArrayList<>())
                    .points(points)
                    .build();
        }

        List<Attribute> duplicateAttributes = new LinkedList<>();

        for(DataClass dataClass : diagram.getVertices()){
            List<Attribute> duplicates = findDuplicateAttributesOnVertex(dataClass);

            if(!duplicates.isEmpty()){
                List<String> duplicateNames = duplicates.stream()
                        .map(Attribute::getName)
                        .collect(Collectors.toList());

                duplicateAttributes.addAll(duplicates);

                info.append(String.format("%s are duplicate attribute names on %s\n",
                        duplicateNames, dataClass.getName()));
            }
        }

        if(!duplicateAttributes.isEmpty()){
            defectPresence = true;
        }

        return resultingDefectBuilder
                .type(defectType)
                .additionalInfo(info.toString())
                .present(defectPresence)
                .incorrectObjects(duplicateAttributes)
                .points(points)
                .build();
    }

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

        var resultingDefectBuilder = BasicDefect.<DataClass>basicBuilder();

        float points = value.getPoints();

        if(diagram.getEntities().isEmpty()){
            return resultingDefectBuilder
                    .type(defectType)
                    .additionalInfo("No vertices in diagram")
                    .present(true)
                    .incorrectObjects(new ArrayList<>())
                    .points(points)
                    .build();
        }

        List<DataClass> unnamedVertices = diagram.getVertices().stream()
                .filter(dataClass -> dataClass.getName().isEmpty() || dataClass.getName().isBlank())
                .collect(Collectors.toList());

        if(!unnamedVertices.isEmpty()){
            defectPresence = true;
        }

        return resultingDefectBuilder
                .type(defectType)
                .additionalInfo("")
                .present(defectPresence)
                .incorrectObjects(unnamedVertices)
                .points(points)
                .build();
    }
}
