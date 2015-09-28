/**
 * 
 */
package utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rakshithk
 *
 */
public final class UtilitiesFunctions {
	
	private static boolean isFunction = false;
	private static String[] collections = {"Map;Map","List;List","ArrayList;ArrayList","Collection;Collection"};
	private static Map<String,String> collectionTypes;
	
	private UtilitiesFunctions(){};
	
	public static int stringContains(String Word,String Expr){
		int StringLocation = -1;
		try{
			StringLocation = Word.indexOf(Expr);
		}catch(Exception e){
			StringLocation = -1;
		}finally {
			return StringLocation;
		}
	}
	
	public static String cleanString(String input) {
		input = input.replace("[", "");
		return input;
	}

	public static boolean isArray(String attributePiece) {
		boolean flag = false;
		if(collectionTypes == null) {
			collectionTypes = new HashMap<>();
			for (String dataType : collections) {
				String[] tempStringArray = dataType.split(";");
				for (int x = 0; x < tempStringArray.length; x++) {
					collectionTypes.put(tempStringArray[0], tempStringArray[1]);
				}
			}
		}
		
		if(attributePiece != null) {
			for(Map.Entry<String,String> collectionType : collectionTypes.entrySet() ) {
				flag = attributePiece.contains(collectionType.getKey() + "<");
				if(flag)
					break;
			}
			if(!flag) {
				flag = attributePiece.contains("[]");
			}		
		}
		return flag;
	}
	
	public static String isArrayOrCollection(String attributePiece) {
		String flag = "";
		if(collectionTypes == null) {
			collectionTypes = new HashMap<>();
			for (String dataType : collections) {
				String[] tempStringArray = dataType.split(";");
				for (int x = 0; x < tempStringArray.length; x++) {
					collectionTypes.put(tempStringArray[0], tempStringArray[1]);
				}
			}
		}
		
		if(attributePiece != null) {
			for(Map.Entry<String,String> collectionType : collectionTypes.entrySet() ) {
				System.out.println("matches " + attributePiece.contains(collectionType.getKey() + "<"));
				if(attributePiece.contains(collectionType.getKey() + "<")) {
					flag = "Collection";
					break;
				}
			}	
			if(attributePiece.contains("[]") && flag == "")
				flag = "Array";
		}
		return flag;
	}
	
	public static boolean isFunction(String attributePiece) {
	
		String functionPattern = "";
		
		if(!isFunction)
			functionPattern = "(.*[(]{1}.*[)]{1}.*[{].*)";
		else
			functionPattern = "(.*[}].*)";
		
		Pattern matchPattern = Pattern.compile(functionPattern, Pattern.CASE_INSENSITIVE);
	    Matcher functionMatcher = matchPattern.matcher(attributePiece);
	    
		if(functionMatcher.find())
			isFunction=!isFunction;
		return isFunction;
	}
}
