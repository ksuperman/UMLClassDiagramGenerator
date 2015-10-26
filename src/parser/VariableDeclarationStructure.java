package parser;

import java.util.ArrayList;

public class VariableDeclarationStructure {
	
	private String variableAccessModifier;
	private String variableType;
	private String variableName;
	private ArrayList<String> otherModifiers;
	
	public VariableDeclarationStructure(String variableAccessModifier, String variableType, String variableName) {
		this.variableAccessModifier = variableAccessModifier;
		this.variableType = variableType;
		this.variableName = variableName;
	}
}
