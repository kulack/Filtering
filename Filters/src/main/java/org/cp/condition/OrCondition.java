package org.cp.condition;

import java.util.List;
import java.util.Set;

public class OrCondition extends Condition {
	private final List<Condition> conditions;

	public OrCondition(List<Condition> conditions) {
		this.conditions = conditions;
	}

	public boolean isTrue(ValueComparer valueProvider) {
		for(Condition condition : conditions) {
			if(condition.isTrue(valueProvider)) {
				return true;
			}
		}
		
		return false;
	}
	
	void addUniqueIdentifiers(Set<String> identifiers) {
		for(Condition condition : conditions) {
			condition.addUniqueIdentifiers(identifiers);
		}
	}
	
	@Override
	public String toString() {
		return "OrCondition [conditions=" + conditions + "]";
	}
	
	public String toSimpleString() {
		StringBuilder sb = new StringBuilder("(");
		
		boolean afterFirst = false;
		
		for(Condition condition : conditions) {
			if(afterFirst) {
				sb.append(" OR ");
			}
			sb.append(condition.toSimpleString());
			
			afterFirst = true;
		}
		
		sb.append(")");
		
		return sb.toString();
	}
}
