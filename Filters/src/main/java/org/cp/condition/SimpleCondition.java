package org.cp.condition;

import java.util.Set;

public class SimpleCondition extends Condition {
	private final String identifier;
	private final String operator;
	private final String value;
	
	public SimpleCondition(String identifier, String operator, String value) {
		this.identifier = identifier;
		this.operator = operator;
		this.value = value;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public String getOperator() {
		return operator;
	}

	public String getValue() {
		return value;
	}
	
	public boolean isTrue(ValueComparer valueProvider) {
		return valueProvider.isTrue(identifier, operator, value);
	}

	void addUniqueIdentifiers(Set<String> identifiers) {
		identifiers.add(identifier);
	}
	
	@Override
	public String toString() {
		return "SimpleCondition [identifier=" + identifier + ", operator="
				+ operator + ", value=" + value + "]";
	}
	
	public String toSimpleString() {
		StringBuilder sb = new StringBuilder("");
		sb.append(identifier);
		sb.append(operator);
		sb.append("'").append(value).append("'");
		
		return sb.toString();
	}
}
