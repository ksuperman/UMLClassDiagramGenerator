/**
 * 
 */
package utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
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
	private static Stack<String> functionCheck = new Stack<>();
	
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
	public static boolean isClass(String attributePiece) {
		boolean isClass = false;
		String classPattern = "(.*[ ]+[class|interface|abstract]+[ ]+.*)";
		Pattern classmatchPattern = Pattern.compile(classPattern, Pattern.CASE_INSENSITIVE);
	    Matcher classMatcher = classmatchPattern.matcher(attributePiece);
	    isClass = classMatcher.find();
	    System.out.println("isClass---" + isClass);
		return isClass;
	}
	
	private static boolean openFunction(String attributePiece) {
		String functionPattern = "";
		boolean isFunctionOpen = false;
		Pattern matchPattern = null;
		Matcher functionMatcher = null;
		try {
		functionPattern = "(.*[(]{1}.*[)]{1}.*[{].*)";
		matchPattern = Pattern.compile(functionPattern, Pattern.CASE_INSENSITIVE);
	    functionMatcher = matchPattern.matcher(attributePiece);
	    isFunctionOpen = functionMatcher.find();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
		    functionMatcher = null;
		    matchPattern = null;
		}
		return isFunctionOpen;
	}
	
	private static boolean closeFunction(String attributePiece) {
		String functionPattern = "";
		boolean isFunctionClose = false;
		Pattern matchPattern = null;
		Matcher functionMatcher = null;
		try {
		functionPattern = "(.*[}].*)";
		matchPattern = Pattern.compile(functionPattern, Pattern.CASE_INSENSITIVE);
	    functionMatcher = matchPattern.matcher(attributePiece);
	    isFunctionClose = functionMatcher.find();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
		    functionMatcher = null;
		    matchPattern = null;
		}
		return isFunctionClose;
	}
	
/*	public static boolean isFunction(String attributePiece) {
	
		String functionPattern = "";
		String place = "";
		if(!isClass(attributePiece)) {
			if(!isFunction) {
				functionPattern = "(.*[{].*)";//functionPattern = "(.*[(]{1}.*[)]{1}.*[{].*)";
				place = "Opening";
			}
			else {
				functionPattern = "(.*[}].*)";
				place = "Closing";
			}
			Pattern matchPattern = Pattern.compile(functionPattern, Pattern.CASE_INSENSITIVE);
		    Matcher functionMatcher = matchPattern.matcher(attributePiece);
		    
		    System.out.println("isFunction : " + isFunction);
		    
			if(functionMatcher.find()) {
				if(place.equals("Closing") && (functionCheck.size() == 1)) {
					System.out.println("Popper Final: " + functionCheck.pop());
					isFunction=!isFunction;
				}
					
				else {
					if(place.equals("Closing") && !functionCheck.empty()) {
						System.out.println("Popper : " + functionCheck.pop());
					}
					if(place.equals("Opening")) {
						functionCheck.push("{");
						isFunction = true;
					}
				}
			}
		}
		return isFunction;
	}
	*/
		private static boolean anotherOpeningBracketFound(String attributePiece) {
			boolean anotherOpeningBracketFound = false;
			attributePiece.contains("{");
			return anotherOpeningBracketFound;
		}
		
		private static boolean matchingClosingBracketFound(String attributePiece) {
			boolean matchingClosingBracketFound = false;
			attributePiece.contains("}");
			return matchingClosingBracketFound;
		}
		
	 	public static boolean isFunction(String attributePiece) {
	
		String functionPattern = "";
		String place = "";
		
		if(!isFunction) {
			functionPattern = "(.*[(]{1}.*[)]{1}.*[{].*)";
			place = "Opening";	
		}
		else {
			if(anotherOpeningBracketFound(attributePiece)) {
				functionPattern = "([XXX])";
				functionCheck.push("{");
			}
			else
			{
				if(functionCheck.empty())
					functionPattern = "(.*[}].*)";
				else {
					if(matchingClosingBracketFound(attributePiece)) {
						functionCheck.pop();
					}
					functionPattern = "([XXX])";
				}
					
			}
			place = "Closing";
		}
		Pattern matchPattern = Pattern.compile(functionPattern, Pattern.CASE_INSENSITIVE);
	    Matcher functionMatcher = matchPattern.matcher(attributePiece);
	    
		if(functionMatcher.find())
			isFunction=!isFunction;
		
		functionMatcher = null;
		matchPattern = null;
		
		return isFunction;
	}
	public static boolean singleLineFunction(String attributePiece) {
		boolean singleLineFunction = false;
		String functionPattern = "(.*[(]{1}.*[)]{1}$)";
		Pattern matchPattern = Pattern.compile(functionPattern, Pattern.CASE_INSENSITIVE);
	    Matcher functionMatcher = matchPattern.matcher(attributePiece);
	    singleLineFunction = functionMatcher.find();
	    singleLineFunction = singleLineFunction && !attributePiece.contains("=");
		functionMatcher = null;
		matchPattern = null;
		return singleLineFunction;
	}
}
