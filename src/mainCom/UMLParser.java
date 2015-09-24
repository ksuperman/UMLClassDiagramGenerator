package mainCom;

import java.io.FileInputStream;
import java.io.IOException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import diagramGenerator.UMLImageRender;
import parser.ClassParser;

public class UMLParser {

	private static String JavaProjectPath;
	public static String UMLDiagramPath;

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub

		JavaProjectPath = args[0];
		UMLDiagramPath = args[1];

		ClassParser classes = new ClassParser(JavaProjectPath);
		classes.ParseClass();
		System.out.println(ParseString.getParseString());
		UMLImageRender classtest = new UMLImageRender();
		classtest.createClassUMLObject();
	}
}
