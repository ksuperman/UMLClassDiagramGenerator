/**
 * 
 */
package utilities;

/**
 * @author rakshithk
 *
 */
public final class UtilitiesFunctions {
	
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
}
