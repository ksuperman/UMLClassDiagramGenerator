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
import parser.MethodDeclarationStructure;
import parser.ParsedClass;

public class UMLParser {

	private static String JavaProjectPath;
	public static String UMLDiagramPath;	
	private static ArrayList<ParsedClass> pc;

	public static void main(String[] args) throws IOException, ParseException {

		String[] Testcases = {
				"/home/rakshithk/Desktop/uml-parser-test-1",
				"/home/rakshithk/Desktop/uml-parser-test-2",
				"/home/rakshithk/Desktop/Test Cases/uml-parser-test-1",
				"/home/rakshithk/Desktop/Test Cases/uml-parser-test-2",
				"/home/rakshithk/Desktop/Test Cases/uml-parser-test-3",
				"/home/rakshithk/Desktop/Test Cases/uml-parser-test-4",
				"/home/rakshithk/Desktop/Test Cases/uml-parser-test-5"
		};
		
		int counter = 0;
		
		for(String Testcase : Testcases) {
			System.out.println("--------------------------------------------------------------------------");
			System.out.println("--------------------------" + Testcase + "----------------------------------------");
			System.out.println("--------------------------------------------------------------------------");
			System.out.println("--------------------------------------------------------------------------");
			UMLDiagramPath = "/home/rakshithk/test" + counter++ + ".png";
			ClassParser classes = new ClassParser(Testcase);
			pc = classes.ParseClass();
			
		/*	for(ParsedClass clas : pc) {
				if(clas!=null)
					System.out.println(clas.toString());
			}*/
			
			UMLImageRender classtest = new UMLImageRender();
			classtest.createClassUMLObject(pc,"plantuml");
			System.out.println("--------------------------------------------------------------------------");
			System.out.println("--------------------------------------------------------------------------");
			System.out.println("--------------------------------------------------------------------------");
			//classtest.createClassUMLObject(pc,"yUML");
		}
/*		
		JavaProjectPath = args[0];
		UMLDiagramPath = args[1];

		ClassParser classes = new ClassParser(JavaProjectPath);
		pc = classes.ParseClass();
		
		for(ParsedClass clas : pc) {
			if(clas!=null)
				System.out.println(clas.toString());
		}
		
		UMLImageRender classtest = new UMLImageRender();
		classtest.createClassUMLObject(pc,"plantuml");
		//classtest.createClassUMLObject(pc,"yUML");
*/	}
}
