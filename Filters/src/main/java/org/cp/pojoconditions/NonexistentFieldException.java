package org.cp.pojoconditions;

public class NonexistentFieldException extends FieldException {
	private static final long serialVersionUID = 6193446344987822644L;
	
	private final String fieldName;
	private final Class<?> pojoClass;
	
	public NonexistentFieldException(String fieldName, Class<?> pojoClass) {
		super("Identifier " + fieldName + " required by condition does not exist");
		this.fieldName = fieldName;
		this.pojoClass = pojoClass;
	}
	
	public String getFieldName() {
		return fieldName;
	}

	public Class<?> getPojoClass() {
		return pojoClass;
	}
}
