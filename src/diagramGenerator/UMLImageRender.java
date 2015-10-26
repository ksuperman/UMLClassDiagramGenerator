/**
 * 
 */
package diagramGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import mainCom.ParseString;
import mainCom.UMLParser;
import net.sourceforge.plantuml.SourceStringReader;
import parser.MethodDeclarationStructure;
import parser.ParsedClass;

/**
 * @author rakshithk
 *
 */

public class UMLImageRender {
	
	public void createClassUMLObject(ArrayList<ParsedClass> pc, String diagramGeneratorName) throws IOException{
		ParseString.clearParseStringMethods();
		if(diagramGeneratorName.equals("plantuml")) {
			ParseString.clearParseStrings();
			PlantUMLDiagramCodeGenerator plantUML = new PlantUMLDiagramCodeGenerator();
			for(ParsedClass parsedClass : pc) {
				plantUML.resetClassFields();
				plantUML.setClassFields(parsedClass.getPackageName(), parsedClass.getModifierName(), parsedClass.getClassType(), parsedClass.getClassName(), parsedClass.getAttributeArray(), parsedClass.getMethodsArray(),parsedClass.getDependencyList(),parsedClass.getConstructors());
				plantUML.generateUMLParsedCode();
			}
			plantUML.renderUMLDiagram(pc);
		}
	}
}
