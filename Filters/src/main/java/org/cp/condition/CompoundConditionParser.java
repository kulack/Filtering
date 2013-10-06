package org.cp.condition;

import static org.parboiled.errors.ErrorUtils.printParseErrors;

import java.util.ArrayList;
import java.util.List;

import org.cp.pojoconditions.PojoParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;

/**
 * Class that supports parsing non-nested AND and OR conditions of form:
 * a>'3'
 * a>3
 * a>3 and b>4 or c>5
 */
public class CompoundConditionParser extends PojoParser<Condition> {
	protected final boolean matchMethods;
	
	public CompoundConditionParser(Boolean matchMethods) {
		this.matchMethods = matchMethods;
	}
	
	public Rule Condition() {
		return FirstOf(Sequence(SimpleCondition(), EOI), Sequence(AndCondition(), EOI), Sequence(OrCondition(), EOI));
	}
	
	public Rule OrCondition() {
		Var<List<Condition>> conditions = new Var<List<Condition>>(new ArrayList<Condition>());
		
		return Sequence(
				  // starts with Simple or And Condition, which we pop off the stack and add to our list
				  FirstOf(AndCondition(), SimpleCondition()), conditions.get().add(pop()),
				  
				  // followed by one or more Simple or And Conditions separated by OR, each
				  // of which we pop off the stack and add to our list
				  OneOrMore(
				    Sequence(Spacing(), Or(), Spacing(), FirstOf(AndCondition(), SimpleCondition())), conditions.get().add(pop())
				    ),
				    
				  // and finally we push the collected list of conditions back onto the stack
				  // as a combined OrCondition
				  push(new OrCondition(conditions.get()))
				);
	}
	
	/**
	 * An and condition starts with a comparison, and continued with at least one additional
	 * comparison, where each is 
	 * @return
	 */
	public Rule AndCondition() {
		Var<List<Condition>> conditions = new Var<List<Condition>>(new ArrayList<Condition>());
		
		return Sequence(
				  // AndCondition starts with a simple condition, which we pop off the stack
				  // and add to our list
				  SimpleCondition(), conditions.get().add(pop()),
				  
				  // followed by one or more simple conditions, separated by AND, each of which
				  // we pop off the stack and add to our list of conditions
				  OneOrMore(
					  Sequence(Spacing(), And(), Spacing(), SimpleCondition(), conditions.get().add(pop()))
				  ), 
				  
				  // and finally we push the collected list of conditions back onto the stack
				  // as a combined AndCondition
				  push(new AndCondition(conditions.get()))
				 );
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
    public Rule SimpleCondition() {
    	StringVar identifier = new StringVar();
    	Var<Boolean> isMethod = new Var<Boolean>();
    	StringVar operator = new StringVar();
    	StringVar value = new StringVar();

    	return Sequence(
    			FirstOf(Sequence(matchMethods, NoParamMethod(), identifier.set(match()), isMethod.set(true)), Sequence(Identifier(), identifier.set(match()), isMethod.set(false))), 
    			Optional(Spacing()), 
    			ComparisonOperator(), operator.set(match()), 
    			Optional(Spacing()), 
    			Value(), value.set(match()),
    			push(new SimpleCondition(identifier.get(), isMethod.get(), operator.get(), value.get())));
    }
    
    /**
     * Matches what we expect in a condition to represent OR
     * for two conditions.  The check is not case sensitive.
     */
    Rule Or() {
    	return IgnoreCase("OR");
    }
    
    /**
     * Matches what we expect in a condition to represent AND
     * for two conditions.  The check is not case sensitive.
     */
    Rule And() {
    	return IgnoreCase("AND");
    }

    /**
     * Comparison operators that we allow for our conditions.  These
     * include <=, >=, <, >, and =
     */
    Rule ComparisonOperator() {
    	return FirstOf("<=", ">=", "<", ">", "=");
    }
    
    /**
     * Parses the given condition string and returns a condition object
     * that reflects the string
     * @throws IllegalArgumentException if the condition fails to parse
     */
    public static Condition parseCondition(String condition, boolean matchMethods) {
    	CompoundConditionParser parser = Parboiled.createParser(CompoundConditionParser.class, matchMethods);
    	ParsingResult<Condition> result = new ReportingParseRunner<Condition>(parser.Condition()).run(condition);
    	
    	if(result.hasErrors()) {
    		throw new IllegalArgumentException(printParseErrors(result));
    	}
    	
    	return result.resultValue;
    }

}