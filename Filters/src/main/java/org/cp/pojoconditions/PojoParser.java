package org.cp.pojoconditions;

import org.parboiled.BaseParser;
import org.parboiled.Rule;

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
	public Rule Spacing() {
		return OneOrMore(AnyOf(" ").label("Whitespace"));
	}
    
    /**
     * A value is represented either by a number directly, or by
     * a string representation enclosed in single quotes.  Note that
     * inclusion or lack of quotes does not impact the type it is evaluated
     * as later.
     */
	public Rule Value() {
		return FirstOf(Sequence('\'', ZeroOrMore(FirstOf(NoneOf("'"), Sequence("'", "'"))), '\''), Sequence(OneOrMore(Digit()), Optional(".", OneOrMore(Digit()))));
	}
	
	public Rule NoParamMethod() {
		return Sequence(Identifier(), Optional(Spacing()), Ch('('), Optional(Spacing()), Ch(')'));
	}
	
    /**
     * An identifier is represented by one non-numeric character followed
     * by characters and numbers
     */
	public Rule Identifier() {
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
}
