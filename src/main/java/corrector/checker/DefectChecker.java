package corrector.checker;

import corrector.configuration.ConfigExtractor;
import corrector.configuration.value.ConfigValue;
import corrector.configuration.value.NaryRelationshipConfigValue;
import corrector.configuration.value.QuantityConfigValue;
import corrector.configuration.value.UsageConfigValue;
import corrector.defect.BasicDefect;
import corrector.defect.Defect;
import corrector.defect.QuantityDefect;
import corrector.defect.UsageDefect;
import enums.Cardinality;
import enums.DefectType;

import lombok.Getter;
import lombok.extern.java.Log;
import model.*;
import model.Diagram;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 *
 * @author Adam Skarda
 */
@Log
@Getter
public class DefectChecker {

    private final List<Defect> defects = new LinkedList<>();
    private final Diagram diagram;
    private final ConfigExtractor extractor;

    public DefectChecker(Diagram diagram, ConfigExtractor extractor){
        this.diagram = diagram;
        this.extractor = extractor;
    }

    /**
     *
     * @return List of checked defects
     */
    public List<Defect> findDefects(){
        defects.clear();
        findBasicDefects();
        findAssignmentSpecificDefects();
        return defects;
    }

    /**
     * Checks for presence of basic ER modeling defects
     */
    private void findBasicDefects(){
        checkIllegalMultivaluedAttribute();
        checkCardinalities();
        checkDiagramComponent();
        checkAllEntityIds();
        checkWeakEntities();
        checkNamedVertices();
        checkDuplicateNames();
        checkDuplicateAttributes();
    }

    /**
     * Checks for presence of assignment specific defects
     */
    private void findAssignmentSpecificDefects(){
        checkMultivaluedAttributeCount();
        checkMultivaluedAttributeCardinalityUsage();
        checkEntityCount();
        checkRelationshipCount();
        checkAttributeCount();
        checkWeakEntityCount();
        checkHierarchyCount();
        checkHierarchyUsage();
        checkCompositeIdentifierCount();
        checkMultipleIdentifierCount();
        checkNaryRelationshipCount();
        checkRecursiveRelationshipCount();
        checkStructuredAttributeCount();
        checkCardinalityUsage();
    }


    /**
     * Checks presence of multivalued attributes in diagram and their count.
     */
    private void checkMultivaluedAttributeCount(){
        log.fine("Checking multivalued attribute count");

        DefectType defectType = DefectType.MULTIVALUED_ATTRIBUTE_COUNT;

        if(!extractor.isEnabledInConfig(defectType)){
            return;
        }

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkMultivaluedAttributeCount(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking multivalued attribute count!", ex);
        }
    }

    /**
     * Checks number of used entities in diagram.
     */
    private void checkEntityCount(){
        log.fine("Checking entity count");

        DefectType defectType = DefectType.ENTITY_COUNT;

        if (!extractor.isEnabledInConfig(defectType)) return;

        try {
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkEntityCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error during entity counting!", ex);
        }
    }

    /**
     * Checks for recursive relationships in diagram.
     */
    private void checkRecursiveRelationshipCount(){
        log.fine("Checking recursive relationship count");

        DefectType defectType = DefectType.RECURSIVE_RELATIONSHIP_COUNT;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkRecursiveRelationshipCount(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking recursive relationship count!", ex);
        }

    }

    /**
     * Checks diagram for weak entities and their count.
     */
    private void checkWeakEntityCount(){
        log.fine("Checking weak entity count");

        DefectType defectType = DefectType.WEAK_ENTITY_COUNT;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkWeakEntityCount(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking weak entity count!", ex);
        }
    }

