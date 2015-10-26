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
import java.sql.Array;
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
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import mainCom.ParseString;

import utilities.UtilitiesFunctions;

public class ClassParser {
	private ArrayList<ParsedClass> pc = new ArrayList<ParsedClass>();
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
	private final String voidReturnType = "void";
	private final String returnStatement = "return";

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
	private ArrayList<MethodDeclarationStructure> methodsArray;
	private String tempMethod;
	private Iterator<MethodDeclarationStructure> methodItr;
	MethodDeclarationStructure methoditrelement;
	
	//Constructors related Declartions
	private ArrayList<MethodDeclarationStructure> constructorArray;
	
	//Attribute related Declaration.
	private ArrayList<String> attributeArray;
	private final boolean optimizeGetterSetterVariableVisibility = true;
	private boolean getterMethodFound = false;
	private boolean setterMethodFound = false;
	private boolean publicGetter = true;
	private boolean publicSetter = true;
	private MethodDeclarationStructure GetterMethod = null;
	private MethodDeclarationStructure SetterMethod = null;
	private ArrayList<String> changedAttributes = null; 
	private Iterator<String> attribItrator;

	// Temp Variables used for Intermediate processing.
	private String line = "", temp = "";
	private int tempInt = 0;
	private String[] tempStringArray;
	private String tempMethodList = "";
	private String[] tempStringArray2;
	private String[] tempStringArray3;
	private String tempAttributeAccessModifier = "";
	private String tempattributeType = "";
	private String tempattributeName = "";
	private String methodBody = "";
	private ArrayList<String> list;
	private ArrayList<String> tempArrayList;
	private ArrayList<String> tempArrayList1;
	private String classBody = "";

