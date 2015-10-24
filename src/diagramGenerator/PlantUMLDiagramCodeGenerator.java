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
	private static Map<String, String> otherModifierMap = new HashMap<>();
	private static final String[] accessModifiers = { "public;+", "private;-"};//, "protected;#"
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
						
					//tempString = tempStringArray[1];
					//attribute = attribute.replaceFirst(tempStringArray[0], accessModifierMap.get(tempStringArray[0]))
					/*if(accessModifierMap.get(tempStringArray[0]) != null && accessModifierMap.get(tempStringArray[0]) != "")
						ParseString.setParseString(attribute.replaceFirst(tempStringArray[0], accessModifierMap.get(tempStringArray[0])) + "\n");*/
					
				}
				ParseString.setParseString("}\n}\n");

				//Creating the Dependencies between classes
				//System.out.println("Dependencies gererate in PlantUML\n");
				for (Map.Entry<String, ArrayList<String>> dependency : dependencyList.entrySet()) {
					//System.out.println("dependency.getKey()"+ dependency.getKey());
					String arrorType = dependencyMap.get(dependency.getKey());
					//System.out.println("arrorType"+arrorType);
					tempDepClassArray = dependency.getValue();
					for (String depClassName : tempDepClassArray) {
						if(UtilitiesFunctions.isArray(depClassName)) {
							
							tempString = className + " " + arrorType ;
							if(dependency.getKey().equals("associations"))//Denote Multiplicity for Association only
								tempString += " \"*\" ";
							else
								tempString += " ";
							tempString += depClassName.substring(0, depClassName.indexOf("["));
							//ParseString.setParseStringBody(tempString);
							//ParseString.setParseStringBody(className + " " + arrorType + " \"*\" " + depClassName.substring(0, depClassName.indexOf("[")) + "\n");
						}else {
							tempString = className + " " + arrorType ;
							if(dependency.getKey().equals("associations"))//Denote Multiplicity for Association only
								tempString += " \"1\" "; 
							else
								tempString += " ";
							tempString += depClassName;
							//ParseString.setParseStringBody(tempString);
							//ParseString.setParseStringBody(className + " " + arrorType + " \"1\" " + depClassName + "\n");
						}
						
						//Uses Lable applied for Interface
						if(dependency.getKey().equals("uses"))
							tempString += " : uses";
						tempString += "\n";
						ParseString.setParseStringBody(tempString);
					}
				}

				//System.out.println("className : " + className);
				//Creating the methods inside the classes
				for(MethodDeclarationStructure methods : methodsArray) {
					if(accessModifierMap.get(methods.getAccessModifier()) != null && accessModifierMap.get(methods.getAccessModifier()) != ""){
						//System.out.println(methods);
						tempString = className + " : ";
						//{static}
						tempArrayListString = methods.getOtherModifiers();
						if(tempArrayListString != null) {
							for(String otherModifiers : tempArrayListString) {
								if(otherModifierMap.containsKey(otherModifiers)) {
									tempString += "{" + otherModifierMap.get(otherModifiers) + "}";
									break;	
								}
							}	
						}
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
						
						tempString += " : ";
						tempString += methods.getReturnType();
						tempString += "\n";
						//System.out.println(tempString);
						ParseString.setParseStringTail(tempString);	
					}
				}
				//Old way
				/*for(String methods : methodsArray) {
					System.out.println("methods " + methods);
					//public void setMessage(String msg)
					//+setMessage(msg : String) : void
					methodSignature = methods.substring(methods.indexOf("(")+1, methods.indexOf(")"));
					if(methodSignature.length() > 0) {
						methodSignatureArr = methodSignature.split(",");
						methodSignature = "";
						
						for(String signature : methodSignatureArr) {
							System.out.println("signature " + signature);
							signatureArr = signature.split(" ");
							if(methodSignature == null || methodSignature == "")
								methodSignature = signatureArr[1] + " : " + signatureArr[0];
							else
								methodSignature += " ," + signatureArr[2] + " : " + signatureArr[1];
						}
						System.out.println("methodSignature : " + methodSignature);	
					}
					
					tempStringArray = methods.substring(0, methods.indexOf("(")).split(" ");
					//attribute = attribute.replaceFirst(tempStringArray[0], accessModifierMap.get(tempStringArray[0]))
					//ParseString.setParseString(tempStringArray[0].replaceFirst(tempStringArray[0], accessModifierMap.get(tempStringArray[0])) + "\n");
					tempString = className + " : " + accessModifierMap.get(tempStringArray[0]) + tempStringArray[2] + "(" +methodSignature + ") : " + tempStringArray[1]  + "\n";
					System.out.println(tempString);
					ParseString.setParseStringTail(tempString);
					//ParseString.setParseStringTail(className + " : " + methods.replaceFirst(tempStringArray[0], accessModifierMap.get(tempStringArray[0])) + "\n");
				}*/
				
				//Creating the methods inside the classes
				
				for(MethodDeclarationStructure methods : constructorArray) {
					//System.out.println(methods);
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
					//System.out.println(tempString);
					ParseString.setParseStringTail(tempString);
				}
				/*for(String constructors : constructorArray) {
					System.out.println("methods " + constructors);
					//public Optimist(ConcreteSubject sub)
					//+Optimist(sub : ConcreteSubject)
					methodSignature = constructors.substring(constructors.indexOf("(")+1, constructors.indexOf(")"));
					if(methodSignature.length() > 0) {
						methodSignatureArr = methodSignature.split(",");
						methodSignature = "";
						
						for(String signature : methodSignatureArr) {
							System.out.println("signature " + signature);
							signatureArr = signature.split(" ");
							if(methodSignature == null || methodSignature == "")
								methodSignature = signatureArr[1] + " : " + signatureArr[0];
							else
								methodSignature += " ," + signatureArr[2] + " : " + signatureArr[1];
						}
						System.out.println("methodSignature : " + methodSignature);	
					}
					
					tempStringArray = constructors.substring(0, constructors.indexOf("(")).split(" ");

					tempString = className + " : " + accessModifierMap.get(tempStringArray[0]) + tempStringArray[1] + "(" +methodSignature + ")" + "\n";
					System.out.println("Constructor : " + tempString);
					ParseString.setParseStringTail(tempString);
				}*/
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

	private void optimizeDepencies() {
		tempString = "";
		tempStringArray = new String[10];
		boolean addToMatrix = true;
		int i = 0,j = 0;

		ArrayList<ArrayList<String>> OptimizationMatrixList = new ArrayList();
	//	String[][] OptimizationMatrix = new String[20][10];
		tempString = ParseString.getParseStringbody();
		String[] tempDependencyArr = tempString.split("\n");
		String associationArrorType = dependencyMap.get("associations");
		String usesArrorType = dependencyMap.get("uses");
		tempString = "";
		for(String dependency : tempDependencyArr) {
			//System.out.println("dependency Processing === " + dependency );
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
					}
				}

				if(addToMatrix) {
					OptimizationMatrixList.add(tempStringArrayList);	
				}
			}else if(dependency.indexOf(usesArrorType)>0){
				/*Subject ..> Observer : uses
				Subject ..> Observer : uses*/
				
				addToMatrix = true;
				ArrayList<String> tempStringArrayList = new ArrayList();
				tempStringArrayList.addAll(Arrays.asList(dependency.split(" ")));

				if(!OptimizationMatrixList.isEmpty()) {
					for(int index = 0;index<OptimizationMatrixList.size();index++) {
						if(tempStringArrayList.get(0).equals(OptimizationMatrixList.get(index).get(0)) 
								&& tempStringArrayList.get(3).equals(OptimizationMatrixList.get(index).get(3))) {

							//OptimizationMatrixList.get(index).add(1, tempStringArrayList.get(2));
							addToMatrix = false;
						}
					}
				}

				if(addToMatrix) {
					OptimizationMatrixList.add(tempStringArrayList);	
				}
				
			}else {
				//OptimizationMatrixList.add(new ArrayList<String>(Arrays.asList(dependency.split(" "))));
				tempString += dependency + "\n";
			}
		}

		//System.out.println("Final Dependencies are \n");
		
		for(ArrayList<String> arrayList : OptimizationMatrixList) {
			for(String items : arrayList) {
				tempString += items; 
				//System.out.print(items);
			}
			tempString += "\n";
			//System.out.println();
		}
		
		ParseString.overWriteParseStringBody(tempString);

		/*
		for(String dependency : tempDependencyArr) {
			if(dependency.indexOf(arrorType)>0) {
				tempStringArray = dependency.split(" ");

				for(int index = 0;index<OptimizationMatrix.length;index++) {
					addToMatrix = true;
					if(tempStringArray[0] == OptimizationMatrix[index][3] && tempStringArray[3] == OptimizationMatrix[index][0]) {
						OptimizationMatrix[index][4] = OptimizationMatrix[index][3];
						OptimizationMatrix[index][3] = OptimizationMatrix[index][2];
						OptimizationMatrix[index][2] = OptimizationMatrix[index][1];
						OptimizationMatrix[index][1] = tempStringArray[2];
						addToMatrix = false;
					}
				}
				if(addToMatrix)
					OptimizationMatrix[i++] = tempStringArray;

				for(int index = 0;index<OptimizationMatrix.length;index++) {
					System.out.println("OptimizationMatrix" + OptimizationMatrix[index][0]);
					System.out.println("OptimizationMatrix" + OptimizationMatrix[index][1]);
					System.out.println("OptimizationMatrix" + OptimizationMatrix[index][2]);
					System.out.println("OptimizationMatrix" + OptimizationMatrix[index][3]);
				}

			}
		}
		 */
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
