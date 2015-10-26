package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import mainCom.ParseString;
import utilities.UtilitiesFunctions;

/**
 * @author rakshithk
 *
 */

public final class ClassAttributesParser {

	private static Map<String, String> accessModifierMap = new HashMap<>();
	private static Map<String, String> accessModifierCoveterMap = new HashMap<>();
	private static Map<String, String> dataTypesMap = new HashMap<>();
	private static Map<String, String> otherModifierMap = new HashMap<>();
	private static ArrayList<MethodDeclarationStructure> methodsArray;
	private static ArrayList<MethodDeclarationStructure> constructorArray;
	private static ArrayList<VariableDeclarationStructure> attributesArray;
	private static String variableAccessModifier;
	private static String[] variableType;
	private static String variableName;
	private static ArrayList<String> methodAttributes = new ArrayList<String>();
	private static ArrayList<String> otherVariableModifiers;
	private static  Map<String, String> allClassNames;
	//private static final String[] accessModifiersConverter = { "1;public", "2;private", "5;protected","0;package" };
	private static final String[] accessModifiers = { "public;public", "private;private", "protected;protected","package;package" };
	private static final String[] dataTypes = { "int;int", "byte;byte", "short;short", "long;long", "float;float","double;double", "boolean;boolean", "char;char", "String;String","Map;Map","List;List","ArrayList;ArrayList" };
	private static final String[] otherModifiers = {"static;static","synchronized;synchronized","final;final","abstract;abstract"};

	private ClassAttributesParser() {
	}

	public static void setupClass() {
		String[] tempStringArray = {};
		methodsArray  = new ArrayList<MethodDeclarationStructure>();
		constructorArray = new ArrayList<MethodDeclarationStructure>();
		attributesArray = new ArrayList<VariableDeclarationStructure>();
		methodAttributes = new ArrayList<String>();
		
		accessModifierMap = new HashMap<>();
		for (String modifiers : accessModifiers) {
			tempStringArray = modifiers.split(";");
			for (int x = 0; x < tempStringArray.length; x++) {
				accessModifierMap.put(tempStringArray[0], tempStringArray[1]);
			}
		}

		dataTypesMap = new HashMap<>();
		for (String dataType : dataTypes) {
			tempStringArray = dataType.split(";");
			for (int x = 0; x < tempStringArray.length; x++) {
				dataTypesMap.put(tempStringArray[0], tempStringArray[1]);
			}
		}

		otherModifierMap = new HashMap<>();
		for (String modifiers : otherModifiers) {
			tempStringArray = modifiers.split(";");
			for (int x = 0; x < tempStringArray.length; x++) {
				otherModifierMap.put(tempStringArray[0], tempStringArray[1]);
			}
		}
		
	/*	accessModifierCoveterMap = new HashMap<>();
		for (String modifiers : accessModifiersConverter) {
			tempStringArray = modifiers.split(";");
			for (int x = 0; x < tempStringArray.length; x++) {
				accessModifierCoveterMap.put(tempStringArray[0], tempStringArray[1]);
			}
		}*/
	}

