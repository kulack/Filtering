package org.cp.condition;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Condition {
	public final Set<String> getUniqueIdentifiers() {
		Set<String> uniqueIdentifiers = new LinkedHashSet<String>();
		addUniqueIdentifiers(uniqueIdentifiers);
		return uniqueIdentifiers;
	}
	
	public abstract boolean isTrue(ValueComparer valueProvider);
    public abstract String toSimpleString();
    
    abstract void addUniqueIdentifiers(Set<String> identifiers);
}
