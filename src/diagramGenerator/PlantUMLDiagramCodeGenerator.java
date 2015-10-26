package diagramGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import mainCom.ParseString;
import mainCom.UMLParser;
import net.sourceforge.plantuml.SourceStringReader;
import parser.MethodDeclarationStructure;
import parser.ParsedClass;
import utilities.UtilitiesFunctions;

/**
 * @author rakshithk
 *
 */

public class PlantUMLDiagramCodeGenerator {
	//Diagram Related Declarations
	private boolean showIcons = false;

	//Class Related Declarations
	private String packageName;
	private String modifierName;
	private String classType;
	private String className;
	private ArrayList<String> attributeArray;
	private ArrayList<MethodDeclarationStructure> methodsArray;
	private ArrayList<MethodDeclarationStructure> constructorArray;
	private Map<String, java.util.ArrayList<String>> dependencyList = new HashMap<>();	

	// Dependencies related Declaration.
	private final String[] dependencies = { "implements;..|>", "extends;--|>" ,"associations;--","uses;..>"};
	private String dependenciesType = "";
	private boolean dependenciesFound = false;
	private ArrayList<String> tempDepClassArray;
	private Map<String, String> dependencyMap = new HashMap<>();

	//Attributes realted variables
	private static Map<String, String> accessModifierMap = new HashMap<>();
	private static Map<String, String> accessModifierMethodsMap = new HashMap<>();
	private static Map<String, String> otherModifierMap = new HashMap<>();
	private static final String[] accessModifiers = { "public;+", "private;-"};//, "protected;#"
	private static final String[] accessModifiersMethods = { "public;+", "private;-"};//, "protected;#"
	private static final String[] otherModifiers = {"static;static","abstract;abstract"};//,"synchronized;synchronized","final;final"
	//private static final String[] accessModifiers = { "public;+", "private;-"};

	//Temp Varaibles
	private String[] tempStringArray;
	private String tempString = "";
	private String tempString2 = "";
	private String methodSignature = "";
	private String[] methodSignatureArr;
	private String[] signatureArr;
	private ArrayList<String> tempArrayListString;

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
		
		accessModifierMethodsMap = new HashMap<>();
		for (String modifiers : accessModifiersMethods) {
			tempStringArray = modifiers.split(";");
			for (int x = 0; x < tempStringArray.length; x++) {
				accessModifierMethodsMap.put(tempStringArray[0], tempStringArray[1]);
			}
		}

