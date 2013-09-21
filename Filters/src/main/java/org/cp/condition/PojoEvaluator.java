package org.cp.condition;
import static org.parboiled.errors.ErrorUtils.printParseErrors;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.parboiled.Node;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;


public class PojoEvaluator {
	private final ParsingResult<?> parsingResult;
	
	/**
	 * Builds a PojoEvaluator to determine if supplied pojos
	 * match the condition
	 * @throws IllegalArgumentException if the condition is invalid
	 */
	private PojoEvaluator(String condition) {
    	PojoConditionParser parser = Parboiled.createParser(PojoConditionParser.class);
    	ParsingResult<?> result = new RecoveringParseRunner(parser.Condition()).run(condition);
    	
    	if(result.hasErrors()) {
    		throw new IllegalArgumentException(printParseErrors(result));
    	}
    	
    	parsingResult = result;
	}
	
	/**
	 * Builds a evalulator that will determine if supplied
	 * pojos meet the condition used to build the evaluator.
	 * 
	 * The condition should specify fields of the pojo, and
	 * compare them with numbers or characters using one of
	 * the following operators: <, <=, >, >=, and =
	 * 
	 * Multiple comparisons can be included and should be
	 * separated by either AND or OR.  Conditions cannot
	 * be nested  '(' and ')' are not allowed except as
	 * part of the value (the right hand of the comparison)
	 * @return an evaluator that will determine if a given
	 *         matches the condition
	 */
	public static PojoEvaluator forCondition(String condition) {
		return new PojoEvaluator(condition);
	}
	
	/**
	 * Determines if the pojo matches the condition
	 * @param object pojo to have its fields inspected
	 * @return true if it does, otherwise false
	 */
	public boolean matches(Object object) {
    	Node<?> conditionRoot = parsingResult.parseTreeRoot;
    	return PojoConditionParser.evaluateCondition(object, parsingResult.inputBuffer, conditionRoot.getChildren().get(0));
	}
	
	/**
	 * Filters an existing iterator based on whether its elements
	 * match the condition or not
	 * @param matching true if matching elements should included, or false if nonmatching elements
	 */
	public <T> Iterator<T> filter(Iterator<T> iterator, boolean matching) {
		return new ConditionalIterator<T>(iterator, matching);
	}
	
	/**
	 * Filters an existing iterable based on whether its elements
	 * match the condition or not
	 * @param matching true if matching elements should included, or false if nonmatching elements
	 */
	public <T> Iterable<T> filter(Iterable<T> iterable, boolean matching) {
		return new ConditionalIterable<T>(iterable, matching);
	}
	
	/**
	 * Wraps an iterable so that the returned iterator filters its elements
	 * based on whether they match the condition or not
	 */
	private class ConditionalIterable<T> implements Iterable<T> {
		private final Iterable<T> wrappedIterable;
		private final boolean matching;
		
		public ConditionalIterable(Iterable<T> wrappedIterable, boolean matching) {
			this.wrappedIterable = wrappedIterable;
			this.matching = matching;
		}
		
		public Iterator<T> iterator() {
			return new ConditionalIterator<T>(wrappedIterable.iterator(), matching);
		}
	}
	
	/**
	 * Wraps an existing iterator and filters the elements based on
	 * whether they match the condition or not
	 */
	private class ConditionalIterator<T> implements Iterator<T> {
		private final Iterator<T> wrappedIterator;
		private final boolean matching;
		
		private T nextObject = null;
		
		/**
		 * Wraps an iterator and filters returned objects based on
		 * whether they match the condition or not
		 * @param wrappedIterator the iterator to wrap
		 * @param matching if true, the iterator will only include objects from
		 *        the underlying iterator that the condition.  if false, the iterator
		 *        will only include objects which DO NOT match the condition
		 */
		public ConditionalIterator(Iterator<T> wrappedIterator, boolean matching) {
			this.wrappedIterator = wrappedIterator;
			this.matching = matching;
		}
		
		public boolean hasNext() {
			prime();
			
			return nextObject != null;
		}
		
		public T next() {
			if(!hasNext()) {
				throw new NoSuchElementException("no more elements");
			}
			
			T toReturn = nextObject;
			nextObject = null;
			return toReturn;
		}
		
		public void remove() {
			throw new UnsupportedOperationException("remove not supported");
		}
		
		/**
		 * Reads and discards entries from the underlying iterator until
		 * an object is found that either matches the filter or the end
		 * of the iterator has been reached
		 */
		private void prime() {
			if(nextObject == null) {
				while(wrappedIterator.hasNext()) {
					T nextWrappedObject = wrappedIterator.next();
					if(matches(nextWrappedObject) == matching) {
						nextObject = nextWrappedObject;
						break;
					}
				}
			}
		}
	}
}
