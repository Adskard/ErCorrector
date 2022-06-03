
import org.xml.sax.SAXException;
import parser.XMLValidator;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
 

public class Runner {
    private static final Logger logger = Logger.getLogger(Runner.class.getName());
    public static void main(String[] args) {
        
        String diagramsPath = "D:\\CVUT\\bakalarka\\diagrams";
        String fileName = "\\allEntities.xml";
        try{
            File f = new File(diagramsPath + fileName);
            logger.log(Level.FINE, f.getName() + " can read: " + f.canRead());

            XMLValidator xml = new XMLValidator(f);
            xml.extractDiagram();
        }
        catch(IOException e){
            logger.log(Level.SEVERE, "Input file exception");
            e.printStackTrace();
            System.exit(1);
        }
        catch(SAXException | ParserConfigurationException e){
            logger.log(Level.SEVERE, "Parser exception");
            e.printStackTrace();
            System.exit(1);
        }
        
                
    }
}
