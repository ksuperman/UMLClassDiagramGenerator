/**
 * @author rakshithk
 *
 */
package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import mainCom.ParseString;

import utilities.UtilitiesFunctions;

public class ClassParser {
	private ParsedClass[] pc = new ParsedClass [100];
	private String javaProjectPath;
	private File[] javaFileList;
	public Map<String, String> classNames = new HashMap<>();

	// Compilation Unit Declaration.
	private CompilationUnit cu;
	private Map<String, CompilationUnit> cuMap;

	// Package String Value Declaration.
	private final String packageString = "package";

	// Modifiers String Value Declarations.
	private final String finalModifierString = "final";
	private final String staticModifierString = "static";
	private final String publicModifierString = "public";
	private final String abstractModifierString = "abstract";
	private final String privateModifierString = "private";
	private final String protectedModifierString = "protected";
	private final String classString = "class";

	// Package and Class related String values and Flag Declaration.
	private String packageName = "", modifierName = "", classType = "", className = "";
	private boolean packageFound = false, modifierFound = false, classFound = false, classNameFound = false;
	
	// Dependencies related Declaration.
	private final String[] dependencies = { "implements;implements", "extends;extends" ,"associations;associations","uses;uses"};
	private String dependenciesType = "";
	private boolean dependenciesFound = false;
	private ArrayList<String> tempDepClassArray;
	private Map<String, String> dependencyMap = new HashMap<>();
	private Map<String, java.util.ArrayList<String>> dependencyList = new HashMap<>();
	
	//Methods related Declaration.
	private ArrayList<String> methodsArray;
	private String tempMethod;
	
	//Constructors related Declartions
	private ArrayList<String> constructorArray;
	
	//Attribute related Declaration.
	private ArrayList<String> attributeArray;

	// Temp Variables used for Intermediate processing.
	private String line = "", temp = "";
	private java.util.ArrayList<String> list;
	private int tempInt = 0;
	private String[] tempStringArray;
	private String tempMethodList = "";
	private String[] tempStringArray2;

