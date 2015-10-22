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
					/*if(modifierName.equals("static") || modifierName.equals("abstract"))
						ParseString.setParseString("<<" +   modifierName  + ">> ");
					else if(classType.equals("interface"))
						ParseString.setParseString(" << " +   classType  + " >> ");
					*/
					ParseString.setParseString(className);	
					
				}
						
				if(attributeArray.size() > 0){
					ParseString.setParseString("|");
				}
				for(String attribute : attributeArray) {
					tempStringArray = attribute.split(" ");
					//System.out.println("array " + attribute);
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
				
				
				//[Customer]1-0..*[Address]
				// Creating the Dependencies between classes
				System.out.println("Dependencies gererate in yUML\n");
				for (Map.Entry<String, ArrayList<String>> dependency : dependencyList.entrySet()) {
					//System.out.println("dependency.getKey()"+ dependency.getKey());
					String arrorType = dependencyMap.get(dependency.getKey());
					//System.out.println("arrorType"+arrorType);
					tempDepClassArray = dependency.getValue();
					for (String depClassName : tempDepClassArray) {
						if(UtilitiesFunctions.isArray(depClassName)) {
							System.out.println("[" + className + "]" + arrorType + "*[" + depClassName.substring(0, depClassName.indexOf("[")) + "],");
							ParseString.setParseStringTail("[" + className + "]" + arrorType + "*[" + depClassName.substring(0, depClassName.indexOf("[")) + "],");
						}
						else {
							ParseString.setParseStringTail("[" + className + "]" + arrorType + "1[" + depClassName + "],");
							System.out.println("[" + className + "]" + arrorType + "1[" + depClassName + "],");
						//ParseString.setParseStringTail("[" + className + "]1" + arrorType + "*[" + depClassName.substring(0, depClassName.indexOf("[")) + "],");
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
		
		/*try {
		String payloadMessage = "";
		String url = "http://yuml.me/diagram/plain/class/";
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
		payloadMessage = "dsl_text=" + ParseString.getParseStringYUML();
		byte[] bytes = payloadMessage.getBytes("UTF-8");
		payloadMessage = new String(bytes, "UTF-8");
		
		//Charset.forName("UTF-8").encode(payloadMessage);
		payloadMessage = payloadMessage.replaceAll(";", "\n");
		//payloadMessage = payloadMessage.replaceAll("\\s+", "%20");
		//payloadMessage = payloadMessage.replaceAll("\n", "%0A");
		System.out.println(payloadMessage);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-length", "" + payloadMessage.length());
		connection.setRequestProperty("User-Agent", "skynet");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		DataOutputStream output = new DataOutputStream(connection.getOutputStream());
		output.writeBytes(payloadMessage);//+ ParseString.getParseStringYUML());
		output.flush();
		output.close();
		
		System.out.println(connection.getResponseCode());
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		System.out.println("response  : " + response);
		String imageURL = "http://yuml.me/"+response;
		URL imageURLObj = new URL(imageURL);
		BufferedImage img = ImageIO.read(imageURLObj);
		
		File imagepngweb = new File("/home/rakshithk/webtest.png");
		ImageIO.write(img, "png", imagepngweb);
		System.out.println(img.toString());
	}
	catch(Exception e) {
		e.printStackTrace();
	}finally {
		
	}*/
		String parseString = ParseString.getParseStringYUML();
		//parseString = parseString.replaceAll("<","%3C");
		//parseString = parseString.replaceAll("<","%3E");
		String command = "echo \"" + parseString + "\" | yuml -v -t class -s scruffy -o /home/rakshithk/diagram.png";
		String[] Commands = new String[]{"bash","-c",command};
		String Temp;
		//System.out.println(command);
		try {
			Process currentshell = Runtime.getRuntime().exec(Commands);
			//currentshell.waitFor();
			 BufferedReader br = new BufferedReader(
		                new InputStreamReader(currentshell.getInputStream()));
			 while ((Temp = br.readLine()) != null)
	                System.out.println("line: " + Temp);
			 currentshell.waitFor();
			 System.out.println ("exit: " + currentshell.exitValue());
			 currentshell.destroy();
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		/*
		try {
			//String payloadMessage = ParseString.getParseStringYUML();
			//byte[] bytes = payloadMessage.getBytes("UTF-8");
			//payloadMessage = new String(bytes, "UTF-8");
			//System.out.println("Parse String : "+ ParseString.getParseStringYUML());
			//System.out.println("payloadMessage : " + payloadMessage );
			
			//String url = "http://yuml.me/diagram/class/"+ URLEncoder.encode(ParseString.getParseStringYUML());
			//url = url.replaceAll("\\s+", "%20");
			//url = url.replaceAll("\n", "%0A");
			System.out.println(ParseString.getParseStringYUML());
			String url = "http://yuml.me/diagram/class/"+ ParseString.getParseStringYUML();
			url = url.replaceAll("\\s+", "_");
			url = url.replaceAll("\n", "%0A");
			url = "http://yuml.me/diagram/class/"+ URLEncoder.encode(url);
			System.out.println("url : " + url);
			URL obj;
			obj = new URL(url);
			//URI uri = new URI("http//", "yuml.me/diagram/scruffy/class/" + ParseString.getParseStringYUML(), null);
			//url = "http://yuml.me/diagram/scruffy/class/[User]";
			//URL obj = uri.toURL();//new URL(uri.toASCIIString());
			System.out.println(obj.toExternalForm());
			BufferedImage img = ImageIO.read(obj);
			System.out.println(img.toString());
			File imagepng = new File(UMLParser.UMLDiagramPath);
			ImageIO.write(img, "png", imagepng);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
}
