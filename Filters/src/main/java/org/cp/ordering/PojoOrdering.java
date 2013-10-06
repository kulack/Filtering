package org.cp.ordering;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cp.pojoconditions.FieldTypeException;
import org.cp.pojoconditions.IncompatibleFieldsException;
import org.cp.pojoconditions.NonexistentIdentifierException;

public class PojoOrdering<T> {
	private final List<IdentifierOrder> identifierOrders;
	
	public PojoOrdering(List<IdentifierOrder> identifierOrders) {
		this.identifierOrders = identifierOrders;
	}
	
	public static <T> PojoOrdering<T> forOrderBy(String orderBy) {
		return new PojoOrdering<T>(OrderByParser.parseOrderBy(orderBy));
	}
	
	public static <T> PojoOrdering<T> forOrderBy(String orderBy, boolean matchMethods) {
		return new PojoOrdering<T>(OrderByParser.parseOrderBy(orderBy, matchMethods));
	}
	
	public void sort(List<T> list) {
		Collections.sort(list, new ReflectiveComparator());
	}
	
	public Comparator<T> getComparator() {
		return new ReflectiveComparator();
	}
	
	private class ReflectiveComparator implements Comparator<T> {
		public Object getValueFromMethod(Object pojo, String identifier) {
			Method method = null;
			
			try {
				method = pojo.getClass().getDeclaredMethod(identifier);
				method.setAccessible(true);
				return method.invoke(pojo);
			} catch (NoSuchMethodException e) {
				throw new NonexistentIdentifierException(identifier, true, pojo.getClass());
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		
		public Object getValueFromField(Object pojo, String identifier) {
			Field field = null;
			
			try {
				field = pojo.getClass().getDeclaredField(identifier);
				field.setAccessible(true);
				return field.get(pojo);
			} catch (NoSuchFieldException e) {
				throw new NonexistentIdentifierException(identifier, false, pojo.getClass());
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		public int compare(T o1, T o2) {
			// go over each identifier in the list in order.  We'll grab the
			// value for each object, compare them, and use the first non-equal
			// identifier in the list for the value of our compareTo
			for(IdentifierOrder identifierOrder : identifierOrders) {
				String identifier = identifierOrder.getIdentifier();
				
				Object o1IdentifierValue = identifierOrder.isMethod() ? getValueFromMethod(o1, identifier) : getValueFromField(o1, identifier);
				Object o2IdentifierValue = identifierOrder.isMethod() ? getValueFromMethod(o2, identifier) : getValueFromField(o2, identifier);
				
				Comparable o1ComparableValue = null; 
				Comparable o2ComparableValue = null; 
				
				try {
					o1ComparableValue = (Comparable)o1IdentifierValue;
				} catch (ClassCastException e) {
					throw new FieldTypeException(identifier, o1IdentifierValue.getClass(), o1.getClass());
				}
				
				try {
					o2ComparableValue = (Comparable)o2IdentifierValue;
				} catch (ClassCastException e) {
					throw new FieldTypeException(identifier, o2IdentifierValue.getClass(), o2.getClass());
				}
				
				try {
					int compareTo = o1ComparableValue.compareTo(o2ComparableValue);
					if(compareTo < 0) {
						return identifierOrder.isAscending() ? -1 : 1;
					} else if(compareTo > 0) {
						return identifierOrder.isAscending() ? 1 : -1;
					}
				} catch (ClassCastException e) {
					throw new IncompatibleFieldsException(identifier, o1ComparableValue.getClass(), o1.getClass(), o2ComparableValue.getClass(), o2.getClass());
				}
			}
			
			return 0;
		}
	}
}
