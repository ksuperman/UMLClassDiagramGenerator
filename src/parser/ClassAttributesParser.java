package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import mainCom.ParseString;
import utilities.UtilitiesFunctions;

public final class ClassAttributesParser {

	private static Map<String, String> accessModifierMap = new HashMap<>();
	private static Map<String, String> dataTypesMap = new HashMap<>();
	private static Map<String, String> otherModifierMap = new HashMap<>();
	private static ArrayList<MethodDeclarationStructure> methodsArray;
	private static ArrayList<MethodDeclarationStructure> constructorArray;
	private static final String[] accessModifiers = { "public;public", "private;private", "protected;protected","package;package" };
	private static final String[] dataTypes = { "int;int", "byte;byte", "short;short", "long;long", "float;float","double;double", "boolean;boolean", "char;char", "String;String","Map;Map","List;List","ArrayList;ArrayList" };
	private static final String[] otherModifiers = {"static;static","synchronized;synchronized","final;final","abstract;abstract"};

	private ClassAttributesParser() {
	}
	
	public static void setupClass() {
		String[] tempStringArray = {};
		methodsArray  = new ArrayList<MethodDeclarationStructure>();
		constructorArray = new ArrayList<MethodDeclarationStructure>();
		
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
	}

	public static ArrayList<String> getAttributesDeclarations(CompilationUnit cu, Map<String, String> classNames) {

		ArrayList<String> methodAttributes = new ArrayList<>();
		String[] tempStringArray = {}, tempStringArray1 = {};
		String tempString = "", accessModifiersType = "", dataTypeName = "", variableName = "";
		boolean accessModifiersFound = false, dataTypeFound = false, variableFound = false;

		try {
			/*
			accessModifierMap = new HashMap<>();
			for (String modifiers : accessModifiers) {
				tempStringArray = modifiers.split(";");
				for (int x = 0; x < tempStringArray.length; x++) {
					accessModifierMap.put(tempStringArray[0], tempStringArray[1]);
					System.out.println(accessModifierMap.get(tempStringArray[0]));
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
			}*/

			String[] Temp = cu.toStringWithoutComments().split("\n");
			
			for (String node : Temp) {
				tempStringArray = node.split(";");
				for (String attributeString : tempStringArray) {
					try {
						//System.out.println("\nLINE--------" + attributeString + "\n");
						//System.out.println("isFunction----------" + UtilitiesFunctions.isFunction(attributeString));
						
						if (!UtilitiesFunctions.isFunction(attributeString) && !UtilitiesFunctions.singleLineFunction(attributeString)) {
							//System.out.println("isarrayorcoll" + UtilitiesFunctions.isArrayOrCollection(attributeString));
							if(UtilitiesFunctions.isArrayOrCollection(attributeString) == "Collection") {
								attributeString = attributeString.replaceAll(",\\s+", ",");
							}
							//System.out.println("\nAfter LINE--------" + attributeString + "\n");
							tempStringArray1 = attributeString.split(" ");
							for (String attributePiece : tempStringArray1) {
								
								if(attributePiece.equalsIgnoreCase("class") || attributePiece.equalsIgnoreCase("interface"))
									break;
								//System.out.println("Now Processing attributePiece : " + attributePiece + "\n");

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
											//System.out.println(dataTypeName + "dataTypeName\n");
											//dataTypeName = dataTypeName.substring(0, dataTypeName.indexOf(">"));
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
												//System.out.println("Collection attruite" + dataTypeName);
												//dataTypeName = dataTypeName.substring(0, dataTypeName.indexOf(">"));
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
										//System.out.println("Finally Before !!! ---  " + accessModifiersType + dataTypeName + " " + variableName);
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
		}
		return methodAttributes;
	}

	public static ArrayList<MethodDeclarationStructure> getMethodDeclarations(CompilationUnit cu) {
		methodsArray.clear();
		new MethodList().visit(cu, null);
		/*ArrayList<String> MethodsArray = new ArrayList<>();
		String tempMethodList = "";
		String[] tempStringArray = {};
		ParseString.clearParseStringMethods();
		new MethodList().visit(cu, null);
		tempMethodList = ParseString.getParseStringMethods();
		tempStringArray = tempMethodList.split("\\n");
		for (String methods : tempStringArray) {
			if (methods != "")
				MethodsArray.add(methods);
		}
		for(MethodDeclarationStructure method : methodsArray) {
			System.out.println("Main  : " + method.toString());
		}
		return MethodsArray;
		*/
		return (ArrayList<MethodDeclarationStructure>) methodsArray.clone();
	}
	
	public static ArrayList<MethodDeclarationStructure> getConstructorDeclarations(CompilationUnit cu) {
		constructorArray.clear();
		new ConstructorList().visit(cu, null);
	/*	ArrayList<String> constructorArray = new ArrayList<>();
		String tempMethodList = "";
		String[] tempStringArray = {};
		ParseString.clearParseStringMethods();
		new ConstructorList().visit(cu, null);
		tempMethodList = ParseString.getParseStringMethods();
		tempStringArray = tempMethodList.split("\\n");
		for (String methods : tempStringArray) {
			if (methods != "")
				constructorArray.add(methods);
		}
		return constructorArray;*/
		return constructorArray;
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
		
		//public String operation()
		//public static void main(String[] args,String Text)
		//String operation()
		//abstract void draw();
		try {
			String[] methodArray = methodString.split(" ");
			for(String item : methodArray) {
				//System.out.println("item : " + item);
				itemSetFlag = false;
				//System.out.println("accessModifierMap : " + accessModifierMap.get("public") );
			//	System.out.println("accessModifierMap.containsKey(item) = " + accessModifierMap.containsKey(item));
				
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
			
			//System.out.println("returnType : " + returnType);
			//System.out.println("methodName : " + methodName);
			//System.out.println("accessModifier : " + accessModifier);
			
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
				//System.out.println("methodBody : " + methodBody);
				localmethod = new MethodDeclarationStructure(accessModifier,otherModifiers,returnType,methodName,parameters,methodBody);
				//System.out.println(localmethod.toString());
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
			//System.out.println("item : " + item);
			itemSetFlag = false;
			//System.out.println("accessModifierMap : " + accessModifierMap.get("public") );
		//	System.out.println("accessModifierMap.containsKey(item) = " + accessModifierMap.containsKey(item));
			
			if(accessModifierMap.containsKey(item) && (accessModifier == null || accessModifier == "")) {
				accessModifier = item;
				itemSetFlag = true;
			}
			
			if((constructorName == null || constructorName == "") && !itemSetFlag && item.indexOf("(")>0) {
				constructorName = item.substring(0, item.indexOf("("));
			}
		}
		//System.out.println("constructorName : " + constructorName);
		
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
			//System.out.println(localmethod.toString());
		}
		//System.out.println(localConstructor.toString());
		return localConstructor;
	}
	
	private static class MethodList extends VoidVisitorAdapter {
		@Override
		public void visit(MethodDeclaration n, Object arg) {
			//methodsArray = null;
			//System.out.println("Testing : " + n.getDeclarationAsString(true, false, true) + "\n" + n.toStringWithoutComments());
			methodsArray.add(sanitizeMethodDeclarations(n.getDeclarationAsString(true, false, true),n.toStringWithoutComments()));
			//ParseString.setParseStringMethods(n.getDeclarationAsString(true, false, true));
		}
	}
	
	private static class ConstructorList extends VoidVisitorAdapter {
		@Override
		public void visit(ConstructorDeclaration c, Object arg) {
			constructorArray.add(sanitizeConstructorDeclarations(c.getDeclarationAsString(),c.toStringWithoutComments()));
			//ParseString.setParseStringMethods(c.getDeclarationAsString());
			//System.out.println("Trying : " + c.getDeclarationAsString());
		}
	}
}
