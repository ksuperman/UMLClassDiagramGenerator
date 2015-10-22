package mainCom;

public final class ParseString {
	private static String ParseStringhead = "";
	private static String ParseStringbody = "";
	private static String ParseStringtail = "";
	private static String ParseStringMethods = "";

	//Setters
	
	public static String getParseStringPlantUML() {
		System.out.println("@startuml\n" + ParseStringhead + ParseStringbody + ParseStringtail +  "@enduml");
		return "@startuml\n" + ParseStringhead  + ParseStringbody + ParseStringtail +  "@enduml";
	}
	
	public static String getParseStringYUML() {
		//System.out.println(ParseStringtail);
		return ParseStringhead + ParseStringtail;
	}

	public static void setParseString(String parseString) {
		ParseStringhead += parseString;
	}
	
	public static void setParseStringTail(String parseString) {
		ParseStringtail += parseString;
	}
	
	public static void overWriteParseStringBody(String parseString) {
		ParseStringbody = parseString;
	}

	public static void setParseStringBody(String parseStringbody) {
		ParseStringbody += parseStringbody;
	}
	
	public static void clearParseStringMethods() {
		ParseStringMethods = "";
	}

	public static void setParseStringMethods(String parseStringMethods) {
		ParseStringMethods += parseStringMethods + "\n";
	}
	
	public static void clearParseStrings() {
		ParseStringhead = "";
		ParseStringbody = "";
		ParseStringtail = "";
		ParseStringMethods = "";
	}
	
	//Getters
	
	public static String getParseStringbody() {
		return ParseStringbody;
	}
	
	public static String getParseStringMethods() {
		return ParseStringMethods;
	}
	
	public static String getParseStringTail() {
		return ParseStringtail;
	}
}
