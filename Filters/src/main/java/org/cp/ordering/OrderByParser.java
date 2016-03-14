package org.cp.ordering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cp.pojoconditions.PojoParser;

import com.github.fge.grappa.Grappa;
import com.github.fge.grappa.rules.Rule;
import com.github.fge.grappa.run.ListeningParseRunner;
import com.github.fge.grappa.run.ParsingResult;
import com.github.fge.grappa.support.StringVar;
import com.github.fge.grappa.support.Var;

/**
 * Parses a statement of the form "order by fieldName1 [asc|desc] [, fieldName2 [asc|desc]]
 */
public class OrderByParser extends PojoParser<List<IdentifierOrder>> {
	protected final boolean matchMethods;
	
	public OrderByParser(Boolean matchMethods) {
		this.matchMethods = matchMethods;
	}
	
	/**
	 * Matches on 'order by IdentifierOrder[, IdentifierOrder, ...]
	 */
	public Rule orderBy() {
		List<IdentifierOrder> identifiersWithOrder = new ArrayList<IdentifierOrder>();
		
		return sequence(ignoreCase("order"), spacing(), ignoreCase("by"), spacing(), noParamMethodOrIdentifierWithOrder(), identifiersWithOrder.addAll(pop()),
				zeroOrMore(
				  sequence(optional(spacing()), ch(','), optional(spacing()), noParamMethodOrIdentifierWithOrder(), identifiersWithOrder.addAll(pop()))
						), push(identifiersWithOrder), EOI);
	}
	
	/**
	 * Matches on 'identifier [asc|desc]
	 */
	public Rule noParamMethodOrIdentifierWithOrder() {
		StringVar identifierName = new StringVar();
		Var<Boolean> isMethod = new Var<Boolean>(true);
		StringVar orderParam = new StringVar("ASC");
		
		return sequence(firstOf(sequence(matchMethods, noParamMethod(), identifierName.set(match())), sequence(identifier(), identifierName.set(match()), isMethod.set(false))), 
				         optional(sequence(spacing(), firstOf(ignoreCase("asc"), ignoreCase("desc")), orderParam.set(match()))),
				         push(Collections.singletonList(new IdentifierOrder(identifierName.get(), isMethod.get(), orderParam.get().equalsIgnoreCase("asc"))))
				         );
	}
	
    /**
     * Parses the given order by string and returns a list of identifiers with their order.  Allows
     * zero parameter methods and fields to be matched.
     * @throws IllegalArgumentException if the condition fails to parse
     */
	public static List<IdentifierOrder> parseOrderBy(String orderBy) {
		return parseOrderBy(orderBy, true);
	}
	
    /**
     * Parses the given order by string and returns a list of identifiers with their order
     * @throws IllegalArgumentException if the condition fails to parse
     */
	public static List<IdentifierOrder> parseOrderBy(String orderBy, boolean matchMethods) {
    	OrderByParser parser = Grappa.createParser(OrderByParser.class, matchMethods);
    	ListeningParseRunner<List<IdentifierOrder>> runner = new ListeningParseRunner<>(parser.orderBy());
    	ParsingResult<List<IdentifierOrder>> result = runner.run(orderBy);
    	// ParsingResult<List<IdentifierOrder>> result = new ReportingParseRunner<List<IdentifierOrder>>(parser.orderBy()).run(orderBy);
    	
    	if(!result.isSuccess()) {
    	    // TODO: Better parse error handling
    		throw new IllegalArgumentException("Failed parsing: " + orderBy);
    	}
    	
    	return result.getTopStackValue();
    }
}
