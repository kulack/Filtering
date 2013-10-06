package org.cp.condition;

public interface ValueComparer {
	public boolean isTrue(String identifier, boolean isMethod, String operator, String value);
}
