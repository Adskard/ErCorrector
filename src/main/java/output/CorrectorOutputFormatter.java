package output;

import corrector.AssignmentGrader;
import corrector.defect.BasicDefect;
import corrector.defect.Defect;
import corrector.defect.QuantityDefect;
import output.stringifier.DefectStringifier;
import output.stringifier.DefectVisitor;

import java.util.List;
import java.util.Optional;

public class CorrectorOutputFormatter {
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

        defects.stream()
                //.filter(Defect::getPresent)
                .forEach(defect -> builder.append(String.format("\n%s\n",defectRepresentation(defect))));
        return builder.toString();
    }

    private static String defectRepresentation(Defect defect){
        DefectVisitor visitor = new DefectStringifier();
        return defect.accept(visitor);
    }

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

        defects.stream().forEach(defect -> builder.append(String.format("| %s%s | %f |\n",
                defect.getType().getMessage(),
                " ".repeat(longestString.get() - defect.getType().getMessage().length()),
                defect.getPresent() ? 0.0f : defect.getPoints())));

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
