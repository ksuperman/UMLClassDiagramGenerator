package diagramGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mainCom.ParseString;
import parser.ParsedClass;
import utilities.UtilitiesFunctions;

public class PlantUMLDiagramCodeGenerator {
	//Class Related Declarations
	private String packageName;
	private String modifierName;
	private String classType;
	private String className;
	private ArrayList<String> attributeArray;
	private ArrayList<String> methodsArray;
	private Map<String, java.util.ArrayList<String>> dependencyList = new HashMap<>();	

	// Dependencies related Declaration.
	private final String[] dependencies = { "implements;..>", "extends;-->" ,"associations;--"};
	private String dependenciesType = "";
	private boolean dependenciesFound = false;
	private ArrayList<String> tempDepClassArray;
	private Map<String, String> dependencyMap = new HashMap<>();

	
	//Attributes realted variables
	private static Map<String, String> accessModifierMap = new HashMap<>();
	private static final String[] accessModifiers = { "public;+", "private;-", "protected;#" };
	
	//Temp Varaibles
	private String[] tempStringArray;
	
	/**
	 * @param packageName
	 * @param modifierName
	 * @param classType
	 * @param className
	 * @param attributeArray
	 * @param methodsArray
	 */
	public PlantUMLDiagramCodeGenerator() {

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

	public void generateUMLParsedCode() {
		try {
			ParseString.clearParseStringMethods();
			
			//Setting the Package Name
			if (packageName == "")
				packageName = "DefaultPackage";
					
			//Creating the classes and its attributes
			if (className != "") {
				ParseString.setParseString("package " + packageName + " #DDDDDD {\n");
				ParseString.setParseString(classType + " " + className);
				
				if(modifierName.equals("static") || modifierName.equals("abstract"))
					ParseString.setParseString(" << " +   modifierName  + " >> ");
				else if(classType.equals("interface"))
					ParseString.setParseString(" << " +   classType  + " >> ");
				ParseString.setParseString("{\n");		
				for(String attribute : attributeArray) {
					tempStringArray = attribute.split(" ");
					//attribute = attribute.replaceFirst(tempStringArray[0], accessModifierMap.get(tempStringArray[0]))
					ParseString.setParseString(attribute.replaceFirst(tempStringArray[0], accessModifierMap.get(tempStringArray[0])) + "\n");
				}
				ParseString.setParseString("}\n}\n");
				// Creating the Dependencies between classes
				for (Map.Entry<String, ArrayList<String>> dependency : dependencyList.entrySet()) {
					System.out.println("dependency.getKey()"+ dependency.getKey());
					String arrorType = dependencyMap.get(dependency.getKey());
					System.out.println("arrorType"+arrorType);
					tempDepClassArray = dependency.getValue();
					for (String depClassName : tempDepClassArray) {
						if(UtilitiesFunctions.isArray(depClassName))
							ParseString.setParseStringTail(className + arrorType + " \"*\" " + depClassName.substring(0, depClassName.indexOf("[")) + "\n");
						else
							ParseString.setParseStringTail(className + arrorType + " \"1\" " + depClassName + "\n");
						
						//ParseString.setParseStringTail(className + " \"1\" " + arrorType + " \"*\" " + depClassName.substring(0, depClassName.indexOf("[")) + "\n");
					}
				}
				
				//Creating the methods inside the classes
				for(String methods : methodsArray) {
					
						tempStringArray = methods.split(" ");
						//attribute = attribute.replaceFirst(tempStringArray[0], accessModifierMap.get(tempStringArray[0]))
					//	ParseString.setParseString(tempStringArray[0].replaceFirst(tempStringArray[0], accessModifierMap.get(tempStringArray[0])) + "\n");
					ParseString.setParseStringTail(className + " : " + methods.replaceFirst(tempStringArray[0], accessModifierMap.get(tempStringArray[0])) + "\n");
				}
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			resetClassFields();
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
}
