package org.cp.pojoconditions;

public class NonexistentIdentifierException extends FieldException {
	private static final long serialVersionUID = 6193446344987822644L;
	
	private final String identifier;
	private boolean isMethod;
	private final Class<?> pojoClass;
	
	public NonexistentIdentifierException(String identifier, boolean isMethod, Class<?> pojoClass) {
		super("Identifier " + identifier + " required by condition does not exist");
		this.identifier = identifier;
		this.isMethod = isMethod;
		this.pojoClass = pojoClass;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public boolean isMethod() {
		return isMethod;
	}

	public Class<?> getPojoClass() {
		return pojoClass;
	}
}
