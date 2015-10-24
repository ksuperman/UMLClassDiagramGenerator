package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mainCom.ParseString;

public class ParsedClass {
	private String packageName;
	private String modifierName;
	private String classType;
	private String className;
	private Map<String, java.util.ArrayList<String>> dependencyList = new HashMap<>();
	private ArrayList<String> AttributeArray;
	private ArrayList<MethodDeclarationStructure> MethodsArray;
	private ArrayList<MethodDeclarationStructure> constructorArray;

	/**
	 * @param packageName
	 * @param modifierName
	 * @param classType
	 * @param className
	 * @param dependencyList
	 * @param attributeArray
	 * @param methodsArray2
	 */

	public ParsedClass(String packageName, String modifierName, String classType, String className,
			Map<String, ArrayList<String>> dependencyList, ArrayList<String> attributeArray,
			ArrayList<MethodDeclarationStructure> methodsArray,ArrayList<MethodDeclarationStructure> constructorArray) {
		if (packageName == "" || packageName == null)
			this.packageName = "DefaultPackage";
		else
			this.packageName = packageName;
		this.modifierName = modifierName;
		this.classType = classType;
		this.className = className;
		this.dependencyList.putAll(dependencyList);
		this.AttributeArray = attributeArray;
		this.MethodsArray = methodsArray;
		this.constructorArray = constructorArray;
		
		//System.out.println("MethodsArray : " + MethodsArray.toString());
	}

	public String getPackageName() {
		return packageName;
	}

	public String getModifierName() {
		return modifierName;
	}

	public String getClassType() {
		return classType;
	}

	public Map<String, java.util.ArrayList<String>> getDependencyList() {
		return dependencyList;
	}

	public ArrayList<String> getAttributeArray() {
		return AttributeArray;
	}

	public ArrayList<MethodDeclarationStructure> getMethodsArray() {
		return MethodsArray;
	}

	public ArrayList<MethodDeclarationStructure> getConstructors() {
		return constructorArray;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public String toString() {
		String returnString = "";
		returnString += "\n-----------------------------------------------------";
		returnString += "\nPackage Name : " + packageName;
		returnString += "\nClass Modifier : " + modifierName;
		returnString += "\nClass Type : " + classType;
		returnString += "\nClass Name : " + className;
		returnString += "\nAttributes Name : ";
		for(String attr : AttributeArray) {
			returnString += "\n" + attr;
		}
		returnString += "\nMethods Name : ";
		for(MethodDeclarationStructure method : MethodsArray) {
			returnString += "\n" + method.toString();
		}
		returnString += "\nConstructor : ";
		for(MethodDeclarationStructure constructor : constructorArray) {
			returnString += "\n" + constructor.toString();
		}
		returnString += "\nDepencies : ";
		for(Map.Entry<String, ArrayList<String>> dependent : dependencyList.entrySet()) {
			returnString += "\n" + dependent.getKey() + " : " + dependent.getValue().toString() ;
		}
		returnString += "\n-----------------------------------------------------";
		return returnString;
	}
}
