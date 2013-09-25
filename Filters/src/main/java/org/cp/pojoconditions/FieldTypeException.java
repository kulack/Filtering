package org.cp.pojoconditions;

public class FieldTypeException extends FieldException {
	private static final long serialVersionUID = 1799511503238592685L;
	
	private final String fieldName;
	private final Class<?> fieldType;
	private final Class<?> pojoClass;
	
	public FieldTypeException(String fieldName, Class<?> fieldType, Class<?> pojoClass) {
		super("Identifier " + fieldName + " required by condition matches a field with unsupported type");
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.pojoClass = pojoClass;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public Class<?> getFieldType() {
		return fieldType;
	}
	
	public Class<?> getPojoClass() {
		return pojoClass;
	}
}