	public static ArrayList<String> getAttributesDeclarations(CompilationUnit cu, Map<String, String> classNames) {
		
		allClassNames = classNames;
		attributesArray.clear();
		methodAttributes.clear();
		new AttributeList().visit(cu, null);
		
/*
			ArrayList<String> methodAttributes = new ArrayList<>();
		String[] tempStringArray = {}, tempStringArray1 = {};
		String tempString = "", accessModifiersType = "", dataTypeName = "", variableName = "";
		boolean accessModifiersFound = false, dataTypeFound = false, variableFound = false;

		try {
			
			String[] Temp = cu.toStringWithoutComments().split("\n");

			for (String node : Temp) {
				tempStringArray = node.split(";");
				for (String attributeString : tempStringArray) {
					try {
						if (!UtilitiesFunctions.isFunction(attributeString) && !UtilitiesFunctions.singleLineFunction(attributeString)) {
							if(UtilitiesFunctions.isArrayOrCollection(attributeString) == "Collection") {
								attributeString = attributeString.replaceAll(",\\s+", ",");
							}
							tempStringArray1 = attributeString.split(" ");
							for (String attributePiece : tempStringArray1) {

								if(attributePiece.equalsIgnoreCase("class") || attributePiece.equalsIgnoreCase("interface"))
									break;

								if (dataTypeName != "" && dataTypeName != null && dataTypeFound && !variableFound) {
									if (UtilitiesFunctions.isArrayOrCollection(attributePiece).equals("Array")) {
										attributePiece = attributePiece.substring(0, attributePiece.indexOf("["));
										dataTypeName += "[]";
									}
									variableName = attributePiece;
									variableFound = true;
								}

								if (accessModifierMap.containsKey(UtilitiesFunctions.cleanString(attributePiece))
										&& !accessModifiersFound && (attributePiece != "" || attributePiece != null)) {
									attributePiece = UtilitiesFunctions.cleanString(attributePiece);
									accessModifiersType = accessModifierMap.get(attributePiece);
									accessModifiersFound = true;
								}

								if ((dataTypesMap.containsKey(attributePiece) && !dataTypeFound
										&& (attributePiece != "" || attributePiece != null))
										|| UtilitiesFunctions.isArray(attributePiece) || classNames.containsValue(attributePiece)) {

									if (UtilitiesFunctions.isArray(attributePiece)) {
										if(UtilitiesFunctions.isArrayOrCollection(attributePiece) == "Collection") {
											dataTypeName = attributePiece.substring(0, attributePiece.indexOf("<"));
											dataTypeName = dataTypesMap.get(dataTypeName);
										}
										else
											dataTypeName = dataTypesMap.get(attributePiece.substring(0, attributePiece.indexOf("[")));
									}
									else
										dataTypeName = dataTypesMap.get(attributePiece);

									//Check for Class Declaration within other classes
									if(dataTypeName == null || dataTypeName == "") {

										if(UtilitiesFunctions.isArray(attributePiece)) { 
											if(UtilitiesFunctions.isArrayOrCollection(attributePiece) == "Collection") {
												dataTypeName = attributePiece.substring(attributePiece.indexOf("<")+1,attributePiece.indexOf(">"));
												dataTypeName = classNames.get(dataTypeName);
											}else
												dataTypeName = classNames.get(attributePiece.substring(0, attributePiece.indexOf("[")));	
										}
										else
											dataTypeName = classNames.get(attributePiece);
									}

									if (dataTypeName != "" && dataTypeName != null) {
										if (UtilitiesFunctions.isArray(attributePiece)) {
											if(UtilitiesFunctions.isArrayOrCollection(attributePiece) == "Collection" && !classNames.containsKey(dataTypeName)) {
												dataTypeName += attributePiece.substring(attributePiece.indexOf("<"), attributePiece.lastIndexOf(">")+1);
											}
											else
												dataTypeName += "[]";
										}
										dataTypeFound = true;
									}
								}

								if (dataTypeName != null && variableName != null && dataTypeName != "" && variableName != "") {
									if (dataTypeName != "" && variableName != "") {
										if (accessModifiersType == "")
											accessModifiersType = accessModifierMap.get("package");//Default Access modified if not Specified.
										methodAttributes.add(accessModifiersType + " " + dataTypeName + " " + variableName);
									}
									variableName = "";
									variableFound = false;
									dataTypeName = "";
									dataTypeFound = false;
									accessModifiersType = "";
									accessModifiersFound = false;
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (dataTypeName != "" && variableName != "") {
							if (accessModifiersType == "")
								accessModifiersType = accessModifierMap.get("package");//Default Access modified if not Specified.
							methodAttributes.add(accessModifiersType + " " + dataTypeName + " " + variableName);
						}
						variableName = "";
						variableFound = false;
						dataTypeName = "";
						dataTypeFound = false;
						accessModifiersType = "";
						accessModifiersFound = false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}*/
		return methodAttributes;
	}

	public static ArrayList<MethodDeclarationStructure> getMethodDeclarations(CompilationUnit cu) {
		methodsArray.clear();
		new MethodList().visit(cu, null);
		return (ArrayList<MethodDeclarationStructure>) methodsArray.clone();
	}

	public static ArrayList<MethodDeclarationStructure> getConstructorDeclarations(CompilationUnit cu) {
		constructorArray.clear();
		new ConstructorList().visit(cu, null);
		return (ArrayList<MethodDeclarationStructure>) constructorArray.clone();
	}

