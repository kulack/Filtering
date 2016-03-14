package org.cp.pojoconditions;

import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.rules.Rule;

/**
 * Provides a base set of rules that are useful for parsing various
 * grammars that revolved about simple java objects
 */
public abstract class PojoParser<V> extends BaseParser<V>{

    /**
     * A rule used to match an arbitrary amount of whitespace.  The only
     * whitespace that is allowed in the condition is one or more spaces,
     * other whitespace characters (tabs, newlines, etc.) are not allowed.
     */
	public Rule spacing() {
		return oneOrMore(anyOf(" ").label("Whitespace"));
	}
    
    /**
     * A value is represented either by a number directly, or by
     * a string representation enclosed in single quotes.  Note that
     * inclusion or lack of quotes does not impact the type it is evaluated
     * as later.
     */
	public Rule value() {
		return firstOf(sequence('\'', zeroOrMore(firstOf(noneOf("'"), sequence("'", "'"))), '\''), 
		               sequence(oneOrMore(digit()), optional(".", oneOrMore(digit()))));
	}
	
	public Rule noParamMethod() {
		return sequence(identifier(), optional(spacing()), ch('('), optional(spacing()), ch(')'));
	}
	
    /**
     * An identifier is represented by one non-numeric character followed
     * by characters and numbers
     */
	public Rule identifier() {
		return sequence(character(), zeroOrMore(characterOrDigit()));
	}

    /**
     * Represents legal characters after the first for a field
     * @return
     */
	public Rule characterOrDigit() {
		return firstOf(character(), digit());
	}

	/**
	 * Represents legal characters for the start of a field in Java
	 */
	public Rule character() {
		return firstOf( 
				charRange('A', 'Z'),
				charRange('a', 'z'), 
				charRange('\u00C0', '\u00D6'), 
				charRange('\u00D8', '\u00F6'), 
				charRange('\u00F8', '\u02FF'), 
				charRange('\u0370', '\u037D'), 
				charRange('\u037F', '\u1FFF'), 
				charRange('\u200C', '\u200D'), 
				charRange('\u2070', '\u218F'), 
				charRange('\u2C00', '\u2FEF'), 
				charRange('\u3001', '\uD7FF'), 
				charRange('\uF900', '\uFDCF'), 
				charRange('\uFDF0', '\uFFFD'),
				'_'
		);
	}

	/**
	 * Represents numbers
	 */
	public Rule digit() {
		return charRange('0', '9');
	}
}