    /**
     * Checks diagram for the number of composite identifiers.
     */
    private void checkCompositeIdentifierCount(){
        log.fine("Checking composite identification count");
        DefectType defectType = DefectType.COMPOSITE_ID_COUNT;
        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkCompositeCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting composite identifiers!", ex);
        }
    }

    /**
     * Checks structured attribute usage in diagram.
     */
    private void checkStructuredAttributeCount(){
        log.fine("Checking structured attribute count");
        DefectType defectType = DefectType.STRUCTURED_ATTRIBUTE_COUNT;
        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkStructuredAttributeCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting structured attributes!", ex);
        }
    }

    /**
     * Checks diagram for entities with multiple identifiers.
     */
    private void checkMultipleIdentifierCount(){
        log.fine("Checking entity with multiple identifiers count");
        DefectType defectType = DefectType.MULTIPLE_ID_COUNT;
        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkMultipleIdentificationsCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting entities with multiple identifiers!", ex);
        }
    }

    /**
     * Checks diagram for n-ary relationships.
     * N-ary relationship is between 3 or more entities.
     */
    private void checkNaryRelationshipCount(){
        log.fine("Check n-ary relationships");

        DefectType defectType = DefectType.N_ARY_RELATIONSHIP_COUNT;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue q = (QuantityConfigValue) extractor.getConfigValue(defectType);
            NaryRelationshipConfigValue value = (NaryRelationshipConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkNaryRelationshipCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting n-ary relationships!", ex);
        }
    }


    /**
     * Checks hierarchy count.
     */
    private void checkHierarchyCount(){
        log.fine("Checking hierarchy count");

        DefectType defectType = DefectType.HIERARCHY_COUNT;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkHierarchyCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting hierarchies!", ex);
        }
    }

    /**
     * Checks attribute count.
     */
    private void checkAttributeCount(){
        log.fine("Checking attribute count");

        DefectType defectType = DefectType.ORDINARY_ATTRIBUTE_COUNT;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkAttributeCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting attributes!", ex);
        }
    }

    /**
     * Checks relationship count.
     */
    private void checkRelationshipCount(){
        log.fine("Checking relationship count");

        DefectType defectType = DefectType.RELATIONSHIP_COUNT;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkRelationshipCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting relationships!", ex);
        }
    }

    /**
     * Checks cardinality usage on multivalued attributes
     */
    private void checkMultivaluedAttributeCardinalityUsage(){
        log.fine("Checking multivalued attribute cardinality usages");

        DefectType defectType = DefectType.MULTIVALUED_ATTRIBUTE_CARDINALITY_USAGE;

        if (!extractor.isEnabledInConfig(defectType)) return;

        try{
            UsageConfigValue<Cardinality> value = (UsageConfigValue<Cardinality>) extractor.getConfigValue(defectType);
            defects.add(UsageDefectChecker.checkMultivaluedAttributeCardinality(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking cardinalities of multivalued attributes!", ex);
        }
    }

    /**
     * Checks types of cardinalities used and their respective count.
     */
    private void checkCardinalityUsage(){
        /*log.fine("Checking cardinality usage");

        DefectType defectType = DefectType.CARDINALITY_TYPE_USAGE;
        boolean defectPresence = false;
        String defaultValue = "";
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = UsageDefect.<Cardinality>usageBuilder();

        if(!isEnabledInConfig(defectType.getConfigKey())) return;

        try{
            List<String> values = getConfigValues(defectType.getConfigKey(), defaultValue);

            List<Cardinality> expectedCardinalities = getCardinalitiesFromValues(values.subList(0,values.size()-1));

            List<Cardinality> actualCardinalities = diagram.getEdges().stream()
                    .filter(Connection::hasCardinality)
                    .map(Connection::getCardinality)
                    .distinct()
                    .collect(Collectors.toList());

            List<Cardinality> missingCardinalities = expectedCardinalities.stream()
                    .filter(cardinality -> !actualCardinalities.contains(cardinality))
                    .collect(Collectors.toList());

            if (!missingCardinalities.isEmpty()) {
                info.append(String.format("Missing cardinalities: %s", missingCardinalities));
                defectPresence = true;
            }

            float taskPoints = Float.parseFloat(values.get(values.size()-1));

            if(!defectPresence) points += taskPoints;

            defects.add(resultingDefectBuilder
                    .type(defectType)
                    .present(defectPresence)
                    .points(taskPoints)
                    .additionalInfo(info.toString())
                    .expected(expectedCardinalities)
                    .actual(actualCardinalities)
                    .build());
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error during cardinality usage checking!", ex);
        }*/
    }


    /**
     * Checks hierarchy usage in diagram. How many were used, which types were used.
     */
    private void checkHierarchyUsage(){

    }


    /**
     * Checks for cardinality pair usage..
     */
    private void checkCardinalityPairUsage(){

    }



    /**
     * For finding diagram connectivity. Diagram should have only one component.
     * It achieves this by doing a DFS and comparing visited nodes to diagram vertices.
     */
    private void checkDiagramComponent(){
        /*log.log(Level.FINE, "Checking diagram component");

        DefectType defectType = DefectType.ONE_COMPONENT;
        String defaultValue = "2.01";
        boolean defectPresence = false;
        StringBuilder info = new StringBuilder();

        var resultingDefectBuilder = BasicDefect.<DataClass>basicBuilder();

        try{
            List<String> values = getConfigValues(defectType.getConfigKey(), defaultValue);

            float taskPoints = Float.parseFloat(values.get(0));

            List<DataClass> notInMainComponent = getMissingVerticesFromMainComponent(diagram);

            if(!notInMainComponent.isEmpty()){
                info.append(String.format("Vertices not in main component: %s", notInMainComponent));
                defectPresence = true;
            }

            if (!defectPresence) points += taskPoints;

            defects.add(resultingDefectBuilder
                    .type(defectType)
                    .present(defectPresence)
                    .points(taskPoints)
                    .additionalInfo(info.toString())
                    .incorrectObjects(notInMainComponent)
                    .build());
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Exception during diagram component checking", ex);
        }*/
    }

    /**
     * Gets vertices not connected by an edge to main diagram component.
     * If resulting list is empty, then the diagram has a single component.
     * Implementation using DFS.
     * Main component is one that contains diagram vertex with index 0.
     *
     * @param diagram
     * @return list of vertices not in main component
     */
    private List<DataClass> getMissingVerticesFromMainComponent(Diagram diagram){
        List<DataClass> visited = new LinkedList<>();
        Stack<DataClass> toBeVisited = new Stack<>();
        DataClass first = diagram.getVertices().get(0);
        toBeVisited.push(first);

        //DFS
        while(!toBeVisited.isEmpty()){
            DataClass vertex = toBeVisited.pop();
            visited.add(vertex);

            for(DataClass adjacent : vertex.getAdjacentDataClasses()){
                if(!visited.contains(adjacent) && !toBeVisited.contains(adjacent)){
                    toBeVisited.push(adjacent);
                }
            }
        }

        return diagram.getVertices().stream()
                .filter(dataClass -> !visited.contains(dataClass))
                .collect(Collectors.toList());
    }

    /**
     * For finding out if every non-weak entity has an identifier
     */
    private void checkAllEntityIds(){
        log.log(Level.FINE, "Checking entity identification");

        List<DataClass> entitiesWithoutKeys = diagram.getEntities().stream()
                .filter(entity -> !entity.getIsWeak())
                .filter(entity -> !checkEntityOwnId(entity))
                .collect(Collectors.toList());

        if(!entitiesWithoutKeys.isEmpty()){
            String sep = "\n";
            StringBuilder builder = new StringBuilder("Diagram contains entities without keys:\n");
            entitiesWithoutKeys.forEach(dataClass -> builder.append(String.format("\t\t%s%s",dataClass.getName(),sep)));
            //defects.add(new Defect(DefectType.EVERY_ENTITY_IDENTIFIED, true, builder.toString(), ));
        }
        else{
            //defects.add(new Defect(DefectType.EVERY_ENTITY_IDENTIFIED, false, "Entities are identified", ));
        }
    }

    /**
     *
     * @param entity
     * @return true if entity is identified
     */
    private boolean checkEntityOwnId(Entity entity){
        boolean hasSimpleKey = entity.getKeys().stream()
                .anyMatch(Key::isSimple);
        if(hasSimpleKey){
            return true;
        }

        boolean hasNonWeakComposite = entity.getKeys().stream()
                .filter(key -> !key.isSimple())
                .map(key -> (Composite) key)
                .anyMatch(composite -> !composite.isRelationshipBased());
        if(hasNonWeakComposite){
            return true;
        }

        boolean hasAncestorId = entity.getAdjacentDataClasses().stream()
                .filter(DataClass::isEntity)
                .map(dataClass -> (Entity) dataClass)
                .anyMatch(entity1 -> checkEntityOwnId(entity1));
        return hasAncestorId;
    }


    /**
     * Checks if all attributes with cardinality connection are proper multivalued attributes
     * i.e. not cardinality 1..1.
     */
    private void checkIllegalMultivaluedAttribute(){
        log.fine("Checking multivalued attribute legality");

        DefectType defectType = DefectType.MULTIVALUED_ATTRIBUTE_ILLEGAL_CARDINALITY;

        if(!extractor.isEnabledInConfig(defectType))   return;

        try{
            ConfigValue value = extractor.getConfigValue(defectType);
            defects.add(BasicDefectChecker.checkMultivaluedAttributes(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking legality of multivalued attributes!", ex);
        }
    }

    /**
     * For finding out if weak entities are properly identified by their composite key.
     * Weak entity must be connected to relationship with non-weak entity with 1..1 cardinality
     */
    private void checkWeakEntities(){
        log.log(Level.FINE, "Checking weak entities");

        List<Entity> weakEntities = diagram.getEntities().stream()
                .filter(Entity::getIsWeak)
                .collect(Collectors.toList());

        List<Entity> wrongUsages = new LinkedList<>();

        for(Entity entity : weakEntities){
            if(!checkWeakEntityIdentification(entity)){
                wrongUsages.add(entity);
            }
        }

        if(wrongUsages.isEmpty()){
            //defects.add(new Defect(DefectType.WEAK_ENTITY_IDENTIFIED, false, "", ));
        }
        else{
            StringBuilder builder = new StringBuilder("Weak entities are misused:");
            wrongUsages.forEach(entity -> builder.append(String.format("\n\t\t%s",entity.getName())));
            //defects.add(new Defect(DefectType.WEAK_ENTITY_IDENTIFIED, true, builder.toString(), ));
        }

    }

    /**
     * For finding out if cardinalities are present where they should be.
     * Entity - Relationship connections
     */
    private void checkCardinalities(){
        log.log(Level.FINE, "Checking connection cardinalities");
        StringBuilder builder = new StringBuilder();
        diagram.getVertices().stream()
                .filter(DataClass::isRelationship)
                .forEach(dataClass -> {
                    List<Connection> withoutCardinality = dataClass.getConnections().stream()
                            .filter(connection -> !connection.isAttributeConnection())
                            .filter(connection -> !connection.hasCardinality())
                            .collect(Collectors.toList());
                    withoutCardinality.forEach(connection -> builder.append(String.format("%s\n", connection.toString())));
                });
        if(builder.isEmpty()){
            //defects.add(new Defect(DefectType.CARDINALITIES_PRESENT, false, "", ));
        }
        else{
            //defects.add(new Defect(DefectType.CARDINALITIES_PRESENT, true, builder.toString(), ));
        }
    }

    /**
     * For finding out if there are duplicate entity and relationship names
     */
    private void checkDuplicateNames(){
        log.log(Level.FINE, "Checking duplicate names");

        List<String> uniques = diagram.getVertices().stream()
                .filter(dataClass -> !dataClass.isAttribute())
                .map(dataClass -> dataClass.getName().strip())
                .distinct()
                .collect(Collectors.toList());

        long actualCount =  diagram.getVertices()
                .stream()
                .filter(dataClass -> !dataClass.isAttribute()).count();

        if(uniques.size() == actualCount){
            //defects.add(new Defect(DefectType.NO_DUPLICATE_NAMES, false, "", ));
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

            //defects.add(new Defect(DefectType.NO_DUPLICATE_NAMES, true, builder.toString(), ));
        }
    }

    /**
     * For finding duplicate attribute names on one Entity or Relationship
     */
    private void checkDuplicateAttributes(){
        log.log(Level.FINE, "Checking duplicate attributes");
        StringBuilder builder = new StringBuilder();
        diagram.getVertices()
                .stream()
                .filter(dataClass -> !dataClass.isAttribute())
                .forEach(dataClass -> {
                    long attributeCount = dataClass.getAdjacentDataClasses()
                            .stream()
                            .filter(DataClass::isAttribute)
                            .count();

                    long uniqueCount = dataClass.getAdjacentDataClasses()
                            .stream()
                            .filter(DataClass::isAttribute)
                            .map(DataClass::getName)
                            .distinct()
                            .count();
                    if(attributeCount != uniqueCount){
                        builder.append(String.format("\n\t\t%s has multiple attributes with the same name", dataClass.getName()));
                    }
                });
        if(builder.isEmpty()){
            //defects.add(new Defect(DefectType.NO_DUPLICATE_ATTRIBUTES, false, "", ));
        }
        else{
            //defects.add(new Defect(DefectType.NO_DUPLICATE_ATTRIBUTES, true, builder.toString(), ));
        }
    }

    /**
     * For finding out if every vertex is named
     */
    private void checkNamedVertices(){
        log.log(Level.FINE, "Checking named vertices");
        List<DataClass> unnamed = diagram.getVertices().stream()
                .filter(dataClass -> dataClass.getName().matches("\s?"))
                .collect(Collectors.toList());
        if(unnamed.isEmpty()){
            //defects.add(new Defect(DefectType.NAMED_VERTICES, false, "", ));
        }
        else{
            StringBuilder builder = new StringBuilder();
            builder.append("Unnamed entities:\n");
            unnamed.forEach(dataClass -> builder.append(String.format("\t\t%s\n",dataClass.toString())));
            //defects.add(new Defect(DefectType.NAMED_VERTICES, true,
            //        builder.toString(), ));
        }
    }

    /**
     *
     * @param entity
     * @return true if weak Entity is identified through its composite id
     */
    private boolean checkWeakEntityIdentification(Entity entity){
        List<Key> keys = entity.getKeys();

        List<Attribute> simpleIds = keys.stream()
                .filter(Key::isSimple)
                .map(key -> (Attribute) key)
                .collect(Collectors.toList());

        if(keys.isEmpty() || !simpleIds.isEmpty()){
            return false;
        }

        List<Composite> possibleIds = entity.getKeys()
                .stream()
                .map(key -> (Composite)key)
                .filter(Composite::isRelationshipBased)
                .collect(Collectors.toList());

        for(Composite composite : possibleIds){
            Optional<Connection> relationshipConnection = composite.getRelationshipConnection();
            if(relationshipConnection.isPresent()){
                boolean isId = isIdentifyingConnection(relationshipConnection.get(), entity);
                if(isId){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param connection relationship to weak entity connection
     * @param entity weak entity
     * @return true if connection identifies a weak entity
     */
    private boolean isIdentifyingConnection(Connection connection, Entity entity){
        if(!connection.getCardinality().equals(Cardinality.ONE)){
            return false;
        }
        return connection.getOtherParticipant(entity).getAdjacentDataClasses()
                .stream()
                .filter(DataClass::isEntity)
                .map(dataClass -> (Entity) dataClass)
                .anyMatch(entity1 -> checkEntityOwnId(entity1));
    }
}