	public ClassParser(String javaProjectPath) {
		// Constructor for Initializing the
		this.javaProjectPath = javaProjectPath;

		javaFileList = new File(this.javaProjectPath).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				if (name.lastIndexOf(".") != -1) {
					String ext = name.substring(name.lastIndexOf("."));
					if (ext.equalsIgnoreCase(".java"))
						return true;
				}
				return false;
			}
		});

		// Creating Compilation Unit for all the Source files present in the Java Project path
		cuMap = new HashMap<>();
		for (int i = 0; i < javaFileList.length; i++) {

			FileInputStream in = null;
			try {
				classNames.put(javaFileList[i].getName().substring(0, javaFileList[i].getName().indexOf(".")), javaFileList[i].getName().substring(0, javaFileList[i].getName().indexOf(".")));
				in = new FileInputStream(javaProjectPath + "/" + javaFileList[i].getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				cu = JavaParser.parse(in);
			} catch (ParseException e) {
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			cuMap.put(javaFileList[i].getName(), cu);
		}

		dependencyMap = new HashMap<>();
		for (String dependency : dependencies) {
			tempStringArray = dependency.split(";");
			for (int x = 0; x < tempStringArray.length; x++) {
				dependencyMap.put(tempStringArray[0], tempStringArray[1]);
			}
		}
		tempStringArray = null;
	}

	public ParsedClass[] ParseClass() {
		try {

			for (int i = 0; i < javaFileList.length; i++) {
				cu = cuMap.get(javaFileList[i].getName());
				
				try {
					list = new java.util.ArrayList<String>(Arrays.asList(cu.toStringWithoutComments().split("\\n")));
					for (Iterator<String> StringIterator = list.iterator(); StringIterator.hasNext();) {

						temp = StringIterator.next();
						if (!packageFound && temp != "") {
							tempInt = UtilitiesFunctions.stringContains(temp.toLowerCase(), packageString);
							if (tempInt != -1) {
								tempStringArray = temp.split(" ");
								if (tempStringArray[0].equalsIgnoreCase(packageString)) {
									packageName = tempStringArray[1].substring(0, tempStringArray[1].length() - 1);
									packageFound = true;
									StringIterator.remove();
									temp = "";
								}
								tempStringArray = null;
							}
						}

						if (temp != "") {
							tempStringArray = temp.split(" ");
							for (int j = 0; j < tempStringArray.length; j++) {
								if (!classFound || className == "") {//Class Found and Class Name
									switch (tempStringArray[j]) {
									case "final":
										modifierName = finalModifierString;
										break;
									case "static":
										modifierName = staticModifierString;
										break;
									case "private":
										modifierName = privateModifierString;
										break;
									case "protected":
										modifierName = protectedModifierString;
										break;
									case "public":
										modifierName = publicModifierString;
										break;
									case "abstract":
										classType = abstractModifierString;
										modifierName = abstractModifierString;
										break;
									case "class":
									case "interface":
										if (classType == "")
											classType = tempStringArray[j];
										classFound = true;
										break;

									default:
										if (classFound) {
											className = tempStringArray[j];
											if (className.lastIndexOf("{") >= 0)
												className.substring(0, className.lastIndexOf("{"));
										}
										break;
									}
								} else {
									try {
										if (!dependenciesFound) {
											dependenciesType = dependencyMap.get(tempStringArray[j]);
											if (dependenciesType != null) {
												dependenciesFound = true;
												dependenciesType = tempStringArray[j];
												//System.out.println(className + "IF" + j + "-----------------" + dependenciesType);
											}
										}

										if (dependenciesFound && tempStringArray[j] != dependenciesType) {
											if (tempStringArray[j].indexOf(",") != -1) {
												tempDepClassArray = new ArrayList<>();
												tempDepClassArray.add(tempStringArray[j].substring(0,
														tempStringArray[j].lastIndexOf(",")));
												dependencyList.put(dependenciesType, tempDepClassArray);
											} else {
												tempDepClassArray = dependencyList.get(dependenciesType);
												if (tempDepClassArray != null)
													tempDepClassArray.add(tempStringArray[j]);
												else {
													tempDepClassArray = new ArrayList<>();
													tempDepClassArray.add(tempStringArray[j]);
												}
												dependencyList.put(dependenciesType, tempDepClassArray);
												//System.out.println(className + "Else" + j + "-----------------"+ tempStringArray[j] + " " + dependenciesType);
												// Nullify the Variable for next pass.
												dependenciesType = "";
												dependenciesFound = false;
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									} finally {

									}
									// System.out.println(tempStringArray[j]);//Handle
									// Inheritance and Child Classes
								}
							}
						}
					}
					
					//Getting Constructors
					constructorArray = ClassAttributesParser.getConstructorDeclarations(cu);
					
					//Getting the Class Method Declartions
					methodsArray = ClassAttributesParser.getMethodDeclarations(cu);

					
					
					//Convert Class instances inside methods to "Uses" Relationship
					/*	
					public void test(A1 a1, String A1)
					public void test(A2 a2)
					*/
					dependenciesType = "uses";
					for(String method : methodsArray) {
						if(method != null && method != "") {
							//System.out.println(method);
							tempStringArray = method.substring(method.indexOf("(")+1, method.indexOf(")")).split(",");
							for(String parameters : tempStringArray) {
								tempStringArray2 =  parameters.split(" ");
								if(classNames.containsKey(tempStringArray2[0])) {
									//System.out.println(className + " is dependent on the Interface " + tempStringArray2[0]);
									tempDepClassArray = dependencyList.get(dependenciesType);
									if (tempDepClassArray != null)
										tempDepClassArray.add(tempStringArray2[0]);
									else {
										tempDepClassArray = new ArrayList<>();
										tempDepClassArray.add(tempStringArray2[0]);
									}
									dependencyList.put(dependenciesType, tempDepClassArray);
								}
							}
						}
					}
										
					//Getting the Class Attribute Declartions
					attributeArray = ClassAttributesParser.getAttributesDeclarations(cu, classNames);
					
					//Convert Class declations inside other classes to Assocations
					Iterator<String> attribItrator = attributeArray.iterator();
					while(attribItrator.hasNext()) {
						String attribute = attribItrator.next();
						dependenciesType = "associations";
						//System.out.println("attribute ; " + attribute);
						tempStringArray = attribute.split(" ");
						if(UtilitiesFunctions.isArray(tempStringArray[1])) {
							if(UtilitiesFunctions.isArrayOrCollection(tempStringArray[1]) == "Collection") {
								temp = tempStringArray[1].substring(0, tempStringArray[1].indexOf("<"));
							}else
								temp = tempStringArray[1].substring(0, tempStringArray[1].indexOf("["));
						}
						else
							temp = tempStringArray[1];
							//System.out.println(temp);
						if(classNames.containsKey(temp)){
							//AttributeArray.remove(attribute);
							tempDepClassArray = dependencyList.get(dependenciesType);
							if (tempDepClassArray != null)
								tempDepClassArray.add(tempStringArray[1]);
							else {
								tempDepClassArray = new ArrayList<>();
								tempDepClassArray.add(tempStringArray[1]);
							}
							dependencyList.put(dependenciesType, tempDepClassArray);
							attribItrator.remove();
						}
					}
		
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					//Create the Parser Classes for each of the Source Files !!
					/*if (packageName == "")
						packageName = "DefaultPackage";
					ParseString.setParseString("package " + packageName + " #DDDDDD {\n");
					if (className != "") {
						ParseString.setParseString(classType + " " + className + " << " +   modifierName  + " >> " + "{\n");
						for(String attribute : attributeArray) {
							ParseString.setParseString(attribute + "\n");
						}
						ParseString.setParseString("}\n}\n");
					}
					// Creating the Dependecies between classes
					for (Map.Entry<String, ArrayList<String>> dependency : dependencyList.entrySet()) {
						String arrorType = dependencyMap.get(dependency.getKey());
						tempDepClassArray = dependency.getValue();
						for (String depClassName : tempDepClassArray) {
							if(UtilitiesFunctions.isArray(depClassName))
								ParseString.setParseStringTail(className + " \"1\" " + arrorType + " \"*\" " + depClassName.substring(0, depClassName.indexOf("[")) + "\n");
							else
								ParseString.setParseStringTail(className + " \"1\" " + arrorType + " \"1\" " + depClassName + "\n");
						}
					}
					
					//Creating the methods inside the classes
					for(String methods : methodsArray) {
						ParseString.setParseStringTail(className + " : " + methods + "\n");
					}*/
					
					if(className != null && className != "")	
						pc[i]= new ParsedClass(packageName, modifierName, classType, className, dependencyList, attributeArray, methodsArray, constructorArray);
					dependencyList.clear();
					tempDepClassArray = new ArrayList<>();

					line = "";
					temp = "";
					packageName = "";
					modifierName = "";
					className = "";
					classType = "";
					packageFound = false;
					modifierFound = false;
					classFound = false;
					classNameFound = false;
					dependenciesType = "";

				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return pc;
	}
}
