package cz.cvut.fel.grading.configuration;

import cz.cvut.fel.grading.configuration.value.*;
import cz.cvut.fel.grading.struct.CardinalityPair;
import cz.cvut.fel.grading.struct.HierarchyPair;
import cz.cvut.fel.enums.Cardinality;
import cz.cvut.fel.enums.Coverage;
import cz.cvut.fel.enums.DefectType;
import cz.cvut.fel.enums.Disjointness;
import cz.cvut.fel.exception.ConfigurationException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class ConfigExtractor is used for extracting ConfigValues
 * from configuration Properties.
 *
 * @author Adam Skarda
 * @see ConfigValue
 */
public class ConfigExtractor {
    private final Properties configuration;

    private final String confKeyDisableString = "disable";
    private final String confValueSeparator = ",";
    private final String confCardinalityAll = "all";
    private final String confPairSeparator = "-";

    /**
     * Basic constructor
     * @param configuration Properties object containing defect configuration
     */
    public ConfigExtractor(Properties configuration) {
        this.configuration = configuration;
    }

    /**
     * Checks if given configuration key is enabled.
     *
     * @param defectType Defect to be checked if its enabled
     * @return True if key is not present, or its value is not equal to disable string
     */
    public boolean isEnabledInConfig(DefectType defectType){
        String key = defectType.getConfigKey();
        String value = configuration.getProperty(key);

        if(Objects.isNull(value)){
            return true;
        }

        return !configuration.getProperty(key).isBlank() &&
                !configuration.getProperty(key).strip().equalsIgnoreCase(confKeyDisableString);
    }

    /**
     * Extracts a ConfigValue for a given defectType.
     * ConfigValue is extracted from Properties.
     *
     * @param defectType Type of defect for which we want configuration values
     * @return configuration value for Defect configuration
     */
    public ConfigValue getConfigValue(DefectType defectType){
        List<String> values = Arrays.stream(
                    configuration.getProperty(defectType.getConfigKey(), defectType.getDefaultValue())
                        .strip()
                        .split(confValueSeparator))
                .map(String::strip)
                .collect(Collectors.toList());

        ConfigValue configValue;

        switch(defectType.getClassification()){
            case USAGE:
                configValue = getUsageValue(defectType, values);
                break;

            case QUANTITY:
                configValue = getQuantityValue(values);
                break;

            case BASIC:
                configValue = getBasicValue(values);
                break;

            case NARY_RELATIONSHIP:
                configValue = new NaryRelationshipConfigValue(Float.parseFloat(values.get(values.size()-1)),
                    Integer.parseInt(values.get(0)), Integer.parseInt(values.get(1)),
                    Integer.parseInt(values.get(2)));
                break;

            default:
                configValue = new ConfigValue(0.0f);
        }

        return configValue;
    }

    /**
     * Extracts configuration values for basic type defects.
     * @param values Values to be parsed into ConfigValue
     * @return configuration values for basic type defects
     * @see cz.cvut.fel.enums.ConfigValueType
     */
    private ConfigValue getBasicValue(List<String> values){
        return new ConfigValue(Float.parseFloat(values.get(0)));
    }

    /**
     * Extracts configuration values for quantity type defects.
     * @param values Values to be parsed into QuantityConfigValue
     * @return configuration values for quantity type defects
     * @see cz.cvut.fel.enums.ConfigValueType
     */
    private ConfigValue getQuantityValue(List<String> values){
        return new QuantityConfigValue(Float.parseFloat(values.get(values.size()-1)),
                Integer.parseInt(values.get(0)), Integer.parseInt(values.get(1)));
    }

    /**
     * Extracts configuration values for usage type defects.
     * @param values Values to be parsed into configuration values
     * @return configuration values for usage type defects
     * @see cz.cvut.fel.enums.ConfigValueType
     */
    private ConfigValue getUsageValue(DefectType defectType, List<String> values) throws ConfigurationException{
        ConfigValue value;
        float points = Float.parseFloat(values.get(values.size()-1));
        switch (defectType){
            case CARDINALITY_TYPE_USAGE:
            case MULTIVALUED_ATTRIBUTE_CARDINALITY_USAGE:
                value = new CardinalityUsageConfigValue(points,
                        getCardinalitiesFromValues(values.subList(0, values.size() - 1)));
                break;
            case CARDINALITY_PAIR_USAGE:
                value = new CardinalityPairUsageConfigValue(points,
                    getCardinalityPairsFromValues(values.subList(0, values.size() - 1)));
                break;
            case HIERARCHY_USAGE:
                value = new HierarchyPairUsageConfigValue(points,
                    getHierarchyPairsFromValues(values.subList(0, values.size() - 1)));
                break;

            default:
                throw new ConfigurationException(
                    String.format("Defect type: %s not implemented as UsageType defect",defectType));
        }
        return value;
    }

