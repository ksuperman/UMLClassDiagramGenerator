/**
 * 
 */
package utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rakshithk
 *
 */
public final class UtilitiesFunctions {
	
	private static boolean isFunction = false;
	
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
		if(attributePiece != null)
			return attributePiece.contains("[]");
		else
			return false;
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
