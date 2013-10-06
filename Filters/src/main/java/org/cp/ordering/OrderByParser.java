package org.cp.ordering;

import static org.parboiled.errors.ErrorUtils.printParseErrors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cp.pojoconditions.PojoParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;

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
	public Rule OrderBy() {
		List<IdentifierOrder> identifiersWithOrder = new ArrayList<IdentifierOrder>();
		
		return Sequence(IgnoreCase("order"), Spacing(), IgnoreCase("by"), Spacing(), NoParamMethodOrIdentifierWithOrder(), identifiersWithOrder.addAll(pop()),
				ZeroOrMore(
				  Sequence(Optional(Spacing()), Ch(','), Optional(Spacing()), NoParamMethodOrIdentifierWithOrder(), identifiersWithOrder.addAll(pop()))
						), push(identifiersWithOrder), EOI);
	}
	
	/**
	 * Matches on 'identifier [asc|desc]
	 */
	public Rule NoParamMethodOrIdentifierWithOrder() {
		StringVar identifierName = new StringVar();
		Var<Boolean> isMethod = new Var<Boolean>(true);
		StringVar orderParam = new StringVar("ASC");
		
		return Sequence(FirstOf(Sequence(matchMethods, NoParamMethod(), identifierName.set(match())), Sequence(Identifier(), identifierName.set(match()), isMethod.set(false))), 
				         Optional(Sequence(Spacing(), FirstOf(IgnoreCase("asc"), IgnoreCase("desc")), orderParam.set(match()))),
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
    	OrderByParser parser = Parboiled.createParser(OrderByParser.class, matchMethods);
    	ParsingResult<List<IdentifierOrder>> result = new ReportingParseRunner<List<IdentifierOrder>>(parser.OrderBy()).run(orderBy);
    	
    	if(result.hasErrors()) {
    		throw new IllegalArgumentException(printParseErrors(result));
    	}
    	
    	return result.resultValue;
    }
}
