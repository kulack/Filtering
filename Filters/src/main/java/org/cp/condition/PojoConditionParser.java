package org.cp.condition;

import java.lang.reflect.Field;

import org.parboiled.BaseParser;
import org.parboiled.Node;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.support.ParseTreeUtils;

/**
 * Base class of all calculator parsers in the org.parboiled.examples.calculators package.
 * Simply adds the public static main entry point.
 *
 * @param <V> the type of the main value object created by the parser
 */
@BuildParseTree
class PojoConditionParser extends BaseParser<String> {

	public Rule Condition() {
		return FirstOf(Sequence(Comparison(), EOI).skipNode(), Sequence(AndCondition(), EOI).skipNode(), Sequence(OrCondition(), EOI).skipNode());
	}
	
	public Rule OrCondition() {
		return Sequence(FirstOf(AndCondition(), Comparison()).skipNode(), OneOrMore(Sequence(Spacing(), Or(), Spacing(), FirstOf(AndCondition(), Comparison()).skipNode()).skipNode()).skipNode());
	}
	
	/**
	 * An and condition starts with a comparison, and continued with at least one additional
	 * comparison, where each is 
	 * @return
	 */
	public Rule AndCondition() {
		return Sequence(Comparison(), OneOrMore(Sequence(Spacing(), And(), Spacing(), Comparison()).skipNode()).skipNode());
	}
	
	/**
	 * Represents a simple comparison between an identifier and a value.  This takes the
	 * form of a valid identifier, optional whitespace, a comparison operator, optional whitespace,
	 * and then the value.
	 * 
	 * For example, the following are valid:
	 * a<1
	 * a<'1'
	 * a < 1
	 * a < 'some text'
	 * 
	 * while the following are invalid:
	 * a<cat (lacking quotes around the value)
	 */
    public Rule Comparison() {
    	return Sequence(Identifier(), Optional(Spacing()).suppressNode(), ComparisonOperator(), Optional(Spacing()).suppressNode(), Value());
    }

    /**
     * A rule used to match an arbitrary amount of whitespace.  The only
     * whitespace that is allowed in the condition is one or more spaces,
     * other whitespace characters (tabs, newlines, etc.) are not allowed.
     */
    @SuppressNode
	Rule Spacing() {
		return OneOrMore(AnyOf(" ").label("Whitespace"));
	}
    
    /**
     * Matches what we expect in a condition to represent OR
     * for two conditions.  The check is not case sensitive.
     */
    @SuppressNode
    Rule Or() {
    	return IgnoreCase("OR");
    }
    
    /**
     * Matches what we expect in a condition to represent AND
     * for two conditions.  The check is not case sensitive.
     */
    @SuppressNode
    Rule And() {
    	return IgnoreCase("AND");
    }

    /**
     * Comparison operators that we allow for our conditions.  These
     * include <=, >=, <, >, and =
     */
    @SuppressSubnodes
    Rule ComparisonOperator() {
    	return FirstOf("<=", ">=", "<", ">", "=");
    }
    
    /**
     * A value is represented either by a number directly, or by
     * a string representation enclosed in single quotes.  Note that
     * inclusion or lack of quotes does not impact the type it is evaluated
     * as later.
     */
    @SuppressSubnodes
	Rule Value() {
		return FirstOf(Sequence('\'', ZeroOrMore(FirstOf(NoneOf("'"), Sequence("'", "'"))), '\''), OneOrMore(Digit()));
	}
	
    /**
     * An identifier is reprented by one non-numeric character followed
     * by characters and numbers
     */
    @SuppressSubnodes
	Rule Identifier() {
		return Sequence(Character(), ZeroOrMore(CharacterOrDigit()));
	}

    /**
     * Represents legal characters after the first for a field
     * @return
     */
	public Rule CharacterOrDigit() {
		return FirstOf(Character(), Digit());
	}

