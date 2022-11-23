package cz.cvut.fel.output;

import cz.cvut.fel.grading.AssignmentGrader;
import cz.cvut.fel.grading.defect.Defect;
import cz.cvut.fel.output.stringifier.DefectStringifier;
import cz.cvut.fel.output.stringifier.DefectVisitor;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class CorrectorOutputFormatter is a collection of static functions
 * which have a goal of creating a comprehensive string representation
 * of AssignmentGrader.
 *
 * @see AssignmentGrader
 * @author Adam Skarda
 */
public class CorrectorOutputFormatter {

    /**
     * Creates a string representation of a grader.
     * This representation takes into consideration
     * Defects found during grading and the final amount of points.
     *
     * @param grader grader to be made into string
     * @return String representing a given grader
     */
    public static String stringifyGrading(AssignmentGrader grader){
        List<Defect> defects = grader.getDefects();
        Float points = grader.getPoints();
        StringBuilder builder = new StringBuilder();
        builder.append("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
        builder.append("%\t\tErrors present in diagram\t\t%\n");
        builder.append("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");

        builder.append(String.format("\nNumber of defects %s out of possible %s\n\n",
                defects.stream().filter(Defect::getPresent).count(),
                defects.size()));

        builder.append(String.format("Number of points %s out of possible %s\n\n",
                defects.stream()
                        .filter(defect -> !defect.getPresent())
                        .map(Defect::getPoints)
                        .reduce(0.0f, Float::sum),
                defects.stream()
                        .map(Defect::getPoints)
                        .reduce(0.0f, Float::sum)));

        builder.append(pointTable(defects, points));

        builder.append("\n%%%%%%%%%%%%%%%%%%%%%% Long output %%%%%%%%%%%%%%%%%%%%%%\n");

        defects.forEach(defect -> builder.append(String.format("\n%s\n",defectRepresentation(defect))));
        return builder.toString();
    }

    /**
     * Creates a String representation of a defect by getting it from DefectVisitor.
     * @param defect defect to whose string is wanted
     * @return String representation of given defect
     * @see DefectVisitor
     */
    private static String defectRepresentation(Defect defect){
        DefectVisitor visitor = new DefectStringifier();
        return defect.accept(visitor);
    }

    /**
     * Creates a String point table of all defects
     * and points for their presence (absence).
     * @param defects defects list of checked defects
     * @param points total number of point obtained
     * @return String table of points
     */
    private static String pointTable(List<Defect> defects, Float points){
        StringBuilder builder = new StringBuilder();

        Optional<Integer> longestString = defects.stream()
                .map(defect -> defect.getType().getMessage().length())
                .max(Integer::compareTo);

        if(longestString.isEmpty()){
            return "";
        }

        builder.append("Point table:\n");
        builder.append("-".repeat(longestString.get()));
        builder.append("---------------\n");

        List<Defect> sortedDefects  = defects.stream()
                .sorted(Comparator.comparingDouble(Defect::getPoints).reversed())
                .sorted(Comparator.comparing((defect)->defect.getPresent()))
                .collect(Collectors.toList());

        for(Defect defect : sortedDefects){
            builder.append(
                String.format("| %s%s | %f |\n",
                    defect.getType().getMessage(),
                    " ".repeat(longestString.get() - defect.getType().getMessage().length()),
                    defect.getPresent() ? 0.0f : defect.getPoints()));
        }
        builder.append("-".repeat(longestString.get()));
        builder.append("---------------\n");

        builder.append(String.format("|Total points %s | %f |\n",
                " ".repeat(longestString.get() - "Total points".length()),
                points));

        builder.append("-".repeat(longestString.get()));
        builder.append("---------------\n");

        return builder.toString();
    }
}
