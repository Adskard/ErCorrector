package cz.cvut.fel;

import cz.cvut.fel.grading.AssignmentGrader;
import cz.cvut.fel.model.Diagram;
import cz.cvut.fel.output.CorrectorOutputFormatter;
import cz.cvut.fel.output.DiagramOutputFormatter;
import cz.cvut.fel.parser.XMLValidator;
import cz.cvut.fel.utils.ConfigLoader;
import org.apache.commons.cli.*;
import lombok.extern.java.Log;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

@Log
public class CommandLineRunner {

    private static final Options options = new Options();
    private static final CommandLineParser parser = new DefaultParser();
    private static final HelpFormatter formatter = new HelpFormatter();

    public static void main(String[] args) {
        Option diagramFile = new Option("d", "diagram", true, "diagram file path");
        diagramFile.setRequired(true);
        options.addOption(diagramFile);

        Option config = new Option("c", "configuration", true, "configuration file path");
        config.setRequired(true);
        options.addOption(config);


        try{
            CommandLine cmd = parser.parse(options, args);
            InputStream xmlFile = new FileInputStream(cmd.getOptionValue(diagramFile));
            XMLValidator xml = new XMLValidator(xmlFile);
            Diagram diagram = xml.extractDiagram();

            //TODO delete debug print
            System.out.println(DiagramOutputFormatter.stringifyDiagram(diagram));

            //Assignment 1
            ConfigLoader loader = new ConfigLoader();
            loader.load(cmd.getOptionValue(config));
            AssignmentGrader grader = new AssignmentGrader(loader.getProperties(), diagram);
            grader.grade();

            //TODO delete debug print
            System.out.println(CorrectorOutputFormatter.stringifyGrading(grader));
        }
        catch(ParseException | IOException | SAXException | ParserConfigurationException ex){
            log.log(Level.SEVERE, "Failed to start", ex);
            formatter.printHelp("ErCorrector", options);
            System.exit(1);
        }
    }

}
