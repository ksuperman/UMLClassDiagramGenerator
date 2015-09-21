package mainCom;

import java.io.FileInputStream;
import java.io.IOException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import diagramGenerator.ClassGenerator;
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
		ClassGenerator classtest = new ClassGenerator();
		classtest.createClassUMLObject();

		/*
		 * 
		 * FileInputStream in = new FileInputStream(
		 * "/home/rakshithk/workspace/JavaMultiThreadExample/src/FileCombine/FileText.java"
		 * ); CompilationUnit cu; try { // parse the file cu =
		 * JavaParser.parse(in); } finally { in.close(); } new
		 * MethodVisitor().visit(cu, null);
		 * 
		 * System.out.println(cu.toStringWithoutComments()); //
		 */
	}

	

}
