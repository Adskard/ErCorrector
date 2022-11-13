
import corrector.AssignmentGrader;
import lombok.extern.java.Log;
import model.Diagram;
import org.xml.sax.SAXException;
import parser.XMLValidator;
import utils.ConfigLoader;
import utils.CorrectorOutputFormatter;
import utils.DiagramOutputFormatter;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
 

@Log
public class Runner {
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
        String fileName = "shop_correct_erdia.xml";
        String configPath = "config/example_config.txt";
        try{
            //Parse model
            File xmlFile = new File(Runner.class.getClassLoader().getResource(diagramsPath+fileName).toURI());
            XMLValidator xml = new XMLValidator(xmlFile);
            Diagram diagram = xml.extractDiagram();

            //TODO delete debug print
            System.out.println(DiagramOutputFormatter.stringifyDiagram(diagram));

            //Assignment 1
            ConfigLoader loader = new ConfigLoader();
            loader.load(Runner.class.getClassLoader().getResource(configPath).getPath());
            AssignmentGrader grader = new AssignmentGrader(loader.getProperties(), diagram);
            grader.grade();

            //TODO delete debug print
            System.out.println(CorrectorOutputFormatter.stringifyGrading(grader));
            //Assignment 2
        }
        catch(IOException | URISyntaxException | NullPointerException e){
            log.log(Level.SEVERE, "Input file exception");
            e.printStackTrace();
            System.exit(1);
        }
        catch(SAXException | ParserConfigurationException e){
            log.log(Level.SEVERE, "Parser exception");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
