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
import parser.ParsedClass;

/**
 * @author rakshithk
 *
 */
public class UMLImageRender {
	
	public void createClassUMLObject(ParsedClass[] pc, String diagramGeneratorName) throws IOException{
		ParseString.clearParseStringMethods();
		if(diagramGeneratorName.equals("plantuml")) {
			PlantUMLDiagramCodeGenerator plantUML = new PlantUMLDiagramCodeGenerator();
			System.out.println("pc.length" + pc.length);
			for(int i = 0;i<pc.length;i++) {
				if(pc[i] != null) {
					plantUML.resetClassFields();
					plantUML.setClassFields(pc[i].getPackageName(), pc[i].getModifierName(), pc[i].getClassType(), pc[i].getClassName(), pc[i].getAttributeArray(), pc[i].getMethodsArray(),pc[i].getDependencyList());
					plantUML.generateUMLParsedCode();
					plantUML.resetClassFields();	
				}
			}
			//System.out.println(ParseString.getParseStringPlantUML());
			SourceStringReader reader = new SourceStringReader(ParseString.getParseStringPlantUML());
			String desc = reader.generateImage(new File(UMLParser.UMLDiagramPath));
		}
		if(diagramGeneratorName.equals("yUML")) {
			yUMLDigaramGenerator yUML = new yUMLDigaramGenerator();
			for(int i = 0;i<pc.length;i++) {
				if(pc[i] != null) {
					yUML.resetClassFields();
					yUML.setClassFields(pc[i].getPackageName(), pc[i].getModifierName(), pc[i].getClassType(), pc[i].getClassName(), pc[i].getAttributeArray(), pc[i].getMethodsArray(),pc[i].getDependencyList());
					yUML.generateYUMLDiagram();	
					yUML.resetClassFields();	
				}
			}
			yUML.getYUMLDiagram();
		}
	}
}
