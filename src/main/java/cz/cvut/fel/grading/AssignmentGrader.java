package cz.cvut.fel.grading;

import cz.cvut.fel.grading.checker.DefectChecker;
import cz.cvut.fel.grading.configuration.ConfigExtractor;
import cz.cvut.fel.grading.defect.Defect;
import cz.cvut.fel.model.Diagram;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 * The Class AssignmentGrader ...
 * This class and its methods do not change given Diagram.
 *
 * @author Adam Skarda
 */
@Log
@Getter
public class AssignmentGrader {

    private float points;
    private final List<Defect> defects = new LinkedList<>();
    private final Diagram diagram;
    private final Properties properties;
    private final DefectChecker defectChecker;

    public AssignmentGrader(Properties config, Diagram diagram){
        this.diagram = diagram;
        this.properties = config;
        defectChecker = new DefectChecker(diagram, new ConfigExtractor(config));
    }

    /**
     * Grades a given diagram based on configuration parameters
     */
    public void grade(){
        log.log(Level.INFO, "Grading diagram");
        defects.addAll(defectChecker.findDefects());
        points = computePoints(defects);
    }

    private float computePoints(List<Defect> defects){
        return  defects.stream()
                .filter(defect -> !defect.getPresent())
                .map(Defect::getPoints)
                .reduce(0.0f, Float::sum);
    }
}
