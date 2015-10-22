package mainCom;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import diagramGenerator.UMLImageRender;
import parser.ClassParser;
import parser.ParsedClass;

public class UMLParser {

	private static String JavaProjectPath;
	public static String UMLDiagramPath;	
	private static ParsedClass[] pc;

	public static void main(String[] args) throws IOException, ParseException {

		JavaProjectPath = args[0];
		UMLDiagramPath = args[1];

		ClassParser classes = new ClassParser(JavaProjectPath);
		pc = classes.ParseClass();
		
		for(int i = 0;i<pc.length;i++) {
			if(pc[i] != null)
				pc[i].getDependencyList();
		}
		
		
		for(ParsedClass clas : pc) {
			if(clas!=null)
				System.out.println(clas.toString());
		}
		
		UMLImageRender classtest = new UMLImageRender();
		classtest.createClassUMLObject(pc,"plantuml");
		//classtest.createClassUMLObject(pc,"yUML");
	}
}
