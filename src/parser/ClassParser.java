/**
 * 
 */
package parser;import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

import mainCom.ParseString;
import utilities.UtilitiesFunctions;

/**
 * @author rakshithk
 *
 */
public class ClassParser{
	
	private final String packageString = "package";
	
	private final String finalModifierString = "final";
	private final String staticModifierString = "static";
	private final String publicModifierString = "public";
	private final String abstractModifierString = "abstract";
	private final String classString = "class";
	
	private String line = "",temp = "";
	
	private String packageName = "",modifierName = "",className = "";
	private boolean packageFound = false,modifierFound = false,classFound = false,classNameFound = false;
	
	private java.util.ArrayList<String> list ;
	
	private int tempInt = 0;
	private String[] tempStringArray;
	
	public void ParseClass(){
		try{
			File file = new File("/home/rakshithk/workspace/JavaQueueExample/src/CRM/ServiceRequest.java");
			FileReader fileRead = new FileReader("/home/rakshithk/workspace/JavaQueueExample/src/CRM/ServiceRequest.java");
			BufferedReader  buffFileRead = new BufferedReader(fileRead);
			list = new java.util.ArrayList<String>();
            
			while((line = buffFileRead.readLine()) != null) {
					list.add(line);
	                System.out.println(line);
            } 
			
			for(Iterator<String> StringIterator = list.iterator();StringIterator.hasNext();){
		
				temp = StringIterator.next();
				if(!packageFound && temp != ""){
					tempInt = UtilitiesFunctions.stringContains(temp.toLowerCase(),packageString);
					if(tempInt != -1){
						tempStringArray = temp.split(" ");
						if(tempStringArray[0].equalsIgnoreCase(packageString)){
							packageName = tempStringArray[1].substring(0, tempStringArray[1].length()-1);
							packageFound = true;
							StringIterator.remove();
							temp = "";
						}
						tempStringArray = null;
					}	
				}
				
				if(temp != ""){
					tempStringArray = temp.split(" ");
					for(int i=0;i<tempStringArray.length;i++){
						if(!classFound || className == "") {
							switch(tempStringArray[i]){
							case "final" :
								modifierName = finalModifierString;
								break;
							case "static" :
								modifierName = staticModifierString;
								break;
							case "public" :
								modifierName = publicModifierString;
								break;
							case "abstract" :
								modifierName = abstractModifierString;
								break;
							case "class" :
								classFound = true;
								break;
								
							default:
								if(classFound) {
									className = tempStringArray[i];
									if(className.lastIndexOf("{")>=0)
										className.substring(0, className.lastIndexOf("{"));
								}
							break;
							}	
						}else {
							//Handle Inheritance and Child Classes
						}
					}

				}

			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			System.out.println("---------------------------------------------------"+list.toString());
			if(packageName == "")
				packageName = "Default Package";
			ParseString.setParseString("package " + packageName + " #DDDDDD {\n");
			if(className != "")
				ParseString.setParseString("class " + className + "\n}\n");
		}
	}
}