	/**
	 * Represents legal characters for the start of a field in Java
	 */
	public Rule Character() {
		return FirstOf( 
				CharRange('A', 'Z'),
				CharRange('a', 'z'), 
				CharRange('\u00C0', '\u00D6'), 
				CharRange('\u00D8', '\u00F6'), 
				CharRange('\u00F8', '\u02FF'), 
				CharRange('\u0370', '\u037D'), 
				CharRange('\u037F', '\u1FFF'), 
				CharRange('\u200C', '\u200D'), 
				CharRange('\u2070', '\u218F'), 
				CharRange('\u2C00', '\u2FEF'), 
				CharRange('\u3001', '\uD7FF'), 
				CharRange('\uF900', '\uFDCF'), 
				CharRange('\uFDF0', '\uFFFD'),
				'_'
		);
	}

	/**
	 * Represents numbers
	 */
	public Rule Digit() {
		return CharRange('0', '9');
	}

    protected static boolean evaluateCondition(Object pojo, InputBuffer inputBuffer, Node<?> condition) {
    	if(condition.getLabel().equals("Comparison")) {
    		return evaluateSimpleCondition(pojo, inputBuffer, condition);
    	} else if(condition.getLabel().equals("AndCondition")){
    		return evaluateAndCondition(pojo, inputBuffer, condition);
    	} else if(condition.getLabel().equals("OrCondition")){
    		return evaluateOrCondition(pojo, inputBuffer, condition);
    	} else {
    		throw new IllegalArgumentException("Condition of wrong type: " + condition);
    	}
    }
    
	private static boolean evaluateOrCondition(Object pojo, InputBuffer inputBuffer, Node<?> condition) {
		boolean result = false;
		// System.out.println("OR");
		for(Node<?> childCondition : condition.getChildren()) {
			if(evaluateCondition(pojo, inputBuffer, childCondition)) {
				result = true;
			}
		}
		// System.out.println("==> " + result);
		return result;
	}
    
	private static boolean evaluateAndCondition(Object pojo, InputBuffer inputBuffer, Node<?> condition) {
		boolean result = true;
		// System.out.println("  AND");
		for(Node<?> simpleCondition : condition.getChildren()) {
			if(!evaluateSimpleCondition(pojo, inputBuffer, simpleCondition)) {
				result = false;
			}
		}
		// System.out.println("  ==> " + result);
		return result;
	}

