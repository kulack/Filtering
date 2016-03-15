package org.cp.pojoconditions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
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
	
	public boolean isTrue(String identifier, boolean isMethod, String operator, String value) {
		return evaluateSimpleCondition(identifier, isMethod, operator, value);
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
				fieldExceptions.add(new NonexistentIdentifierException(fieldName, false, clazz));
			}
		}
		
		return fieldExceptions;
	}
	
    private boolean evaluateSimpleCondition(String identifier, boolean isMethod, String operator, String value) {
    	boolean result = true;
    	
    	if(value.startsWith("'") && value.endsWith("'")) {
    		value = value.substring(1, value.length()-1);
    	}
    	value = value.replace("''", "'");
    	
    	String stringIdentifierValue = null;
    	
    	try {
    		Object identifierValue = null;
    		
    		if(isMethod) {
    			Method method = pojo.getClass().getDeclaredMethod(identifier);
    			method.setAccessible(true);
    			identifierValue = method.invoke(pojo);
    		} else {
        		Field field = pojo.getClass().getDeclaredField(identifier);
        		field.setAccessible(true);
        		identifierValue = field.get(pojo);
    		}

    		stringIdentifierValue = identifierValue.toString();
    		
    		if(identifierValue instanceof Byte) {
    			if(operator.equals(">")) {
    				result = ((Byte)identifierValue).byteValue() > Byte.decode(value);
    			} else if(operator.equals("<")) {
    				result = ((Byte)identifierValue).byteValue() < Byte.decode(value);
    			} else if(operator.equals("=")) {
    				result = ((Byte)identifierValue).byteValue() == Byte.decode(value);
    			} else if(operator.equals("<=")) {
    				result = ((Byte)identifierValue).byteValue() <= Byte.decode(value);
    			} else if (operator.equals(">=")) {
    				result = ((Byte)identifierValue).byteValue() >= Byte.decode(value);
    			} else if (operator.equals("=~")) {
    			    throw new FieldTypeException(identifier, identifierValue.getClass(), pojo.getClass());
    			} else /* if (operator.equals("!~"))*/ {
    			    throw new FieldTypeException(identifier, identifierValue.getClass(), pojo.getClass());
    			}
    		} else if(identifierValue instanceof Short) {
    			if(operator.equals(">")) {
    				result = ((Short)identifierValue).shortValue() > Short.decode(value);
    			} else if(operator.equals("<")) {
    				result = ((Short)identifierValue).shortValue() < Short.decode(value);
    			} else if(operator.equals("=")) {
    				result = ((Short)identifierValue).shortValue() == Short.decode(value);
    			} else if(operator.equals("<=")) {
    				result = ((Short)identifierValue).shortValue() <= Short.decode(value);
    			} else if (operator.equals(">=")) {
    				result = ((Short)identifierValue).shortValue() >= Short.decode(value);
    			} else if (operator.equals("=~")) {
    			    throw new FieldTypeException(identifier, identifierValue.getClass(), pojo.getClass());
                } else /* if (operator.equals("!~"))*/ {
                    throw new FieldTypeException(identifier, identifierValue.getClass(), pojo.getClass());
                }
    		} else if(identifierValue instanceof Integer) {
    			if(operator.equals(">")) {
    				result = ((Integer)identifierValue).intValue() > Integer.decode(value);
    			} else if(operator.equals("<")) {
    				result = ((Integer)identifierValue).intValue() < Integer.decode(value);
    			} else if(operator.equals("=")) {
    				result = ((Integer)identifierValue).intValue() == Integer.decode(value);
    			} else if(operator.equals("<=")) {
    				result = ((Integer)identifierValue).intValue() <= Integer.decode(value);
    			} else if (operator.equals(">=")) {
    				result = ((Integer)identifierValue).intValue() >= Integer.decode(value);
    			} else if (operator.equals("=~")) {
    			    throw new FieldTypeException(identifier, identifierValue.getClass(), pojo.getClass());
                } else /* if (operator.equals("!~"))*/ {
                    throw new FieldTypeException(identifier, identifierValue.getClass(), pojo.getClass());
                }
    		} else if(identifierValue instanceof Long) {
    			if(operator.equals(">")) {
    				result = ((Long)identifierValue).longValue() > Long.decode(value);
    			} else if(operator.equals("<")) {
    				result = ((Long)identifierValue).longValue() < Long.decode(value);
    			} else if(operator.equals("=")) {
    				result = ((Long)identifierValue).longValue() == Long.decode(value);
    			} else if(operator.equals("<=")) {
    				result = ((Long)identifierValue).longValue() <= Long.decode(value);
    			} else if (operator.equals(">=")) {
    				result = ((Long)identifierValue).longValue() >= Long.decode(value);
    			} else if (operator.equals("=~")) {
    			    throw new FieldTypeException(identifier, identifierValue.getClass(), pojo.getClass());
                } else /* if (operator.equals("!~"))*/ {
                    throw new FieldTypeException(identifier, identifierValue.getClass(), pojo.getClass());
                }
    		} else if(identifierValue instanceof Float) {
    			if(operator.equals(">")) {
    				result = ((Float)identifierValue).floatValue() > Float.valueOf(value);
    			} else if(operator.equals("<")) {
    				result = ((Float)identifierValue).floatValue() < Float.valueOf(value);
    			} else if(operator.equals("=")) {
    				result = ((Float)identifierValue).floatValue() == Float.valueOf(value);
    			} else if(operator.equals("<=")) {
    				result = ((Float)identifierValue).floatValue() <= Float.valueOf(value);
    			} else if (operator.equals(">=")) {
    				result = ((Float)identifierValue).floatValue() >= Float.valueOf(value);
    			} else if (operator.equals("=~")) {
    			    throw new FieldTypeException(identifier, identifierValue.getClass(), pojo.getClass());
                } else /* if (operator.equals("!~"))*/ {
                    throw new FieldTypeException(identifier, identifierValue.getClass(), pojo.getClass());
                }
    		} else if(identifierValue instanceof Double) {
    			if(operator.equals(">")) {
    				result = ((Double)identifierValue).doubleValue() > Double.valueOf(value);
    			} else if(operator.equals("<")) {
    				result = ((Double)identifierValue).doubleValue() < Double.valueOf(value);
    			} else if(operator.equals("=")) {
    				result = ((Double)identifierValue).doubleValue() == Double.valueOf(value);
    			} else if(operator.equals("<=")) {
    				result = ((Double)identifierValue).doubleValue() <= Double.valueOf(value);
    			} else if (operator.equals(">=")) {
    				result = ((Double)identifierValue).doubleValue() >= Double.valueOf(value);
    			} else if (operator.equals("=~")) {
    			    throw new FieldTypeException(identifier, identifierValue.getClass(), pojo.getClass());
                } else /* if (operator.equals("!~"))*/ {
                    throw new FieldTypeException(identifier, identifierValue.getClass(), pojo.getClass());
                }
    		} else if(identifierValue instanceof Boolean && operator.equals("=")) {
    				result = ((Boolean)identifierValue).booleanValue() == Boolean.valueOf(value);
    		} else if(identifierValue instanceof String){
    		    if (operator.equals("=~")) {
    		        result = ((String)identifierValue).toLowerCase().contains(value.toLowerCase());
    		    } else if (operator.equals("!~")) {
    		        result = !((String)identifierValue).toLowerCase().contains(value.toLowerCase());
    		    } else {
    		        int compareTo = stringIdentifierValue.compareTo(value);
    		        if(operator.equals(">")) {
    		            result = (compareTo > 0);
    		        } else if(operator.equals("<")) {
    		            result = (compareTo < 0);
    		        } else if(operator.equals("=")) {
    		            result = (compareTo == 0);
    		        } else if(operator.equals("<=")) {
    		            result = (compareTo <= 0);
    		        } else if (operator.equals(">=")) {
    		            result = (compareTo >= 0);
    		        }
    		    }
    		} else {
    			throw new FieldTypeException(identifier, identifierValue.getClass(), pojo.getClass());
    		}
    	} catch(NoSuchFieldException e) {
    		throw new NonexistentIdentifierException(identifier, false, pojo.getClass());
		} catch (NoSuchMethodException e) {
			throw new NonexistentIdentifierException(identifier, true, pojo.getClass());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
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
    	
    	supportedFieldTypes.add(Boolean.TYPE);
    	supportedFieldTypes.add(Boolean.class);
    	
    	supportedFieldTypes.add(String.class);
    	
    	return supportedFieldTypes;
    }
}

