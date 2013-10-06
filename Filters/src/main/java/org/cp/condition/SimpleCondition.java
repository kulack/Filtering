package org.cp.condition;

import java.util.Set;

public class SimpleCondition extends Condition {
	private final String identifier;
	private final boolean isMethod;
	private final String operator;
	private final String value;
	
	public SimpleCondition(String identifier, boolean isMethod, String operator, String value) {
		if(isMethod) {
			int lastRParen = identifier.lastIndexOf(')');
			int lastLParen = identifier.lastIndexOf('(', lastRParen);
			identifier = identifier.substring(0, lastLParen);
		}
		this.identifier = identifier;
		this.isMethod = isMethod;
		this.operator = operator;
		this.value = value;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public boolean isMethod() {
		return isMethod;
	}
	
	public String getOperator() {
		return operator;
	}

	public String getValue() {
		return value;
	}
	
	public boolean isTrue(ValueComparer valueProvider) {
		return valueProvider.isTrue(identifier, isMethod, operator, value);
	}

	void addUniqueIdentifiers(Set<String> identifiers) {
		identifiers.add(identifier);
	}
	
	@Override
	public String toString() {
		return "SimpleCondition [identifier=" + identifier + ", isMethod="
				+ isMethod + ", operator=" + operator + ", value=" + value
				+ "]";
	}

	public String toSimpleString() {
		StringBuilder sb = new StringBuilder("");
		sb.append(identifier);
		sb.append(operator);
		sb.append("'").append(value).append("'");
		
		return sb.toString();
	}
}