	public ClassParser(String javaProjectPath) {
		// Constructor for Initializing the
		this.javaProjectPath = javaProjectPath;

		javaFileList = new File(this.javaProjectPath).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
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

	public ArrayList<ParsedClass> ParseClass() {
		try {
			//Setup the Static Class for Parsing
			ClassAttributesParser.setupClass();
			
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
												// Nullify the Variable for next pass.
												dependenciesType = "";
												dependenciesFound = false;
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									} finally {

									}
									//Handle Inheritance and Child Classes
								}
							}
						}
					}
					
					//Getting Constructors
					constructorArray = ClassAttributesParser.getConstructorDeclarations(cu);
					
					//Getting the Class Method Declartions
					methodsArray = ClassAttributesParser.getMethodDeclarations(cu);
					
					//Convert Class instances inside methods to "Uses" Relationship
					dependenciesType = "uses";
					for(MethodDeclarationStructure method : methodsArray) {
						ArrayList<String> tempArrayList = method.getParameters();
						if(tempArrayList != null) {
							for(String parameters : tempArrayList) {
								tempStringArray2 =  parameters.split(" ");
								if(classNames.containsKey(tempStringArray2[0])) {
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
					
					/*//Convert Class instances inside methods to "Uses" Relationship
					dependenciesType = "uses";
					for(MethodDeclarationStructure method : constructorArray) {
						ArrayList<String> tempArrayList = method.getParameters();
						if(tempArrayList != null) {
							for(String parameters : tempArrayList) {
								tempStringArray2 =  parameters.split(" ");
								if(classNames.containsKey(tempStringArray2[0])) {
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
					}*/
															
					//Getting the Class Attribute Declartions
					attributeArray = ClassAttributesParser.getAttributesDeclarations(cu, classNames);

					//Convert Class declations inside other classes to Assocations
					attribItrator = attributeArray.iterator();
					while(attribItrator.hasNext()) {
						String attribute = attribItrator.next();
						dependenciesType = "associations";
						tempStringArray = attribute.split(" ");
						if(UtilitiesFunctions.isArray(tempStringArray[1])) {
							if(UtilitiesFunctions.isArrayOrCollection(tempStringArray[1]) == "Collection") {
								temp = tempStringArray[1].substring(0, tempStringArray[1].indexOf("<"));
							}else
								temp = tempStringArray[1].substring(0, tempStringArray[1].indexOf("["));
						}
						else
							temp = tempStringArray[1];
						if(classNames.containsKey(temp)){
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
					
					//Check Method Body for Dependencies 
					boolean classObjectfound = false;
					String classObjectName = "";
					String classVariableName = "";
					
					dependenciesType = "associations";
					methodItr = methodsArray.iterator();
					while(methodItr.hasNext()) {
						methoditrelement = methodItr.next();
						methodBody = methoditrelement.getMethodBody();
						if(methodBody != null && methodBody != "") {
							tempStringArray = methodBody.split("\n");
							if(tempStringArray != null) {
								for(String line : tempStringArray) {
									tempStringArray2 = line.split(" ");
									classObjectfound = false;
									classObjectName = "";
									classVariableName = "";
									for(String item : tempStringArray2) {
										String tempVar = "";
										if(UtilitiesFunctions.isArrayOrCollection(item) == "Collection") {
											tempVar = item.substring(item.indexOf("<")+1,item.indexOf(">"));
										}else if(UtilitiesFunctions.isArrayOrCollection(item) == "Array") {
											tempVar = item.substring(0,item.indexOf("["));
										}else {
											tempVar = item;
										}
										if(classNames.containsKey(tempVar) && !classObjectfound) {
											classObjectfound = true;
											classObjectName = item;
										}else 
										if(classObjectName != null && classObjectName != "" && classObjectfound && (classVariableName == null  || classVariableName == "")) {
											classVariableName = item;
										}
									}
									if(classVariableName != "" && classVariableName != null) {
										if(UtilitiesFunctions.isArrayOrCollection(classObjectName) == "Collection") {
											classObjectName = classObjectName.substring(classObjectName.indexOf("<")+1, classObjectName.indexOf(">")) + "[]";
										}else if(UtilitiesFunctions.isArray(classVariableName)) {
											classObjectName += "[]";
										}
										tempDepClassArray = dependencyList.get(dependenciesType);
										if (tempDepClassArray != null)
											tempDepClassArray.add(classObjectName);
										else {
											tempDepClassArray = new ArrayList<>();
											tempDepClassArray.add(classObjectName);
										}
										dependencyList.put(dependenciesType, tempDepClassArray);
									}
								}
							}
						}
					}
					
					//Converting Getter and Setter Methods of Private Methods to Public access Method					
					if(optimizeGetterSetterVariableVisibility) {
						changedAttributes = new ArrayList<String>();
						attribItrator = attributeArray.iterator();
						while(attribItrator.hasNext()) {
							String attribute = attribItrator.next();
							
							getterMethodFound = false;
							setterMethodFound = false;
							publicGetter = false;
							publicSetter = false;
							GetterMethod = null;
							SetterMethod = null;
							tempArrayList = new ArrayList<String>();
							tempArrayList1 = new ArrayList<String>();
							
							if(attribute != null && attribute != "") {
								tempStringArray = attribute.split(" ");
								if(tempStringArray.length > 0) {
									tempAttributeAccessModifier = tempStringArray[0].trim();
									tempattributeType = tempStringArray[1].trim();
									tempattributeName = tempStringArray[2].trim();
									
									//Check for Each Method if the Setter and Getter Exists
									for(MethodDeclarationStructure method : methodsArray) {
										
										//check for Getter Method
										if(method.getReturnType().equals(tempattributeType) && !getterMethodFound) {
											methodBody = method.getMethodBody();
											if(methodBody != null && methodBody != "") {
												tempStringArray = methodBody.split("\n");
												if(tempStringArray != null) {
													for(String line: tempStringArray) {
														if(line.contains(returnStatement)) {
															if(line.trim().contentEquals(returnStatement + " " + tempattributeName + ";") || line.trim().contentEquals(returnStatement + " this." + tempattributeName + ";")) {
																getterMethodFound = true;
																if(method.getAccessModifier().equals(publicModifierString)) {
																	publicGetter = true;
																}
																GetterMethod = method;
																break;
															}
														}
													}
												}
											}
										}else {
											//Check for Setter method which should not return any value
											if(method.getReturnType().equals(voidReturnType) && !setterMethodFound){
												tempArrayList = method.getParameters();
												if(tempArrayList != null) {
													//Check if Method have parameters which are being sent that are same type as the Current Valiable
													for(String parameter : tempArrayList) {
														tempStringArray2 = parameter.split(" ");
														if(tempStringArray2 != null && tempStringArray2.length>0) {
															if(tempattributeType.equals(tempStringArray2[0].trim())) {
																tempArrayList1.add(tempStringArray2[1].trim());
															}
														}
													}
													//If the Current Method is void type and there are parameters which are being passed which are of same type as current Valiable
													if(tempArrayList1.size()>0) {
														methodBody = method.getMethodBody();
														if(methodBody != null && methodBody != "") {
															tempStringArray = methodBody.split("\n");
															if(tempStringArray != null) {
																for(String line: tempStringArray) {
																	if((line.contains("this."+tempattributeName) || line.contains(tempattributeName)) && line.contains("=")) {
																		for(String params : tempArrayList1) {
																			if((line.trim().contentEquals(tempattributeName + " = " + params + ";")) || (line.trim().contentEquals("this."+tempattributeName + " = " + params + ";"))) {
																				setterMethodFound = true;
																				if(method.getAccessModifier().equals(publicModifierString))
																					publicSetter = true;
																				SetterMethod = method;
																				break;
																			}
																		}
																	}
																}
															}
														}
													}	
												}
											}
										}
									}
								}
								//if Setter and Getter Methods are found for the Current Variable.
								if(SetterMethod != null && GetterMethod != null && getterMethodFound && setterMethodFound) {
									if(publicGetter && publicSetter) {
										//Make the Current Varaible also Public
										changedAttributes.add(attribute.replaceFirst(privateModifierString, publicModifierString));
										attribItrator.remove();
									}
									//remove the current methods from the methodlist
									methodItr = methodsArray.iterator();
									while(methodItr.hasNext()) {
										methoditrelement = methodItr.next();
										if(methoditrelement.equals(SetterMethod) || methoditrelement.equals(GetterMethod)) {
											methodItr.remove();
										}
									}
								}
							}
						}	
						if(changedAttributes != null) {
							attributeArray.addAll(changedAttributes);
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {					
					//Cleanup Methods
					tempArrayList1 = new ArrayList<String>();
					methodItr = methodsArray.iterator();
					while(methodItr.hasNext()) {
						MethodDeclarationStructure methoditrelement = methodItr.next();
						if(tempArrayList1.contains(methoditrelement.toString())) {
							methodItr.remove();
						}else {
							tempArrayList1.add(methoditrelement.toString());
						}
					}
					
					for(String test : attributeArray) {
						System.out.println("Final : " + test);
					}
					
					if(className != null && className != "") {	
						pc.add(new ParsedClass(packageName, modifierName, classType, className, dependencyList, (ArrayList<String>) attributeArray.clone(), (ArrayList<MethodDeclarationStructure>) methodsArray.clone(), (ArrayList<MethodDeclarationStructure>) constructorArray.clone()));
					}
					
					tempDepClassArray = new ArrayList<>();
					dependencyList.clear();
					methodsArray.clear();
					constructorArray.clear();
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
		} finally {}
		return pc;
	}
}