    private static boolean evaluateSimpleCondition(Object pojo, InputBuffer inputBuffer, Node<?> condition) {
    	boolean result = true;
    	String identifier = ParseTreeUtils.getNodeText(condition.getChildren().get(0), inputBuffer);
    	String comparator = ParseTreeUtils.getNodeText(condition.getChildren().get(1), inputBuffer);
    	String value = ParseTreeUtils.getNodeText(condition.getChildren().get(2), inputBuffer);
    	
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
    			if(comparator.equals(">")) {
    				result = ((Byte)fieldValue).byteValue() > Byte.decode(value);
    			} else if(comparator.equals("<")) {
    				result = ((Byte)fieldValue).byteValue() < Byte.decode(value);
    			} else if(comparator.equals("=")) {
    				result = ((Byte)fieldValue).byteValue() == Byte.decode(value);
    			} else if(comparator.equals("<=")) {
    				result = ((Byte)fieldValue).byteValue() <= Byte.decode(value);
    			} else { // comparator.equals(">=")
    				result = ((Byte)fieldValue).byteValue() >= Byte.decode(value);
    			}
    		} else if(fieldValue instanceof Short) {
    			if(comparator.equals(">")) {
    				result = ((Short)fieldValue).shortValue() > Short.decode(value);
    			} else if(comparator.equals("<")) {
    				result = ((Short)fieldValue).shortValue() < Short.decode(value);
    			} else if(comparator.equals("=")) {
    				result = ((Short)fieldValue).shortValue() == Short.decode(value);
    			} else if(comparator.equals("<=")) {
    				result = ((Short)fieldValue).shortValue() <= Short.decode(value);
    			} else { // comparator.equals(">=")
    				result = ((Short)fieldValue).shortValue() >= Short.decode(value);
    			}
    		} else if(fieldValue instanceof Integer) {
    			if(comparator.equals(">")) {
    				result = ((Integer)fieldValue).intValue() > Integer.decode(value);
    			} else if(comparator.equals("<")) {
    				result = ((Integer)fieldValue).intValue() < Integer.decode(value);
    			} else if(comparator.equals("=")) {
    				result = ((Integer)fieldValue).intValue() == Integer.decode(value);
    			} else if(comparator.equals("<=")) {
    				result = ((Integer)fieldValue).intValue() <= Integer.decode(value);
    			} else { // comparator.equals(">=")
    				result = ((Integer)fieldValue).intValue() >= Integer.decode(value);
    			}
    		} else if(fieldValue instanceof Long) {
    			if(comparator.equals(">")) {
    				result = ((Long)fieldValue).longValue() > Long.decode(value);
    			} else if(comparator.equals("<")) {
    				result = ((Long)fieldValue).longValue() < Long.decode(value);
    			} else if(comparator.equals("=")) {
    				result = ((Long)fieldValue).longValue() == Long.decode(value);
    			} else if(comparator.equals("<=")) {
    				result = ((Long)fieldValue).longValue() <= Long.decode(value);
    			} else { // comparator.equals(">=")
    				result = ((Long)fieldValue).longValue() >= Long.decode(value);
    			}
    		} else if(fieldValue instanceof Float) {
    			if(comparator.equals(">")) {
    				result = ((Float)fieldValue).floatValue() > Float.valueOf(value);
    			} else if(comparator.equals("<")) {
    				result = ((Float)fieldValue).floatValue() < Float.valueOf(value);
    			} else if(comparator.equals("=")) {
    				result = ((Float)fieldValue).floatValue() == Float.valueOf(value);
    			} else if(comparator.equals("<=")) {
    				result = ((Float)fieldValue).floatValue() <= Float.valueOf(value);
    			} else { // comparator.equals(">=")
    				result = ((Float)fieldValue).floatValue() >= Float.valueOf(value);
    			}
    		} else if(fieldValue instanceof Double) {
    			if(comparator.equals(">")) {
    				result = ((Double)fieldValue).doubleValue() > Double.valueOf(value);
    			} else if(comparator.equals("<")) {
    				result = ((Double)fieldValue).doubleValue() < Double.valueOf(value);
    			} else if(comparator.equals("=")) {
    				result = ((Double)fieldValue).doubleValue() == Double.valueOf(value);
    			} else if(comparator.equals("<=")) {
    				result = ((Double)fieldValue).doubleValue() <= Double.valueOf(value);
    			} else { // comparator.equals(">=")
    				result = ((Double)fieldValue).doubleValue() >= Double.valueOf(value);
    			}
    		} else if(fieldValue instanceof String){
    			int compareTo = stringFieldValue.compareTo(value);
    			if(comparator.equals(">")) {
    				result = (compareTo > 0);
    			} else if(comparator.equals("<")) {
    				result = (compareTo < 0);
    			} else if(comparator.equals("=")) {
    				result = (compareTo == 0);
    			} else if(comparator.equals("<=")) {
    				result = (compareTo <= 0);
    			} else { // comparator.equals(">=")
    				result = (compareTo >= 0);
    			}
    		} else {
    			throw new IllegalArgumentException("Condition supplies an identifier (" + identifier + ") that matches a field with unsupported type in the object: " + fieldValue.getClass().getSimpleName());
    		}
    	} catch(NoSuchFieldException e) {
    		throw new IllegalArgumentException("Condition supplies an identifier (" + identifier + ") not found in the object");
    	} catch(IllegalAccessException e) {
    		throw new RuntimeException(e);
    	}
    	
    	// System.out.println("    " + identifier + comparator + value + " ==> " + result);
    	return result;
    }
}