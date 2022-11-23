package cz.cvut.fel.grading.checker;

import cz.cvut.fel.enums.Coverage;
import cz.cvut.fel.enums.Disjointness;
import cz.cvut.fel.grading.configuration.value.ConfigValue;
import cz.cvut.fel.grading.defect.BasicDefect;
import cz.cvut.fel.enums.Cardinality;
import cz.cvut.fel.enums.DefectType;
import cz.cvut.fel.model.*;
import lombok.extern.java.Log;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class BasicDefectChecker a collection of static functions
 * used to check diagram for BasicDefects.
 *
 * @author Adam Skarda
 */
@Log
public class BasicDefectChecker {

    /**
     * Checks given diagram for generalization Coverage and Disjointness validity
     * @param diagram diagram to be checked
     * @param defectType DefectType associated with this check
     * @param value configuration value for this defect type
     * @return Defect describing which ancestor Entities had hierarchies without proper coverage and disjointness annotation
     */
    public static BasicDefect<Entity> checkHierarchyAnnotation(Diagram diagram, DefectType defectType, ConfigValue value){
        StringBuilder info = new StringBuilder();
        boolean defectPresence = false;

        var resultingDefectBuilder = BasicDefect.<Entity>basicBuilder();

        float taskPoints = value.getPoints();

        List<Entity> problematicAncestors = diagram.getEdges().stream()
                .filter(Edge::isGeneralization)
                .map(generalization -> (Generalization) generalization)
                .filter(generalization -> !generalization.getCoverage().equals(Coverage.NOT_RECOGNIZED) ||
                        !generalization.getDisjointness().equals(Disjointness.NOT_RECOGNIZED))
                .map(Edge::getTarget)
                .map(vertex -> (Entity) vertex)
                .collect(Collectors.toList());

        if(!problematicAncestors.isEmpty()){
            defectPresence = true;
            info.append("Disjointness or Coverage information not recognized or missing.");
        }

        return resultingDefectBuilder.type(defectType)
                .points(taskPoints)
                .additionalInfo(info.toString())
                .incorrectObjects(problematicAncestors)
                .present(defectPresence)
                .build();
    }


