package org.cp.pojoconditions;

public class IncompatibleFieldsException extends FieldException {
	private final String identifier;;
	
	private final Class<?> fieldType1;
	private final Class<?> pojoClass1;

	private final Class<?> fieldType2;
	private final Class<?> pojoClass2;
	
	public IncompatibleFieldsException(String identifier, Class<?> fieldType1, Class<?> pojoClass1, Class<?> fieldType2, Class<?> pojoClass2) {
		super("Identifier " + identifier + " matches incompatible types in different objects");
		this.identifier = identifier;
		this.fieldType1 = fieldType1;
		this.pojoClass1 = pojoClass1;
		this.fieldType2 = fieldType2;
		this.pojoClass2 = pojoClass2;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public Class<?> getFieldType1() {
		return fieldType1;
	}

	public Class<?> getPojoClass1() {
		return pojoClass1;
	}

	public Class<?> getFieldType2() {
		return fieldType2;
	}

	public Class<?> getPojoClass2() {
		return pojoClass2;
	}
}
