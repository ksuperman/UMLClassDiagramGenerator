package mainCom;

public final class ParseString {

	private static String ParseString = "";

	public static String getParseString() {
		return "@startuml\n" + ParseString + "@enduml";
	}

	public static void setParseString(String parseString) {
		ParseString += parseString;
	}
	
}
