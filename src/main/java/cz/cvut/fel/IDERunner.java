package cz.cvut.fel;

import cz.cvut.fel.grading.AssignmentGrader;
import cz.cvut.fel.output.CorrectorOutputFormatter;
import cz.cvut.fel.output.DiagramOutputFormatter;
import cz.cvut.fel.parser.XMLValidator;
import cz.cvut.fel.utils.ConfigLoader;
import lombok.extern.java.Log;
import cz.cvut.fel.model.Diagram;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
 

@Log
public class IDERunner {
    /*
       Test file names

       erdia
       very_complicated.xml
       allErParts_erdia.xml
       account_connectionAndId_erdia.xml
       account_correct_erdia.xml
       person_missingId_erdia.xml
       person_correct_erdia.xml
       shop_correct_erdia.xml
       novak.xml
       mostlyCorrect.xml

       drawio
       allErParts_drawio.xml
       account_correct_drawio.xml
       shop_minorMistakes_drawio.xml
       account_missingCardinality_drawio.xml
       account_missingId&notOneComponent_drawio.xml
        */
    public static void main(String[] args) {
        
        String diagramsPath = "test_data_ER/erdia/";
        String fileName = "account_connectionAndId_erdia.xml";
        String configPath = "config/example_config.txt";
        try{
            //Parse er model
            XMLValidator xml = new XMLValidator(IDERunner.class.getClassLoader().getResourceAsStream(diagramsPath+fileName));
            Diagram diagram = xml.extractDiagram();

            //TODO delete debug print
            System.out.println(DiagramOutputFormatter.stringifyDiagram(diagram));

            //Assignment 1
            ConfigLoader loader = new ConfigLoader();
            loader.load(IDERunner.class.getClassLoader().getResource(configPath).getPath());
            AssignmentGrader grader = new AssignmentGrader(loader.getProperties(), diagram);
            grader.grade();

            //TODO delete debug print
            System.out.println(CorrectorOutputFormatter.stringifyGrading(grader));
            //Assignment 2
        }
        catch(IOException | NullPointerException e){
            log.log(Level.SEVERE, "Input file cz.cvut.fel.corrector.exception");
            e.printStackTrace();
            System.exit(1);
        }
        catch(SAXException | ParserConfigurationException e){
            log.log(Level.SEVERE, "Parser cz.cvut.fel.corrector.exception");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
