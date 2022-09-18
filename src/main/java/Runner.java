
import lombok.extern.java.Log;
import org.xml.sax.SAXException;
import parser.XMLValidator;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
 

@Log
public class Runner {
    public static void main(String[] args) {
        
        String diagramsPath = "test_data_ER/erdia/";
        String fileName = "allErParts_erdia.xml";
        try{
            File xmlFile = new File(Runner.class.getClassLoader().getResource(diagramsPath+fileName).toURI());
            log.log(Level.FINE, xmlFile.getName() + (xmlFile.canRead() ? " is readable" : " is unreadable" ));
            XMLValidator xml = new XMLValidator(xmlFile);
            xml.extractDiagram();
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
