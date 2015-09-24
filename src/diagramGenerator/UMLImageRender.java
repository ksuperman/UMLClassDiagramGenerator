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
public class UMLImageRender {
	
	public void createClassUMLObject() throws IOException{
		SourceStringReader reader = new SourceStringReader(ParseString.getParseString());
		String desc = reader.generateImage(new File(UMLParser.UMLDiagramPath));
	}
	
}
