package corrector;

import lombok.Getter;
import lombok.extern.java.Log;
import model.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 * The Class AssignmentGrader ...
 *
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
    private final BasicCorrector basicCorrector;
    private final AssignmentCorrector assignmentCorrector;

    public AssignmentGrader(Properties config, Diagram diagram){
        this.diagram = diagram;
        this.properties = config;
        basicCorrector = new BasicCorrector(diagram, config);
        assignmentCorrector = new AssignmentCorrector(diagram, config);
    }

    /**
     * Grades a given diagram based on configuration parameters
     */
    public void grade(){
        log.log(Level.INFO, "Grading diagram");
        defects.addAll(basicCorrector.findDefects());
        defects.addAll(assignmentCorrector.findDefects());

    }
}
