package parser;

import java.util.ArrayList;

public class MethodDeclarationStructure {

	private String accessModifier;//public,private
	private ArrayList<String> otherModifiers;//Static,Sync
	private String returnType;
	private String methodName;
	private ArrayList<String> parameters;
	private String methodBody;
	
	public MethodDeclarationStructure(String accessModifier, ArrayList<String> otherModifiers, String returnType, String methodName,
			ArrayList<String> parameters, String methodBody) {
		super();
		if(accessModifier != null && accessModifier != "")
			this.accessModifier = accessModifier;
		else
			this.accessModifier = "public";
		this.otherModifiers = otherModifiers;
		this.returnType = returnType;
		this.methodName = methodName;
		this.parameters = parameters;
		this.methodBody = methodBody;
	}
	public String getAccessModifier() {
		return accessModifier;
	}
	public ArrayList<String> getOtherModifiers() {
		return otherModifiers;
	}
	public String getReturnType() {
		return returnType;
	}
	public String getMethodName() {
		return methodName;
	}
	public ArrayList<String> getParameters() {
		return parameters;
	}
	public String getParametersAsCommaSeparatedString() {
		String tempString = "";
		boolean first = true;
		tempString += "(";
		if(parameters != null) {
			for(String parameter : parameters) {
				if(first) {
					tempString += parameter;
					first = false;
				}else {
					tempString += ", " + parameter;
				}
			}
		}
		tempString += ")";
		return tempString;
	}
	public String getMethodBody() {
		return methodBody;
	}
	
	public void setAccessModifier(String accessModifier) {
		this.accessModifier = accessModifier;
	}
	public void setOtherModifiers(ArrayList<String> otherModifiers) {
		this.otherModifiers = otherModifiers;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public void setParameters(ArrayList<String> parameters) {
		this.parameters = parameters;
	}
	
	@Override
	public String toString() {
		String tempString = "";
		boolean first = true;
		tempString = accessModifier;
		if(otherModifiers != null) {
			for(String othermodifier : otherModifiers) {
				tempString += " " + othermodifier;
			}	
		}
		tempString += " " + returnType;
		tempString += " " + methodName;
		tempString += "(";
		if(parameters != null) {
			for(String parameter : parameters) {
				if(first) {
					tempString += parameter;
					first = false;
				}else {
					tempString += ", " + parameter;
				}
			}
		}
		tempString += ")";
		return tempString;
	}
	
}
