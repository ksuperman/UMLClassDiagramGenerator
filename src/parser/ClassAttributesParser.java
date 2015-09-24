package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import mainCom.ParseString;
import utilities.UtilitiesFunctions;

public final class ClassAttributesParser {

	private static Map<String, String> accessModifierMap = new HashMap<>();
	private static Map<String, String> dataTypesMap = new HashMap<>();
	private static final String[] accessModifiers = { "public;+", "private;-", "protected;#" };
	private static final String[] dataTypes = { "int;int", "byte;byte", "short;short", "long;long", "float;float",
			"double;double", "boolean;boolean", "char;char", "String;String" };

	private ClassAttributesParser() {
	}

	public static ArrayList<String> getAttributesDeclarations(CompilationUnit cu, Map<String, String> classNames, Map<String, java.util.ArrayList<String>> dependencyList) {

		ArrayList<String> methodAttributes = new ArrayList<>();
		String[] tempStringArray = {}, tempStringArray1 = {};
		String tempString = "", accessModifiersType = "", dataTypeName = "", variableName = "";
		boolean accessModifiersFound = false, dataTypeFound = false, variableFound = false;

		try {

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
			String[] Temp = cu.toStringWithoutComments().split("\n");
			
			for (String node : Temp) {
				tempStringArray = node.split(";");
				for (String attributeString : tempStringArray) {
					try {
						System.out.println("\nLINE--------" + attributeString + "\n");
						//System.out.println("------------" + UtilitiesFunctions.isFunction(attributeString));
						
						if (!UtilitiesFunctions.isFunction(attributeString)) {
							tempStringArray1 = attributeString.split(" ");
							for (String attributePiece : tempStringArray1) {
								
								if(attributePiece.equalsIgnoreCase("class") || attributePiece.equalsIgnoreCase("interface"))
									break;
								//System.out.println("Now Processing attributePiece : " + attributePiece + "\n");

								if (dataTypeName != "" && dataTypeName != null && dataTypeFound && !variableFound) {
									if (UtilitiesFunctions.isArray(attributePiece)) {
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
									
									if (UtilitiesFunctions.isArray(attributePiece))
										dataTypeName = dataTypesMap.get(attributePiece.substring(0, attributePiece.indexOf("[")));
									else
										dataTypeName = dataTypesMap.get(attributePiece);
									
									if(dataTypeName == null || dataTypeName == "") {
											
										if(UtilitiesFunctions.isArray(attributePiece)) 
											dataTypeName = classNames.get(attributePiece.substring(0, attributePiece.indexOf("[")));
										else
											dataTypeName = classNames.get(attributePiece);
									}

									if (dataTypeName != "" && dataTypeName != null) {
										if (UtilitiesFunctions.isArray(attributePiece)) {
											dataTypeName += "[]";
										}
										dataTypeFound = true;
									}
								}

								//System.out.println("accessModifiersType-- " + accessModifiersType);
								//System.out.println("variableName ---  " + variableName);
								//System.out.println("dataTypeName ---  " + dataTypeName);

								if (dataTypeName != null && variableName != null && dataTypeName != ""
										&& variableName != "") {
									if (dataTypeName != "" && variableName != "") {
										if (accessModifiersType == "")
											accessModifiersType = accessModifierMap.get("public");
										//System.out.println("Finally Before !!! ---  " + accessModifiersType + dataTypeName + " "												+ variableName);
										methodAttributes.add(accessModifiersType + dataTypeName + " " + variableName);
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
								accessModifiersType = accessModifierMap.get("public");
							//System.out.println("Finally Before !!! ---  " + accessModifiersType + dataTypeName + " "									+ variableName);
							methodAttributes.add(accessModifiersType + dataTypeName + " " + variableName);
						}
						variableName = "";
						variableFound = false;
						dataTypeName = "";
						dataTypeFound = false;
						accessModifiersType = "";
						accessModifiersFound = false;
					}
					// methodAttributes
				}

			}
			/*List<TypeDeclaration> typeDeclationList = cu.getTypes();
			  for (Node node : typeDeclationList) {
				tempStringArray = ((TypeDeclaration) node).getMembers().toString().split(";");
				for (String attributeString : tempStringArray) {
					try {
						System.out.println("\nLINE--------" + attributeString + "\n");
						//System.out.println("------------" + UtilitiesFunctions.isFunction(attributeString));
						
						if (true) {
							tempStringArray1 = attributeString.split(" ");
							for (String attributePiece : tempStringArray1) {

								System.out.println("Now Processing attributePiece : " + attributePiece + "\n");

								if (dataTypeName != "" && dataTypeName != null && dataTypeFound && !variableFound) {
									if (UtilitiesFunctions.isArray(attributePiece)) {
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
										|| UtilitiesFunctions.isArray(attributePiece)) {
									if (UtilitiesFunctions.isArray(attributePiece))
										dataTypeName = dataTypesMap
												.get(attributePiece.substring(0, attributePiece.indexOf("[")));
									else
										dataTypeName = dataTypesMap.get(attributePiece);

									if (dataTypeName != "" && dataTypeName != null) {
										if (UtilitiesFunctions.isArray(attributePiece)) {
											dataTypeName += "[]";
										}
										dataTypeFound = true;
									}
								}

								System.out.println("accessModifiersType-- " + accessModifiersType);
								System.out.println("variableName ---  " + variableName);
								System.out.println("dataTypeName ---  " + dataTypeName);

								if (dataTypeName != null && variableName != null && dataTypeName != ""
										&& variableName != "") {
									if (dataTypeName != "" && variableName != "") {
										if (accessModifiersType == "")
											accessModifiersType = accessModifierMap.get("public");
										System.out.println("Finally Before !!! ---  " + accessModifiersType + dataTypeName + " "
												+ variableName);
										methodAttributes.add(accessModifiersType + dataTypeName + " " + variableName);
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
								accessModifiersType = accessModifierMap.get("public");
							System.out.println("Finally Before !!! ---  " + accessModifiersType + dataTypeName + " "
									+ variableName);
							methodAttributes.add(accessModifiersType + dataTypeName + " " + variableName);
						}
						variableName = "";
						variableFound = false;
						dataTypeName = "";
						dataTypeFound = false;
						accessModifiersType = "";
						accessModifiersFound = false;
					}
					// methodAttributes
				}

			}*/
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//System.out.println("Finally ---- " + methodAttributes.toString());
		}
		return methodAttributes;
	}

	public static ArrayList<String> getMethodDeclarations(CompilationUnit cu, String className) {
		//System.out.println(cu.toStringWithoutComments());
		ArrayList<String> MethodsArray = new ArrayList<>();
		String tempMethodList = "";
		String[] tempStringArray = {};
		ParseString.clearParseStringMethods();
		new MethodList().visit(cu, null);
		tempMethodList = ParseString.getParseStringMethods();
		//System.out.println(tempMethodList.toString());
		tempStringArray = tempMethodList.split("\\n");
		for (String methods : tempStringArray) {
			if (methods != "")
				MethodsArray.add(className + " : " + methods + "\n");
			//System.out.println(methods);
		}
		return MethodsArray;
	}

	private static class MethodList extends VoidVisitorAdapter {
		@Override
		public void visit(MethodDeclaration n, Object arg) {
			ParseString.setParseStringMethods(n.getDeclarationAsString(true, false, true));
		}
	}

}