    /**
     * Extracts cardinality pairs from list of strings.
     * Values should contain either token for extracting
     * full pair enumeration at first position. Or an enumeration of
     * Cardinality pairs.
     *
     * @param values configuration CardinalityPair enumeration to be parsed
     * @return Cardinality pairs set by configuration
     * @throws ConfigurationException if a parsed Cardinality is not recognized
     * @see CardinalityPair
     */
    private List<CardinalityPair> getCardinalityPairsFromValues(List<String> values) throws ConfigurationException{
        List<Cardinality> cardinalities = new LinkedList<>();
            cardinalities.add(Cardinality.ZERO_TO_ONE);
            cardinalities.add(Cardinality.ONE);
            cardinalities.add(Cardinality.ONE_TO_MANY);
            cardinalities.add(Cardinality.ZERO_TO_MANY);

        List<CardinalityPair> pairs = new LinkedList<>();

        if(values.get(0).equalsIgnoreCase(confCardinalityAll)){
            return CardinalityPair.fromCardinalityList(cardinalities);
        }

        for(String val : values){
            String[] cardinalityString = val.split(confPairSeparator);
            Cardinality first = Cardinality.decideCardinality(cardinalityString[0]);
            Cardinality second = Cardinality.decideCardinality(cardinalityString[1]);

            if(first.equals(Cardinality.NOT_RECOGNIZED) || second.equals(Cardinality.NOT_RECOGNIZED)){
                throw new ConfigurationException(String.format("Cardinality pair %s not recognized", val));
            }

            pairs.add(new CardinalityPair(first, second));
        }

        return pairs;
    }

    /**
     * Parses HierarchyPairs from given string values.
     * Values should contain either token for extracting
     * full pair enumeration at first position. Or an enumeration of
     * HierarchyPairs.
     *
     * @param values configuration HierarchyPair enumeration to be parsed
     * @return enumeration of HierarchyPairs set by configuration
     * @throws ConfigurationException if Hierarchy coverage or disjointness is not recognized
     * @see HierarchyPair
     */
    private List<HierarchyPair> getHierarchyPairsFromValues(List<String> values) throws ConfigurationException{
        List<HierarchyPair> pairs = new LinkedList<>();
        if(values.get(0).equalsIgnoreCase(confCardinalityAll)){
            pairs.add(new HierarchyPair(Coverage.COMPLETE, Disjointness.EXCLUSIVE));
            pairs.add(new HierarchyPair(Coverage.PARTIAL, Disjointness.OVERLAPPING));
            pairs.add(new HierarchyPair(Coverage.PARTIAL, Disjointness.EXCLUSIVE));
            pairs.add(new HierarchyPair(Coverage.COMPLETE, Disjointness.OVERLAPPING));
            return pairs;
        }

        for(String val : values){
            String[] pair = val.split(confPairSeparator);
            Coverage coverage = Coverage.decideCoverage(pair[0]);
            Disjointness disjointness = Disjointness.decideDisjointness(pair[1]);

            if(coverage.equals(Coverage.NOT_RECOGNIZED) || disjointness.equals(Disjointness.NOT_RECOGNIZED)) {
                throw new ConfigurationException(String.format("Hierarchy type not recognized %s", val));
            }

            pairs.add(new HierarchyPair(coverage, disjointness));
        }

        return pairs;
    }

    /**
     * Gets types of cardinalities from given values.
     * @see Cardinality
     * @param values list of cardinality string values
     * @return types of cardinalities present
     * @throws ConfigurationException if values contain not recognized cardinality
     */
    private List<Cardinality> getCardinalitiesFromValues(List<String> values) throws ConfigurationException{
        List<Cardinality> cardinalities = new LinkedList<>();

        if(values.get(0).equalsIgnoreCase(confCardinalityAll)){
            cardinalities.add(Cardinality.ZERO_TO_ONE);
            cardinalities.add(Cardinality.ONE);
            cardinalities.add(Cardinality.ONE_TO_MANY);
            cardinalities.add(Cardinality.ZERO_TO_MANY);
            return cardinalities;
        }

        for(String val : values){
            Cardinality cardinality = Cardinality.decideCardinality(val);
            if(cardinality.equals(Cardinality.NOT_RECOGNIZED)){
                throw new ConfigurationException(String.format("Cardinality %s not recognized", val));
            }
            cardinalities.add(cardinality);
        }

        return cardinalities.stream()
                .filter(cardinality -> cardinality.equals(Cardinality.NO_CARDINALITY))
                .distinct().collect(Collectors.toList());
    }
}
