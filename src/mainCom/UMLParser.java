package mainCom;

import java.io.IOException;

import diagramGenerator.ClassGenerator;
import parser.ClassParser;

public class UMLParser {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ClassParser classes = new ClassParser();
		classes.ParseClass();
		System.out.println(ParseString.getParseString());
		ClassGenerator classtest = new ClassGenerator();
		classtest.createClassUMLObject();
	}

}