    /**
     * Checks given diagram for multivalued attributes with illegal cardinality 1..1
     * @param diagram diagram to be checked
     * @param defectType DefectType associated with this check
     * @param value configuration value for this defect type
     * @return Defect describing which Attributes had invalid cardinality
     */
    public static BasicDefect<Attribute> checkMultivaluedAttributes(Diagram diagram, DefectType defectType, ConfigValue value){

        StringBuilder info = new StringBuilder();
        boolean defectPresence = false;
        List<Attribute> problematicAttributes = new LinkedList<>();

        var resultingDefectBuilder = BasicDefect.<Attribute>basicBuilder();

        float taskPoints = value.getPoints();

        List<Edge> multivaluedEdges = diagram.getEdges().stream()
                .filter(Edge::isAttributeConnection)
                .filter(Edge::hasCardinality)
                .collect(Collectors.toList());

        if(multivaluedEdges.isEmpty()){
            info.append("No multivalued attributes present, no points added");
            return resultingDefectBuilder.type(defectType)
                    .points(taskPoints)
                    .additionalInfo(info.toString())
                    .incorrectObjects(problematicAttributes)
                    .present(true)
                    .build();
        }

        List<Edge> edgesWithIllegalCardinality =  multivaluedEdges.stream()
                    .filter(edge -> edge.getCardinality().equals(Cardinality.ONE))
                    .collect(Collectors.toList());

        if(!edgesWithIllegalCardinality.isEmpty()){
            problematicAttributes.addAll(edgesWithIllegalCardinality.stream()
                    .map(edge -> (Attribute)edge.getSource())
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
     * Resulting defect has all Vertices that were not present in the main diagram component.
     * @param diagram diagram to be checked
     * @param defectType DefectType associated with this check
     * @param value configuration value for this defect type
     * @return Defect describing which Vertices were not found in main diagram component
     */
    public static BasicDefect<Vertex> checkDiagramComponent(Diagram diagram, DefectType defectType, ConfigValue value){

        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = BasicDefect.<Vertex>basicBuilder();

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

        List<Vertex> notInMainComponent = diagram.getMissingVerticesFromMainComponent();

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
     * For finding out if every entity has an identifier.
     * @param diagram diagram to be checked
     * @param defectType DefectType associated with this check
     * @param value configuration value for this defect type
     * @return Defect describing which Entities were modeled improperly
     */
    public static BasicDefect<Entity> checkAllEntityIds(Diagram diagram, DefectType defectType, ConfigValue value){

        boolean defectPresence = false;

        var resultingDefectBuilder = BasicDefect.<Entity>basicBuilder();

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

        List<Entity> entitiesWithoutKeys = diagram.getEntities().stream()
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
     * Weak entity must be connected to relationship with 1..1 cardinality through a composite key.
     * Composite key has to have an attribute member.
     * @param diagram diagram to be checked
     * @param defectType DefectType associated with this check
     * @param value configuration value for this defect type
     * @return Defect describing which weak entities were modeled improperly
     */
    public static BasicDefect<Entity> checkWeakEntities(Diagram diagram, DefectType defectType, ConfigValue value){

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
     * For finding out if cardinalities are present on every Entity - Relationship Edge.
     * @param diagram diagram to be checked
     * @param defectType DefectType associated with this check
     * @param value configuration value for this defect type
     * @return Defect describing which Relationship-Entity Edge did not have a valid Cardinality
     */
    public static BasicDefect<Edge> checkCardinalities(Diagram diagram, DefectType defectType, ConfigValue value){

        boolean defectPresence = false;

        var resultingDefectBuilder = BasicDefect.<Edge>basicBuilder();
        float points = value.getPoints();

        if(diagram.getEdges().stream().noneMatch(Edge::isRelationshipConnection)){
            return resultingDefectBuilder
                    .type(defectType)
                    .additionalInfo("No relationship connection present in diagram")
                    .present(true)
                    .incorrectObjects(new ArrayList<>())
                    .points(points)
                    .build();
        }

        List<Edge> edgesWithoutCardinality = diagram.getEdges().stream()
                .filter(Edge::isRelationshipConnection)
                .filter(edge -> !edge.hasCardinality())
                .collect(Collectors.toList());

        if(!edgesWithoutCardinality.isEmpty()){
            defectPresence = true;
        }

        return resultingDefectBuilder
                .type(defectType)
                .additionalInfo("")
                .present(defectPresence)
                .incorrectObjects(edgesWithoutCardinality)
                .points(points)
                .build();
    }

    /**
     * For finding out if there are duplicate entity and relationship names in diagram.
     * @param diagram diagram to be checked
     * @param defectType DefectType associated with this check
     * @param value configuration value for this defect type
     * @return Defect describing which Vertices have duplicate names
     */
    public static BasicDefect<Vertex> checkDuplicateNames(Diagram diagram, DefectType defectType, ConfigValue value){

        boolean defectPresence = false;

        var resultingDefectBuilder = BasicDefect.<Vertex>basicBuilder();
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

        Set<String> unique = new HashSet<>();

        List<Vertex> duplicate = diagram.getVertices().stream()
                .filter(vertex -> !vertex.isAttribute())
                .filter(vertex -> !unique.add(vertex.getName()))
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
     * @param diagram diagram to be checked
     * @param defectType DefectType associated with this check
     * @param value configuration value for this defect type
     * @return Defect describing which Vertices have what duplicate names
     */
    public static BasicDefect<Attribute> checkDuplicateAttributes(Diagram diagram, DefectType defectType, ConfigValue value){

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

        for(Vertex vertex : diagram.getVertices()){
            List<Attribute> duplicates = findDuplicateAttributesOnVertex(vertex);

            if(!duplicates.isEmpty()){
                List<String> duplicateNames = duplicates.stream()
                        .map(Attribute::getName)
                        .distinct()
                        .collect(Collectors.toList());

                duplicateAttributes.addAll(duplicates);

                info.append(String.format("%s are duplicate attribute names on %s.",
                        duplicateNames, vertex.getName()));
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

    /**
     * For finding duplicate attribute names on a Vertex
     * @param vertex vertex on which to look for duplicate attribute names
     * @return list of duplicate names without the first occurrance
     */
    private static List<Attribute> findDuplicateAttributesOnVertex(Vertex vertex){
        Set<String> uniqueNames = new HashSet<>();
        List<Attribute> all = vertex.getAdjacentVertices().stream()
                .filter(Vertex::isAttribute)
                .map(attribute -> (Attribute) attribute)
                .collect(Collectors.toList());

        return all.stream()
                .filter(attribute -> !uniqueNames.add(attribute.getName()))
                .collect(Collectors.toList());
    }

    /**
     * For finding out if every vertex has a name that is not blank
     * @param diagram diagram to be checked
     * @param defectType DefectType associated with this check
     * @param value configuration value for this defect type
     * @return Defect describing which Vertices have no names
     */
    public static BasicDefect<Vertex> checkNamedVertices(Diagram diagram, DefectType defectType, ConfigValue value){

        boolean defectPresence = false;

        var resultingDefectBuilder = BasicDefect.<Vertex>basicBuilder();

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

        List<Vertex> unnamedVertices = diagram.getVertices().stream()
                .filter(vertex -> vertex.getName().isEmpty() || vertex.getName().isBlank())
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
