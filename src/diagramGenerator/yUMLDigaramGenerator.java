package diagramGenerator;

import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import mainCom.ParseString;
import mainCom.UMLParser;
import utilities.UtilitiesFunctions;

/**
 * @author rakshithk
 *
 */

public class yUMLDigaramGenerator {

	//Class Related Declarations
	private String packageName;
	private String modifierName;
	private String classType;
	private String className;
	private ArrayList<String> attributeArray;
	private ArrayList<String> methodsArray;
	private Map<String, java.util.ArrayList<String>> dependencyList = new HashMap<>();	

	// Dependencies related Declaration.
	private final String[] dependencies = { "implements;-.-^", "extends;-^" ,"associations;-"};
	private String dependenciesType = "";
	private boolean dependenciesFound = false;
	private ArrayList<String> tempDepClassArray;
	private Map<String, String> dependencyMap = new HashMap<>();

	//Attributes realted variables
	private static Map<String, String> accessModifierMap = new HashMap<>();
	private static final String[] accessModifiers = { "public;+", "private;-", "protected;#" };

	//Temp Varaibles
	private String[] tempStringArray;

	public yUMLDigaramGenerator() {

		dependencyMap = new HashMap<>();
		for (String dependency : dependencies) {
			tempStringArray = dependency.split(";");
			for (int x = 0; x < tempStringArray.length; x++) {
				dependencyMap.put(tempStringArray[0], tempStringArray[1]);
			}
		}

		accessModifierMap = new HashMap<>();
		for (String modifiers : accessModifiers) {
			tempStringArray = modifiers.split(";");
			for (int x = 0; x < tempStringArray.length; x++) {
				accessModifierMap.put(tempStringArray[0], tempStringArray[1]);
			}
		}
	}

	public void setClassFields(String packageName, String modifierName, String classType, String className,
			ArrayList<String> attributeArray, ArrayList<String> methodsArray,Map<String, java.util.ArrayList<String>> dependencyList) {
		this.packageName = packageName;
		this.modifierName = modifierName;
		this.classType = classType;
		this.className = className;
		this.attributeArray = attributeArray;
		this.methodsArray = methodsArray;
		this.dependencyList = dependencyList;
	}

	public void resetClassFields() {
		this.packageName = "";
		this.modifierName = "";
		this.classType = "";
		this.className = "";
		this.attributeArray = null;
		this.methodsArray = null;
		this.dependencyList = null;;
	}

	public void generateYUMLDiagram() {
		try {
			ParseString.clearParseStringMethods();
			//Creating the classes and its attributes
			if (className != "") {
				if(attributeArray.size() > 0||methodsArray.size() > 0)
					ParseString.setParseString("[");
				if(attributeArray.size() > 0||methodsArray.size() > 0) {
					ParseString.setParseString(className);	

				}

				if(attributeArray.size() > 0){
					ParseString.setParseString("|");
				}
				for(String attribute : attributeArray) {
					tempStringArray = attribute.split(" ");
					if(UtilitiesFunctions.isArray(tempStringArray[1])) {
						if(UtilitiesFunctions.isArrayOrCollection(tempStringArray[1]) == "Collection") {
							ParseString.setParseString(accessModifierMap.get(tempStringArray[0]) + tempStringArray[2] + ":" +tempStringArray[1].substring(0, tempStringArray[1].indexOf("<")) + " Collection" + ";");
						}else
							ParseString.setParseString(accessModifierMap.get(tempStringArray[0]) + tempStringArray[2] + ":" +tempStringArray[1].substring(0, tempStringArray[1].indexOf("[")) + " Array"  + ";");
					}else
						ParseString.setParseString(accessModifierMap.get(tempStringArray[0]) + tempStringArray[2] + ":" +tempStringArray[1] + ";");
				}
				//Creating the methods inside the classes
				if(methodsArray.size() > 0){
					ParseString.setParseString("|");
					for(String methods : methodsArray) {
						tempStringArray = methods.split(" ");
						String temps = methods.replaceFirst(tempStringArray[0], accessModifierMap.get(tempStringArray[0]));
						temps = temps.replace("[", "");
						temps = temps.replace("]", "");
						temps = temps.replace(",", " ");
						ParseString.setParseString( temps + ";");
					}
				}
				if (className != "" && (attributeArray.size() > 0||methodsArray.size() > 0))			
					ParseString.setParseString("]\n");

				// Creating the Dependencies between classes
				for (Map.Entry<String, ArrayList<String>> dependency : dependencyList.entrySet()) {
					String arrorType = dependencyMap.get(dependency.getKey());
					tempDepClassArray = dependency.getValue();
					for (String depClassName : tempDepClassArray) {
						if(UtilitiesFunctions.isArray(depClassName)) {
							ParseString.setParseStringTail("[" + className + "]" + arrorType + "*[" + depClassName.substring(0, depClassName.indexOf("[")) + "],");
						}
						else {
							ParseString.setParseStringTail("[" + className + "]" + arrorType + "1[" + depClassName + "],");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dependenciesType = "";
			dependenciesFound = false;
			tempDepClassArray = null;
			tempStringArray = null;
			packageName = "";
			modifierName = "";
			classType = "";
			className = "";
			attributeArray = null;
			methodsArray = null;
		}

	}

	public void getYUMLDiagram() {

		String parseString = ParseString.getParseStringYUML();
		String command = "echo \"" + parseString + "\" | yuml -v -t class -s scruffy -o /home/rakshithk/diagram.png";
		String[] Commands = new String[]{"bash","-c",command};
		String Temp;
		try {
			Process currentshell = Runtime.getRuntime().exec(Commands);
			BufferedReader br = new BufferedReader(
					new InputStreamReader(currentshell.getInputStream()));
			while ((Temp = br.readLine()) != null)
			currentshell.waitFor();
			currentshell.destroy();

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
