/**
 * 
 */
package diagramGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import mainCom.ParseString;
import mainCom.UMLParser;
import net.sourceforge.plantuml.SourceStringReader;

/**
 * @author rakshithk
 *
 */
public class ClassGenerator {
	// String source = "@startuml\n";

	
	public void createClassUMLObject() throws IOException{
		/*String source = "@startuml \n";
				source += "package \"Classic Collections\" #DDDDDD { \n";
				source += "Object <|-- ArrayList2 \n";
				source += "} \n";
				source += "package \"Classic Collections\" #DDDDDD { \n";
				source += "Object <|-- ArrayList1 \n";
				source += "} \n";
				source += "@enduml";
				*/
		SourceStringReader reader = new SourceStringReader(ParseString.getParseString());
		String desc = reader.generateImage(new File(UMLParser.UMLDiagramPath));
	}
	
}
