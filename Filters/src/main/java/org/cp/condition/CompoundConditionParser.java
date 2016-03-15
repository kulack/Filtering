package org.cp.condition;

import java.util.ArrayList;
import java.util.List;

import org.cp.ordering.IdentifierOrder;
import org.cp.pojoconditions.PojoParser;

import com.github.fge.grappa.Grappa;
import com.github.fge.grappa.rules.Rule;
import com.github.fge.grappa.run.ListeningParseRunner;
import com.github.fge.grappa.run.ParsingResult;
import com.github.fge.grappa.support.StringVar;
import com.github.fge.grappa.support.Var;

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
	
	public Rule condition() {
		return firstOf(sequence(simpleCondition(), EOI), sequence(andCondition(), EOI), sequence(orCondition(), EOI));
	}
	
	public Rule orCondition() {
		Var<List<Condition>> conditions = new Var<List<Condition>>(new ArrayList<Condition>());
		
		return sequence(
				  // starts with Simple or And Condition, which we pop off the stack and add to our list
				  firstOf(andCondition(), simpleCondition()), conditions.get().add(pop()),
				  
				  // followed by one or more Simple or And Conditions separated by OR, each
				  // of which we pop off the stack and add to our list
				  oneOrMore(
				    sequence(spacing(), or(), spacing(), firstOf(andCondition(), simpleCondition())), conditions.get().add(pop())
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
	public Rule andCondition() {
		Var<List<Condition>> conditions = new Var<List<Condition>>(new ArrayList<Condition>());
		
		return sequence(
				  // AndCondition starts with a simple condition, which we pop off the stack
				  // and add to our list
				  simpleCondition(), conditions.get().add(pop()),
				  
				  // followed by one or more simple conditions, separated by AND, each of which
				  // we pop off the stack and add to our list of conditions
				  oneOrMore(
					  sequence(spacing(), and(), spacing(), simpleCondition(), conditions.get().add(pop()))
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
    public Rule simpleCondition() {
    	StringVar identifier = new StringVar();
    	Var<Boolean> isMethod = new Var<Boolean>();
    	StringVar operator = new StringVar();
    	StringVar value = new StringVar();

    	return sequence(
    			firstOf(sequence(matchMethods, noParamMethod(), identifier.set(match()), isMethod.set(true)), sequence(identifier(), identifier.set(match()), isMethod.set(false))), 
    			optional(spacing()), 
    			comparisonOperator(), operator.set(match()), 
    			optional(spacing()), 
    			value(), value.set(match()),
    			push(new SimpleCondition(identifier.get(), isMethod.get(), operator.get(), value.get())));
    }
    
    /**
     * Matches what we expect in a condition to represent OR
     * for two conditions.  The check is not case sensitive.
     */
    Rule or() {
    	return ignoreCase("OR");
    }
    
    /**
     * Matches what we expect in a condition to represent AND
     * for two conditions.  The check is not case sensitive.
     */
    Rule and() {
    	return ignoreCase("AND");
    }

    /**
     * Comparison operators that we allow for our conditions.  These
     * include <=, >=, <, >, and =
     */
    Rule comparisonOperator() {
    	return firstOf("<=", ">=", "<", ">", "=", "=~", "!~");
    }
    
    /**
     * Parses the given condition string and returns a condition object
     * that reflects the string
     * @throws IllegalArgumentException if the condition fails to parse
     */
    public static Condition parseCondition(String condition, boolean matchMethods) {
    	CompoundConditionParser parser = Grappa.createParser(CompoundConditionParser.class, matchMethods);
        ListeningParseRunner<Condition> runner = new ListeningParseRunner<>(parser.condition());
        ParsingResult<Condition> result = runner.run(condition);
    	// ParsingResult<Condition> result = new ReportingParseRunner<Condition>(parser.condition()).run(condition);
    	
    	if (!result.isSuccess()) {
    		throw new IllegalArgumentException("Failed to parse: " + condition);
    	}
    	
    	return result.getTopStackValue();
    }

}