package org.cp.ordering;

public class IdentifierOrder {
	private final String identifier;
	private final boolean isMethod;
	
	private final boolean ascending;
	
	public IdentifierOrder(String identifier, boolean isMethod, boolean ascending) {
		if(isMethod) {
			int lastRParen = identifier.lastIndexOf(')');
			int lastLParen = identifier.lastIndexOf('(', lastRParen);
			identifier = identifier.substring(0, lastLParen);
		}
		this.identifier = identifier;
		this.isMethod = isMethod;
		this.ascending = ascending;
	}

	@Override
	public String toString() {
		return "IdentifierOrder [identifier=" + identifier + ", isMethod="
				+ isMethod + ", ascending=" + ascending + "]";
	}

	public String getIdentifier() {
		return identifier;
	}
	
	public boolean isMethod() {
		return isMethod;
	}

	public boolean isAscending() {
		return ascending;
	}
}
