package org.cp.condition;

import java.util.List;
import java.util.Set;

public class AndCondition extends Condition {
	private final List<Condition> conditions;

	public AndCondition(List<Condition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public String toString() {
		return "AndCondition [conditions=" + conditions + "]";
	}
	
	public boolean isTrue(ValueComparer valueProvider) {
		for(Condition condition : conditions) {
			if(!condition.isTrue(valueProvider)) {
				return false;
			}
		}
		
		return true;
	}
	
	void addUniqueIdentifiers(Set<String> identifiers) {
		for(Condition condition : conditions) {
			condition.addUniqueIdentifiers(identifiers);
		}
	}
	
	public String toSimpleString() {
		StringBuilder sb = new StringBuilder("(");
		
		boolean afterFirst = false;
		
		for(Condition condition : conditions) {
			if(afterFirst) {
				sb.append(" AND ");
			}
			sb.append(condition.toSimpleString());
			
			afterFirst = true;
		}
		
		sb.append(")");
		
		return sb.toString();
	}
}
