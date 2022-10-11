
import lombok.extern.java.Log;
import org.xml.sax.SAXException;
import parser.XMLValidator;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
 

@Log
public class Runner {
    public static void main(String[] args) {
        
        String diagramsPath = "test_data_ER/erdia/";
        String fileName = "mostlyCorrect.xml";
        /*
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
        try{
            File xmlFile = new File(Runner.class.getClassLoader().getResource(diagramsPath+fileName).toURI());
            log.log(Level.FINE, xmlFile.getName() + (xmlFile.canRead() ? " is readable" : " is unreadable" ));
            XMLValidator xml = new XMLValidator(xmlFile);
            //TODO delete debug print
            System.out.println(formatter.SimpleDiagramFormat.stringifyDiagram(xml.extractDiagram()));
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