	public static MethodDeclarationStructure sanitizeMethodDeclarations(String methodString,String methodBody) {
		MethodDeclarationStructure localmethod = null;
		String accessModifier = null;//public,private
		ArrayList<String> otherModifiers = null;//Static,Sync
		String returnType = null;
		String methodName = null;
		boolean itemSetFlag = false;
		String parameterString = null;
		String[] parameterArray = null;
		ArrayList<String> parameters = null;

		try {
			String[] methodArray = methodString.split(" ");
			for(String item : methodArray) {
				itemSetFlag = false;
				if(accessModifierMap.containsKey(item) && (accessModifier == null || accessModifier == "")) {
					accessModifier = item;
					itemSetFlag = true;
				}
				if(otherModifierMap.containsKey(item) && !itemSetFlag) {
					if(otherModifiers == null)
						otherModifiers = new ArrayList<String>();
					otherModifiers.add(item);
					itemSetFlag = true;
				}
				if((returnType == null || returnType == "") && !itemSetFlag) {
					returnType = item;
					itemSetFlag = true;
				}
				if((methodName == null || methodName == "") && !itemSetFlag && item.indexOf("(")>0 && returnType != null) {
					methodName = item.substring(0, item.indexOf("("));
				}
			}

			if(methodName != null && methodName != "" && returnType != null && returnType != "") {
				parameterString = methodString.substring(methodString.indexOf("(")+1, methodString.indexOf(")"));
				if(parameterString != null && parameterString != "" && parameterString.length()>0) {
					parameterArray = parameterString.split(",");
					for(String parameter : parameterArray) {
						parameter = parameter.trim();
						if(parameters == null)
							parameters = new ArrayList<String>();
						parameters.add(parameter);	
					}	
				}
				localmethod = new MethodDeclarationStructure(accessModifier,otherModifiers,returnType,methodName,parameters,methodBody);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {}
		return localmethod;
	}

	public static MethodDeclarationStructure sanitizeConstructorDeclarations(String constructorString,String methodBody) {
		MethodDeclarationStructure localConstructor = null;
		String accessModifier = null;//public,private
		String constructorName = null;
		boolean itemSetFlag = false;
		String parameterString = null;
		String[] parameterArray = null;
		ArrayList<String> parameters = null;

		String[] methodArray = constructorString.split(" ");
		for(String item : methodArray) {
			itemSetFlag = false;
			if(accessModifierMap.containsKey(item) && (accessModifier == null || accessModifier == "")) {
				accessModifier = item;
				itemSetFlag = true;
			}
			if((constructorName == null || constructorName == "") && !itemSetFlag && item.indexOf("(")>0) {
				constructorName = item.substring(0, item.indexOf("("));
			}
		}
		if(constructorName != null && constructorName != "") {
			parameterString = constructorString.substring(constructorString.indexOf("(")+1, constructorString.indexOf(")"));
			if(parameterString != null && parameterString != "" && parameterString.length()>0) {
				parameterArray = parameterString.split(",");
				for(String parameter : parameterArray) {
					parameter = parameter.trim();
					if(parameters == null)
						parameters = new ArrayList<String>();
					parameters.add(parameter);	
				}	
			}
			localConstructor = new MethodDeclarationStructure(accessModifier,null,"",constructorName,parameters, methodBody);
		}
		return localConstructor;
	}

	private static class MethodList extends VoidVisitorAdapter {
		@Override
		public void visit(MethodDeclaration n, Object arg) {
			methodsArray.add(sanitizeMethodDeclarations(n.getDeclarationAsString(true, false, true),n.toStringWithoutComments()));
		}
	}

	private static class ConstructorList extends VoidVisitorAdapter {
		@Override
		public void visit(ConstructorDeclaration c, Object arg) {
			constructorArray.add(sanitizeConstructorDeclarations(c.getDeclarationAsString(),c.toStringWithoutComments()));
		}
	}
	
	private static class AttributeList extends VoidVisitorAdapter {
		@Override
		public void visit(FieldDeclaration n, Object arg) {

			variableAccessModifier = n.toStringWithoutComments();
			variableType = n.getType().toStringWithoutComments().split("\n");
			variableName = n.getVariables().toString();
			
			String[] tempStringArray = {};
			String temp = "";
			ArrayList<String> AttributeModifiers = new ArrayList<>();
			attributesArray.clear();		
			
			tempStringArray = variableAccessModifier.split("\n");
			for(String item : tempStringArray) {
				
				if(accessModifierMap.containsKey(item.split(" ")[0])){
					temp = accessModifierMap.get(item.split(" ")[0]);
				}
				else
					temp = accessModifierMap.get("package");
				AttributeModifiers.add(temp);
			}
			
			for(int i = 0; i<variableType.length ; i++) {
				for(String variables : variableName.split("\n")) {
					temp = variables.substring(1, variables.length()-1);
					for(String variable : temp.split(",")) {
						String temp2 = "";
						temp2 = AttributeModifiers.get(i);
						temp2 += " ";
						if(UtilitiesFunctions.isArray(variableType[i])) { 
							if(UtilitiesFunctions.isArrayOrCollection(variableType[i]) == "Collection" && allClassNames.containsKey(variableType[i].substring(variableType[i].indexOf("<")+1,variableType[i].indexOf(">")))) {
								variableType[i] = variableType[i].substring(variableType[i].indexOf("<")+1,variableType[i].indexOf(">"));
								variableType[i] += "[]";
							}
						}
						String temp3 = variable.trim().split(" ")[0];
						if(UtilitiesFunctions.isArray(temp3)) { 
							if(UtilitiesFunctions.isArrayOrCollection(variableType[i]) == "Array") {
								variableType[i] += "[]";
								temp3 = temp3.substring(0, temp3.indexOf("["));
							}
							
						}
						temp2 += variableType[i] + " " + temp3;
						methodAttributes.add(temp2);
					}
				}
			}
		}
	}
}