		otherModifierMap = new HashMap<>();
		for (String modifiers : otherModifiers) {
			tempStringArray = modifiers.split(";");
			for (int x = 0; x < tempStringArray.length; x++) {
				otherModifierMap.put(tempStringArray[0], tempStringArray[1]);
			}
		}
	}

	public void setClassFields(String packageName, String modifierName, String classType, String className,
			ArrayList<String> attributeArray, ArrayList<MethodDeclarationStructure> arrayList,Map<String, java.util.ArrayList<String>> dependencyList, ArrayList<MethodDeclarationStructure> constructorArray) {
		this.packageName = packageName;
		this.modifierName = modifierName;
		this.classType = classType;
		this.className = className;
		this.attributeArray = attributeArray;
		this.methodsArray = arrayList;
		this.dependencyList = dependencyList;
		this.constructorArray = constructorArray;
	}

	public void resetClassFields() {
		this.packageName = "";
		this.modifierName = "";
		this.classType = "";
		this.className = "";
		this.attributeArray = null;
		this.methodsArray = null;
		this.dependencyList = null;
		this.constructorArray = null;
	}

	public void generateUMLParsedCode() {
		try {
			//clear the Static Class before the PlantUML Code is Generated
			ParseString.clearParseStringMethods();

			//Hide Access Modifier Icons
			if(!showIcons) {
				ParseString.setParseString("skinparam classAttributeIconSize 0\n");
			}

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
					if(tempStringArray.length == 3) {
						if(accessModifierMap.get(tempStringArray[0]) != null && accessModifierMap.get(tempStringArray[0]) != "") {
							tempString = accessModifierMap.get(tempStringArray[0]);
							tempString += tempStringArray[2];
							tempString += " : ";
							tempString += tempStringArray[1];
							tempString += "\n";
							ParseString.setParseString(tempString);
						}
					}
				}
				ParseString.setParseString("}\n}\n");

				//Creating the Dependencies between classes
				for (Map.Entry<String, ArrayList<String>> dependency : dependencyList.entrySet()) {
					String arrorType = dependencyMap.get(dependency.getKey());
					tempDepClassArray = dependency.getValue();
					for (String depClassName : tempDepClassArray) {
						if(UtilitiesFunctions.isArray(depClassName)) {

							tempString = className + " " + arrorType ;
							if(dependency.getKey().equals("associations"))//Denote Multiplicity for Association only
								tempString += " \"*\" ";
							else
								tempString += " ";
							tempString += depClassName.substring(0, depClassName.indexOf("["));
						}else {
							tempString = className + " " + arrorType ;
							if(dependency.getKey().equals("associations"))//Denote Multiplicity for Association only
								tempString += " \"1\" "; 
							else
								tempString += " ";
							tempString += depClassName;
						}

						//Uses Lable applied for Interface
						if(dependency.getKey().equals("uses"))
							tempString += " : uses";
						tempString += "\n";
						ParseString.setParseStringBody(tempString);
					}
				}

				//Creating the methods inside the classes
				for(MethodDeclarationStructure methods : methodsArray) {
					if(accessModifierMethodsMap.get(methods.getAccessModifier()) != null && accessModifierMethodsMap.get(methods.getAccessModifier()) != ""){
						tempString = className + " : ";
						tempArrayListString = methods.getOtherModifiers();
						if(tempArrayListString != null) {
							for(String otherModifiers : tempArrayListString) {
								if(otherModifierMap.containsKey(otherModifiers)) {
									tempString += "{" + otherModifierMap.get(otherModifiers) + "}";
									break;	
								}
							}	
						}
						tempString += accessModifierMethodsMap.get(methods.getAccessModifier());
						tempString += methods.getMethodName();

						tempArrayListString = methods.getParameters();
						if(tempArrayListString != null) {
							boolean firstTime = true;
							tempString += "(";
							for(String parameter : tempArrayListString) {
								tempStringArray = parameter.split(" ");
								if(tempStringArray.length == 2) {
									if(firstTime)
										tempString += tempStringArray[1] + " : " + tempStringArray[0];
									else
										tempString += ", " + tempStringArray[1] + " : " + tempStringArray[0];
									firstTime = false;
								}
							}
							tempString += ")";
						}
						else
							tempString += methods.getParametersAsCommaSeparatedString();

						tempString += " : ";
						tempString += methods.getReturnType();
						tempString += "\n";
						ParseString.setParseStringTail(tempString);	
					}
				}

				//Creating the Constructor inside the classes
				for(MethodDeclarationStructure methods : constructorArray) {
					tempString = className + " : ";
					tempString += accessModifierMap.get(methods.getAccessModifier());
					tempString += methods.getMethodName();

					tempArrayListString = methods.getParameters();
					if(tempArrayListString != null) {
						boolean firstTime = true;
						tempString += "(";
						for(String parameter : tempArrayListString) {
							tempStringArray = parameter.split(" ");
							if(tempStringArray.length == 2) {
								if(firstTime)
									tempString += tempStringArray[1] + " : " + tempStringArray[0];
								else
									tempString += ", " + tempStringArray[1] + " : " + tempStringArray[0];
								firstTime = false;
							}
						}
						tempString += ")";
					}
					else
						tempString += methods.getParametersAsCommaSeparatedString();
					tempString += "\n";
					ParseString.setParseStringTail(tempString);
				}
			}
		} catch (Exception e) {
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

	private void optimizeDepencies() {
		tempString = "";
		tempStringArray = new String[10];
		boolean addToMatrix = true;

		ArrayList<ArrayList<String>> OptimizationMatrixList = new ArrayList();
		tempString = ParseString.getParseStringbody();
		String[] tempDependencyArr = tempString.split("\n");
		String associationArrorType = dependencyMap.get("associations");
		String usesArrorType = dependencyMap.get("uses");
		tempString = "";
		for(String dependency : tempDependencyArr) {
			if(dependency.indexOf(associationArrorType)>0 && dependency.indexOf(">")<0) {
				addToMatrix = true;
				ArrayList<String> tempStringArrayList = new ArrayList();
				tempStringArrayList.addAll(Arrays.asList(dependency.split(" ")));
				if(!OptimizationMatrixList.isEmpty()) {
					for(int index = 0;index<OptimizationMatrixList.size();index++) {
						if(tempStringArrayList.get(0).equals(OptimizationMatrixList.get(index).get(3)) 
								&& tempStringArrayList.get(3).equals(OptimizationMatrixList.get(index).get(0))) {

							OptimizationMatrixList.get(index).add(1, tempStringArrayList.get(2));
							addToMatrix = false;
						}
						if(tempStringArrayList.get(0).equals(OptimizationMatrixList.get(index).get(0)) 
								&& tempStringArrayList.get(3).equals(OptimizationMatrixList.get(index).get(3))) {
							OptimizationMatrixList.get(index).remove(2);
							OptimizationMatrixList.get(index).add(2, "\"*\"");
							addToMatrix = false;
						}
					}
				}

				if(addToMatrix) {
					OptimizationMatrixList.add(tempStringArrayList);	
				}
			}else if(dependency.indexOf(usesArrorType)>0){
				addToMatrix = true;
				ArrayList<String> tempStringArrayList = new ArrayList();
				tempStringArrayList.addAll(Arrays.asList(dependency.split(" ")));
				if(!OptimizationMatrixList.isEmpty()) {
					for(int index = 0;index<OptimizationMatrixList.size();index++) {
						if(tempStringArrayList.get(0).equals(OptimizationMatrixList.get(index).get(0)) 
								&& tempStringArrayList.get(3).equals(OptimizationMatrixList.get(index).get(3))) {
							addToMatrix = false;
						}
					}
				}
				if(addToMatrix) {
					OptimizationMatrixList.add(tempStringArrayList);	
				}

			}else {
				tempString += dependency + "\n";
			}
		}

		tempArrayListString = new ArrayList<String>();
		for(ArrayList<String> arrayList : OptimizationMatrixList) {
			tempString2 = "";
			for(String items : arrayList) {
				tempString2 += items; 
			}
			if(!tempArrayListString.contains(tempString2)) {
				tempArrayListString.add(tempString2);
				tempString += tempString2 + "\n";
			}
		}
		ParseString.overWriteParseStringBody(tempString);
	}

	public void renderUMLDiagram() {
		try {
			optimizeDepencies();
			SourceStringReader reader = new SourceStringReader(ParseString.getParseStringPlantUML());
			String desc = reader.generateImage(new File(UMLParser.UMLDiagramPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
