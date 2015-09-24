package mainCom;

public final class ParseString {
	private static String ParseStringhead = "";
	private static String ParseStringtail = "";
	private static String ParseStringMethods = "";

	public static String getParseString() {
		return "@startuml\n" + ParseStringhead + ParseStringtail +  "@enduml";
	}

	public static void setParseString(String parseString) {
		ParseStringhead += parseString;
	}
	
	public static void setParseStringTail(String parseString) {
		ParseStringtail += parseString;
	}

	public static String getParseStringMethods() {
		return ParseStringMethods;
	}
	
	public static void clearParseStringMethods() {
		ParseStringMethods = "";
	}

	public static void setParseStringMethods(String parseStringMethods) {
		ParseStringMethods += parseStringMethods + "\n";
	}
	
	
}
