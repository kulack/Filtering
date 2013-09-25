package org.cp.pojoconditions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cp.condition.ValueComparer;

class ObjectFieldComparer implements ValueComparer {
	private static final Set<Class<?>> SUPPORTED_FIELD_TYPES = getSupportedFieldTypes();
	
	public final Object pojo;
	
	public ObjectFieldComparer(Object pojo) {
		this.pojo = pojo;
	}
	
	public boolean isTrue(String identifier, String operator, String value) {
		return evaluateSimpleCondition(identifier, operator, value);
	}
	
	public static List<FieldException> getUnsupportedFields(Class<?> clazz, Set<String> fields) {
		List<FieldException> fieldExceptions = new ArrayList<FieldException>();
		
		for(String fieldName : fields) {
			try {
				Field field = clazz.getDeclaredField(fieldName);
				Class<?> fieldType = field.getType();
				
				if(!SUPPORTED_FIELD_TYPES.contains(fieldType)) {
					fieldExceptions.add(new FieldTypeException(fieldName, fieldType, clazz));
				}
			} catch (NoSuchFieldException e) {
				fieldExceptions.add(new NonexistentFieldException(fieldName, clazz));
			}
		}
		
		return fieldExceptions;
	}
	
    private boolean evaluateSimpleCondition(String identifier, String operator, String value) {
    	boolean result = true;
    	
    	if(value.startsWith("'") && value.endsWith("'")) {
    		value = value.substring(1, value.length()-1);
    	}
    	value = value.replace("''", "'");
    	
    	String stringFieldValue = null;
    	
    	try {
    		Field field = pojo.getClass().getDeclaredField(identifier);
    		field.setAccessible(true);
    		
    		Object fieldValue = field.get(pojo);
    		stringFieldValue = fieldValue.toString();
    		
    		if(fieldValue instanceof Byte) {
    			if(operator.equals(">")) {
    				result = ((Byte)fieldValue).byteValue() > Byte.decode(value);
    			} else if(operator.equals("<")) {
    				result = ((Byte)fieldValue).byteValue() < Byte.decode(value);
    			} else if(operator.equals("=")) {
    				result = ((Byte)fieldValue).byteValue() == Byte.decode(value);
    			} else if(operator.equals("<=")) {
    				result = ((Byte)fieldValue).byteValue() <= Byte.decode(value);
    			} else { // comparator.equals(">=")
    				result = ((Byte)fieldValue).byteValue() >= Byte.decode(value);
    			}
    		} else if(fieldValue instanceof Short) {
    			if(operator.equals(">")) {
    				result = ((Short)fieldValue).shortValue() > Short.decode(value);
    			} else if(operator.equals("<")) {
    				result = ((Short)fieldValue).shortValue() < Short.decode(value);
    			} else if(operator.equals("=")) {
    				result = ((Short)fieldValue).shortValue() == Short.decode(value);
    			} else if(operator.equals("<=")) {
    				result = ((Short)fieldValue).shortValue() <= Short.decode(value);
    			} else { // comparator.equals(">=")
    				result = ((Short)fieldValue).shortValue() >= Short.decode(value);
    			}
    		} else if(fieldValue instanceof Integer) {
    			if(operator.equals(">")) {
    				result = ((Integer)fieldValue).intValue() > Integer.decode(value);
    			} else if(operator.equals("<")) {
    				result = ((Integer)fieldValue).intValue() < Integer.decode(value);
    			} else if(operator.equals("=")) {
    				result = ((Integer)fieldValue).intValue() == Integer.decode(value);
    			} else if(operator.equals("<=")) {
    				result = ((Integer)fieldValue).intValue() <= Integer.decode(value);
    			} else { // comparator.equals(">=")
    				result = ((Integer)fieldValue).intValue() >= Integer.decode(value);
    			}
    		} else if(fieldValue instanceof Long) {
    			if(operator.equals(">")) {
    				result = ((Long)fieldValue).longValue() > Long.decode(value);
    			} else if(operator.equals("<")) {
    				result = ((Long)fieldValue).longValue() < Long.decode(value);
    			} else if(operator.equals("=")) {
    				result = ((Long)fieldValue).longValue() == Long.decode(value);
    			} else if(operator.equals("<=")) {
    				result = ((Long)fieldValue).longValue() <= Long.decode(value);
    			} else { // comparator.equals(">=")
    				result = ((Long)fieldValue).longValue() >= Long.decode(value);
    			}
    		} else if(fieldValue instanceof Float) {
    			if(operator.equals(">")) {
    				result = ((Float)fieldValue).floatValue() > Float.valueOf(value);
    			} else if(operator.equals("<")) {
    				result = ((Float)fieldValue).floatValue() < Float.valueOf(value);
    			} else if(operator.equals("=")) {
    				result = ((Float)fieldValue).floatValue() == Float.valueOf(value);
    			} else if(operator.equals("<=")) {
    				result = ((Float)fieldValue).floatValue() <= Float.valueOf(value);
    			} else { // comparator.equals(">=")
    				result = ((Float)fieldValue).floatValue() >= Float.valueOf(value);
    			}
    		} else if(fieldValue instanceof Double) {
    			if(operator.equals(">")) {
    				result = ((Double)fieldValue).doubleValue() > Double.valueOf(value);
    			} else if(operator.equals("<")) {
    				result = ((Double)fieldValue).doubleValue() < Double.valueOf(value);
    			} else if(operator.equals("=")) {
    				result = ((Double)fieldValue).doubleValue() == Double.valueOf(value);
    			} else if(operator.equals("<=")) {
    				result = ((Double)fieldValue).doubleValue() <= Double.valueOf(value);
    			} else { // comparator.equals(">=")
    				result = ((Double)fieldValue).doubleValue() >= Double.valueOf(value);
    			}
    		} else if(fieldValue instanceof String){
    			int compareTo = stringFieldValue.compareTo(value);
    			if(operator.equals(">")) {
    				result = (compareTo > 0);
    			} else if(operator.equals("<")) {
    				result = (compareTo < 0);
    			} else if(operator.equals("=")) {
    				result = (compareTo == 0);
    			} else if(operator.equals("<=")) {
    				result = (compareTo <= 0);
    			} else { // comparator.equals(">=")
    				result = (compareTo >= 0);
    			}
    		} else {
    			throw new FieldTypeException(identifier, fieldValue.getClass(), pojo.getClass());
    		}
    	} catch(NoSuchFieldException e) {
    		throw new NonexistentFieldException(identifier, pojo.getClass());
    	} catch(IllegalAccessException e) {
    		throw new RuntimeException(e);
    	}
    	
    	return result;
    }
    
    public static Set<Class<?>> getSupportedFieldTypes() {
    	Set<Class<?>> supportedFieldTypes = new HashSet<Class<?>>();
    	supportedFieldTypes.add(Byte.TYPE);
    	supportedFieldTypes.add(Byte.class);
    	
    	supportedFieldTypes.add(Short.TYPE);
    	supportedFieldTypes.add(Short.class);
    	
    	supportedFieldTypes.add(Integer.TYPE);
    	supportedFieldTypes.add(Integer.class);
    	
    	supportedFieldTypes.add(Long.TYPE);
    	supportedFieldTypes.add(Long.class);
    	
    	supportedFieldTypes.add(Float.TYPE);
    	supportedFieldTypes.add(Float.class);
    	
    	supportedFieldTypes.add(Double.TYPE);
    	supportedFieldTypes.add(Double.class);
    	
    	supportedFieldTypes.add(String.class);
    	
    	return supportedFieldTypes;
    }
}
