package corrector;

import enums.DefectType;
import lombok.Data;
import lombok.extern.java.Log;
import model.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * The BasicCorrector class and its methods are used
 */
@Log
public class BasicCorrector {

    private final Diagram diagram;
    private final Properties properties;
    private final List<Defect> defects = new LinkedList<>();

    public BasicCorrector(Diagram diagram, Properties properties){
        this.diagram = diagram;
        this.properties = properties;
    }

    public List<Defect> findDefects(){
        checkCardinalities();
        checkDiagramComponent();
        checkEntityIdentification();
        checkWeakEntities();
        checkNamedVertices();
        checkDuplicateNames();
        checkDuplicateAttributes();
        return defects;
    }

    /**
     * For finding diagram connectivity. Diagram should have only one component.
     */
    private void checkDiagramComponent(){
        log.log(Level.FINE, "Checking diagram component");
        defects.add(new Defect(DefectType.ONE_COMPONENT, true, "Diagram has more than one component."));
    }

    /**
     * For finding out if every non-weak entity has an identifier
     */
    private void checkEntityIdentification(){
        log.log(Level.FINE, "Checking entity identification");
        defects.add(new Defect(DefectType.EVERY_ENTITY_IDENTIFIED, true, "Diagram contains entities without keys"));
    }

    /**
     * For finding out if weak entities are properly identified by their composite key.
     * Weak entity must be connected to relationship with non-weak entity with 1..1 cardinality
     */
    private void checkWeakEntities(){
        log.log(Level.FINE, "Checking weak entities");
        defects.add(new Defect(DefectType.WEAK_ENTITY_IDENTIFIED, true, "Weak entities are misused"));
    }

    /**
     * For finding out if cardinalities are present where they should be.
     * Entity - Relationship connections
     */
    private void checkCardinalities(){
        log.log(Level.FINE, "Checking connection cardinalities");
        StringBuilder builder = new StringBuilder();
        diagram.getVertices().stream()
                .filter(dataClass -> dataClass.isRelationship())
                .forEach(dataClass -> {
                    List<Connection> withoutCardinality = dataClass.getConnections().stream()
                            .filter(connection -> !connection.isAttributeConnection())
                            .filter(connection -> !connection.hasCardinality())
                            .collect(Collectors.toList());
                    withoutCardinality.forEach(connection -> builder.append(connection.toString() + "\n"));
        });
        if(builder.isEmpty()){
            defects.add(new Defect(DefectType.CARDINALITIES_PRESENT, false, ""));
        }
        defects.add(new Defect(DefectType.CARDINALITIES_PRESENT, true, builder.toString()));
    }

    /**
     * For finding out if there are duplicate entity and relationship names
     */
    private void checkDuplicateNames(){
        log.log(Level.FINE, "Checking connection cardinalities");
        List<String> uniques = diagram.getVertices().stream()
                .filter(dataClass -> dataClass.isAttribute())
                .map(dataClass -> dataClass.getName().strip())
                .distinct()
                .collect(Collectors.toList());

        Long actualCount =  diagram.getVertices()
                .stream()
                .filter(dataClass -> !dataClass.isAttribute()).count();

        if(uniques.size() == actualCount){
            defects.add(new Defect(DefectType.NO_DUPLICATE_NAMES, false, ""));
        }
        else{
            StringBuilder builder = new StringBuilder();
            builder.append("Duplicate names:\n");

            for(String name : uniques){
                List<DataClass> sameNames = diagram.getVertices().stream()
                        .filter(dataClass -> !dataClass.isAttribute())
                        .filter(dataClass -> dataClass.getName().strip().equals(name))
                        .collect(Collectors.toList());

                if(sameNames.size()>1){
                    builder.append(String.format("\t\tName: \"%s\"\n",name));
                }
            }

            defects.add(new Defect(DefectType.NO_DUPLICATE_NAMES, true, builder.toString()));
        }
    }

    /**
     * For finding duplicate attribute names on one Entity or Relationship
     */
    private void checkDuplicateAttributes(){
        log.log(Level.FINE, "Checking connection cardinalities");
        StringBuilder builder = new StringBuilder();
        diagram.getVertices()
                .stream()
                .filter(dataClass -> !dataClass.isAttribute())
                .forEach(dataClass -> {
                    Long attributeCount = dataClass.getAdjacentDataClasses()
                            .stream()
                            .filter(vert -> vert.isAttribute())
                            .count();

                    Long uniqueCount = dataClass.getAdjacentDataClasses()
                            .stream()
                            .filter(vert -> vert.isAttribute())
                            .map(attribute -> attribute.getName())
                            .distinct()
                            .count();
                    if(attributeCount != uniqueCount){
                        builder.append(String.format("%s has multiple attributes with the same name\n", dataClass.getName()));
                    }
        });
        if(builder.isEmpty()){
            defects.add(new Defect(DefectType.NO_DUPLICATE_ATTRIBUTES, false, ""));
        }
        else{
            defects.add(new Defect(DefectType.NO_DUPLICATE_ATTRIBUTES, true, builder.toString()));
        }
    }

    /**
     * For finding out if every vertex is named
     */
    private void checkNamedVertices(){
        log.log(Level.FINE, "Checking connection cardinalities");
        List<DataClass> unnamed = diagram.getVertices().stream()
                .filter(dataClass -> dataClass.getName().matches("\s?"))
                .collect(Collectors.toList());
        if(unnamed.isEmpty()){
            defects.add(new Defect(DefectType.NAMED_VERTICES, false, ""));
        }
        else{
            StringBuilder builder = new StringBuilder();
            builder.append("Unnamed entities:\n");
            unnamed.forEach(dataClass -> builder.append(String.format("\t\t%s\n",dataClass.toString())));
            defects.add(new Defect(DefectType.NAMED_VERTICES, true,
                    builder.toString()));
        }
    }
}
