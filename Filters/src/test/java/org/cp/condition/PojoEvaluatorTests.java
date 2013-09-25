package org.cp.condition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.Assert;

import org.cp.pojoconditions.FieldException;
import org.cp.pojoconditions.FieldTypeException;
import org.cp.pojoconditions.NonexistentFieldException;
import org.cp.pojoconditions.PojoEvaluator;
import org.junit.Test;


public class PojoEvaluatorTests {
	@Test
	public void testValidConditions() throws Exception {
		final String[] VALID_CONDITIONS = {
				"a=''",
				"a=''''",
				"a='christopher''s test'",
				"a<3",
				"a<3.0",
				"a<'3'",
				"a<2423482384",
				"a<'this is some text'",
				"a<'~!@#$%^&*()_+{}|:\"<>?[]\\;'',./€ƒ‡ŒŽñᠡ'",
				"someField123<345",
				"cat < '123' and dog<34 and monkey>3 or appleCount<52 and name='chris' and day=2 or m<=25 and y>=2013"
		};
		
		for(String validCondition : VALID_CONDITIONS) {
			try{ 
				PojoEvaluator.forCondition(validCondition);
			} catch (Exception e) {
				throw new Exception("Failed to create evaluator for condition: " + validCondition, e);
			}
		}
	}

	@Test
	public void testInvalidConditions() throws Exception {
		final String[] INVALID_CONDITIONS = {
				"'a'<'d'",
				"a<d",
				"<d",
				"a>",
				"123<123",
				"a<..3",
				"a<3.",
				"a<3.3.",
				"",
				"a<2 and and a<2",
				"a<2 and or a<2",
				"a < 2 and b>3 or",
				"a<\"dog\""
				
		};
		
		for(String invalidCondition : INVALID_CONDITIONS) {
			try{ 
				PojoEvaluator.forCondition(invalidCondition);
				Assert.fail("Expected failure for condition: " + invalidCondition);
			} catch (IllegalArgumentException e) {
				// expected
			}
		}
	}


	
	/**
	 * Tests out a pojo with just a single
	 * integer field <i>field</i>
	 */
	@Test
	public void testSingleFieldPojo() {
		PojoEvaluator evaluator = PojoEvaluator.forCondition("field=0");
		Assert.assertTrue(evaluator.matches(new IntPojo(0)));
		Assert.assertFalse(evaluator.matches(new IntPojo(-1)));
		Assert.assertFalse(evaluator.matches(new IntPojo(1)));
		
		evaluator = PojoEvaluator.forCondition("field>0");
		Assert.assertTrue(evaluator.matches(new IntPojo(1)));
		Assert.assertFalse(evaluator.matches(new IntPojo(0)));
		Assert.assertFalse(evaluator.matches(new IntPojo(-1)));
		
		evaluator = PojoEvaluator.forCondition("field<0");
		Assert.assertTrue(evaluator.matches(new IntPojo(-1)));
		Assert.assertFalse(evaluator.matches(new IntPojo(0)));
		Assert.assertFalse(evaluator.matches(new IntPojo(1)));
		
		evaluator = PojoEvaluator.forCondition("field>=0");
		Assert.assertTrue(evaluator.matches(new IntPojo(1)));
		Assert.assertTrue(evaluator.matches(new IntPojo(0)));
		Assert.assertFalse(evaluator.matches(new IntPojo(-1)));
		
		evaluator = PojoEvaluator.forCondition("field<=0");
		Assert.assertTrue(evaluator.matches(new IntPojo(-1)));
		Assert.assertTrue(evaluator.matches(new IntPojo(0)));
		Assert.assertFalse(evaluator.matches(new IntPojo(1)));
		
		evaluator = PojoEvaluator.forCondition("field>2 and field<5");
		Assert.assertTrue(evaluator.matches(new IntPojo(3)));
		Assert.assertFalse(evaluator.matches(new IntPojo(2)));
		Assert.assertFalse(evaluator.matches(new IntPojo(5)));
		
		evaluator = PojoEvaluator.forCondition("field<2 or field>5");
		Assert.assertTrue(evaluator.matches(new IntPojo(1)));
		Assert.assertTrue(evaluator.matches(new IntPojo(6)));
		Assert.assertFalse(evaluator.matches(new IntPojo(2)));
		Assert.assertFalse(evaluator.matches(new IntPojo(5)));
		
		evaluator = PojoEvaluator.forCondition("field<2 or field>5 and field<7");
		Assert.assertTrue(evaluator.matches(new IntPojo(1)));
		Assert.assertTrue(evaluator.matches(new IntPojo(6)));
		Assert.assertFalse(evaluator.matches(new IntPojo(3)));
		Assert.assertFalse(evaluator.matches(new IntPojo(5)));
		Assert.assertFalse(evaluator.matches(new IntPojo(7)));
		
		evaluator = PojoEvaluator.forCondition("field>5 and field<7 or field<2");
		Assert.assertTrue(evaluator.matches(new IntPojo(1)));
		Assert.assertTrue(evaluator.matches(new IntPojo(6)));
		Assert.assertFalse(evaluator.matches(new IntPojo(3)));
		Assert.assertFalse(evaluator.matches(new IntPojo(5)));
		Assert.assertFalse(evaluator.matches(new IntPojo(7)));
		
		evaluator = PojoEvaluator.forCondition("field<3 and field>3");
		Assert.assertFalse(evaluator.matches(new IntPojo(2)));
		Assert.assertFalse(evaluator.matches(new IntPojo(3)));
		Assert.assertFalse(evaluator.matches(new IntPojo(4)));
	}
	
	@Test
	public void testStringType() {
		PojoEvaluator evaluator = PojoEvaluator.forCondition("stringField='cat'");
		Assert.assertTrue(evaluator.matches(new AllTypePojo("cat", null, null, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo("this is not cat", null, null, null, null, null, null)));

		evaluator = PojoEvaluator.forCondition("stringField>'cat'");
		Assert.assertTrue(evaluator.matches(new AllTypePojo("dog", null, null, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo("cat", null, null, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo("bear", null, null, null, null, null, null)));
		
		evaluator = PojoEvaluator.forCondition("stringField>='cat'");
		Assert.assertTrue(evaluator.matches(new AllTypePojo("dog", null, null, null, null, null, null)));
		Assert.assertTrue(evaluator.matches(new AllTypePojo("cat", null, null, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo("bear", null, null, null, null, null, null)));
		
		evaluator = PojoEvaluator.forCondition("stringField<'cat'");
		Assert.assertTrue(evaluator.matches(new AllTypePojo("bear", null, null, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo("cat", null, null, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo("zebra", null, null, null, null, null, null)));
		
		evaluator = PojoEvaluator.forCondition("stringField<='cat'");
		Assert.assertTrue(evaluator.matches(new AllTypePojo("bear", null, null, null, null, null, null)));
		Assert.assertTrue(evaluator.matches(new AllTypePojo("cat", null, null, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo("zebra", null, null, null, null, null, null)));
	}
	
	@Test
	public void testByteType() {
		PojoEvaluator evaluator = PojoEvaluator.forCondition("byteField=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, (byte)125, null, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, (byte)12, null, null, null, null, null)));

		evaluator = PojoEvaluator.forCondition("byteField>125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, (byte)126, null, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, (byte)125, null, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, (byte)124, null, null, null, null, null)));
		
		evaluator = PojoEvaluator.forCondition("byteField>=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, (byte)126, null, null, null, null, null)));
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, (byte)125, null, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, (byte)124, null, null, null, null, null)));
		
		evaluator = PojoEvaluator.forCondition("byteField<125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, (byte)124, null, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, (byte)125, null, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, (byte)126, null, null, null, null, null)));
		
		evaluator = PojoEvaluator.forCondition("byteField<=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, (byte)124, null, null, null, null, null)));
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, (byte)125, null, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, (byte)126, null, null, null, null, null)));
	}
	
	@Test
	public void testShortType() {
		PojoEvaluator evaluator = PojoEvaluator.forCondition("shortField=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, (short)125, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, (short)12, null, null, null, null)));

		evaluator = PojoEvaluator.forCondition("shortField>125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, (short)126, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, (short)125, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, (short)124, null, null, null, null)));
		
		evaluator = PojoEvaluator.forCondition("shortField>=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, (short)126, null, null, null, null)));
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, (short)125, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, (short)124, null, null, null, null)));
		
		evaluator = PojoEvaluator.forCondition("shortField<125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, (short)124, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, (short)125, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, (short)126, null, null, null, null)));
		
		evaluator = PojoEvaluator.forCondition("shortField<=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, (short)124, null, null, null, null)));
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, (short)125, null, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, (short)126, null, null, null, null)));
	}
	
	@Test
	public void testLongType() {
		PojoEvaluator evaluator = PojoEvaluator.forCondition("longField=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, 125L, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, 12L, null, null)));

		evaluator = PojoEvaluator.forCondition("longField>125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, 126L, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, 125L, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, 124L, null, null)));
		
		evaluator = PojoEvaluator.forCondition("longField>=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, 126L, null, null)));
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, 125L, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, 124L, null, null)));
		
		evaluator = PojoEvaluator.forCondition("longField<125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, 124L, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, 125L, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, 126L, null, null)));
		
		evaluator = PojoEvaluator.forCondition("longField<=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, 124L, null, null)));
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, 125L, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, 126L, null, null)));
	}
	
	@Test
	public void testFloatType() {
		PojoEvaluator evaluator = PojoEvaluator.forCondition("floatField=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, null, 125.0f, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, null, 12.0f, null)));

		evaluator = PojoEvaluator.forCondition("floatField>125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, null, 125.1f, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, null, 125.0f, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, null, 124.9f, null)));
		
		evaluator = PojoEvaluator.forCondition("floatField>=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, null, 125.1f, null)));
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, null, 125.0f, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, null, 124.9f, null)));
		
		evaluator = PojoEvaluator.forCondition("floatField<125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, null, 124.9f, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, null, 125.0f, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, null, 125.1f, null)));
		
		evaluator = PojoEvaluator.forCondition("floatField<=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, null, 124.9f, null)));
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, null, 125.0f, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, null, 125.1f, null)));
	}
	
	@Test
	public void testDoubleType() {
		PojoEvaluator evaluator = PojoEvaluator.forCondition("doubleField=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, null, null, 125.0)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, null, null, 12.0)));

		evaluator = PojoEvaluator.forCondition("doubleField>125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, null, null, 125.1)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, null, null, 125.0)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, null, null, 124.9)));
		
		evaluator = PojoEvaluator.forCondition("doubleField>=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, null, null, 125.1)));
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, null, null, 125.0)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, null, null, 124.9)));
		
		evaluator = PojoEvaluator.forCondition("doubleField<125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, null, null, 124.9)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, null, null, 125.0)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, null, null, 125.1)));
		
		evaluator = PojoEvaluator.forCondition("doubleField<=125");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, null, null, 124.9)));
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, null, null, null, null, 125.0)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, null, null, null, null, 125.1)));
	}
	
	@Test
	public void testComplexConditions() {
		PojoEvaluator evaluator = PojoEvaluator.forCondition("stringField='phone' and integerField<10 and integerField>=0");
		Assert.assertTrue(evaluator.matches(new AllTypePojo("phone", null, null, 5, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo("phone", null, null, 11, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo("phone", null, null, -1, null, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo("animal", null, null, 5, null, null, null)));
		
		evaluator = PojoEvaluator.forCondition("shortField>0 OR longField>0");
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, (short)5, null, -5L, null, null)));
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, (short)-5, null, 5L, null, null)));
		Assert.assertTrue(evaluator.matches(new AllTypePojo(null, null, (short)-5, null, 5L, null, null)));
		Assert.assertFalse(evaluator.matches(new AllTypePojo(null, null, (short)0, null, 0L, null, null)));
	}

	@Test
	public void testIteratorFilter() {
		PojoEvaluator evaluator = PojoEvaluator.forCondition("field>0");
		
		// check that we return false for hasEmpty for an empty wrapped iterator
		List<IntPojo> pojos = new ArrayList<IntPojo>();
		Assert.assertFalse(evaluator.filter(pojos.iterator(), true).hasNext());
		
		// we should still return false for hasNext when the wrapped iterator
		// doesn't have an matching elements, unless we want nonmatching pojos
		pojos.add(new IntPojo(-1));
		Assert.assertFalse(evaluator.filter(pojos.iterator(), true).hasNext());
		Assert.assertTrue(evaluator.filter(pojos.iterator(), false).hasNext());
		Assert.assertEquals(-1, evaluator.filter(pojos.iterator(), false).next().field);
		
		pojos.add(new IntPojo(1));
		Assert.assertTrue(evaluator.filter(pojos.iterator(), true).hasNext());
		Assert.assertEquals(1, evaluator.filter(pojos.iterator(), true).next().field);
		Assert.assertEquals(-1, evaluator.filter(pojos.iterator(), false).next().field);
		
		pojos.add(new IntPojo(10));
		pojos.add(new IntPojo(-10));
		pojos.add(new IntPojo(20));
		pojos.add(new IntPojo(-20));
		
		// get an iterator including only elements that meet the condition
		Iterator<IntPojo> matchingIterator = evaluator.filter(pojos.iterator(), true);
		Assert.assertTrue(matchingIterator.hasNext());
		Assert.assertEquals(1, matchingIterator.next().field);
		
		Assert.assertTrue(matchingIterator.hasNext());
		Assert.assertEquals(10, matchingIterator.next().field);
		
		Assert.assertTrue(matchingIterator.hasNext());
		Assert.assertEquals(20, matchingIterator.next().field);
		
		Assert.assertFalse(matchingIterator.hasNext());
		try {
			matchingIterator.next();
			Assert.fail("Should've thrown NoSuchElementException");
		} catch (NoSuchElementException e) {
			// expected
		}
		
		// get an iterator including only elements that do not meet the condition
		Iterator<IntPojo> nonmatchingIterator = evaluator.filter(pojos.iterator(), false);
		Assert.assertTrue(nonmatchingIterator.hasNext());
		Assert.assertEquals(-1, nonmatchingIterator.next().field);
		
		Assert.assertTrue(nonmatchingIterator.hasNext());
		Assert.assertEquals(-10, nonmatchingIterator.next().field);
		
		Assert.assertTrue(nonmatchingIterator.hasNext());
		Assert.assertEquals(-20, nonmatchingIterator.next().field);
		
		Assert.assertFalse(nonmatchingIterator.hasNext());
		try {
			matchingIterator.next();
			Assert.fail("Should've thrown NoSuchElementException");
		} catch (NoSuchElementException e) {
			// expected
		}
		
		// ensure the default for filter(iterator) is filter(iterator, true)
		matchingIterator = evaluator.filter(pojos.iterator());
		Assert.assertTrue(matchingIterator.hasNext());
		Assert.assertEquals(1, matchingIterator.next().field);
	}
	
	@Test
	public void testIterableFilter() {
		// an iterable list with both positive and negative values for the IntPojo
		List<IntPojo> pojos = new ArrayList<IntPojo>();
		pojos.add(new IntPojo(10));
		pojos.add(new IntPojo(20));
		pojos.add(new IntPojo(-20));
		pojos.add(new IntPojo(30));
		
		// try an iterable including only elements matching the condition
		PojoEvaluator evaluator = PojoEvaluator.forCondition("field>=0");
		List<IntPojo> encounteredPojos = new ArrayList<IntPojo>();
		for(IntPojo pojo : evaluator.filter(pojos, true)) {
			encounteredPojos.add(pojo);
		}
		Assert.assertEquals(3, encounteredPojos.size());
		Assert.assertEquals(10, encounteredPojos.remove(0).field);
		Assert.assertEquals(20, encounteredPojos.remove(0).field);
		Assert.assertEquals(30, encounteredPojos.remove(0).field);
		
		// try an iterable including only elements NOT matching the condition
		encounteredPojos = new ArrayList<IntPojo>();
		for(IntPojo pojo : evaluator.filter(pojos, false)) {
			encounteredPojos.add(pojo);
		}
		Assert.assertEquals(1, encounteredPojos.size());
		Assert.assertEquals(-20, encounteredPojos.remove(0).field);
		
		// just ensure that the default value for filter(iterable) is filter(iterable, true)
		encounteredPojos = new ArrayList<IntPojo>();
		for(IntPojo pojo : evaluator.filter(pojos)) {
			encounteredPojos.add(pojo);
		}
		Assert.assertEquals(3, encounteredPojos.size());
	}
	
	/**
	 * Only certain types of pojo field types are supported in conditions.
	 * Try a condition that matches against a fild with type java.util.Date,
	 * and verify that we get an exception
	 */
	@Test
	public void testInvalidFieldType() {
		DatePojo datePojo = new DatePojo(Calendar.getInstance().getTime());
		
		PojoEvaluator evaluator = PojoEvaluator.forCondition("field=''");
		
		List<FieldException> fieldExceptions = evaluator.getUnsupportedIdentifiers(datePojo.getClass());
		Assert.assertEquals(1, fieldExceptions.size());
		Assert.assertTrue(fieldExceptions.get(0) instanceof FieldTypeException);
		
		FieldTypeException fieldTypeException = (FieldTypeException)fieldExceptions.get(0);
		Assert.assertEquals("field", fieldTypeException.getFieldName());
		Assert.assertEquals(Date.class, fieldTypeException.getFieldType());
		Assert.assertEquals(DatePojo.class, fieldTypeException.getPojoClass());
		
		try{
			evaluator.matches(datePojo);
			Assert.fail("Expected an exception");
		} catch (FieldTypeException e) {
			Assert.assertEquals("field", e.getFieldName());
			Assert.assertEquals(Date.class, e.getFieldType());
			Assert.assertEquals(DatePojo.class, e.getPojoClass());
		}
	}
	
	@Test
	public void testNonexistentField() {
		IntPojo intPojo = new IntPojo(0);
		
		PojoEvaluator evaluator = PojoEvaluator.forCondition("madeUpField=''");

		List<FieldException> fieldExceptions = evaluator.getUnsupportedIdentifiers(intPojo.getClass());
		Assert.assertEquals(1, fieldExceptions.size());
		Assert.assertTrue(fieldExceptions.get(0) instanceof NonexistentFieldException);
		
		NonexistentFieldException NonexistentFieldException = (NonexistentFieldException)fieldExceptions.get(0);
		Assert.assertEquals("madeUpField", NonexistentFieldException.getFieldName());
		Assert.assertEquals(IntPojo.class, NonexistentFieldException.getPojoClass());
		
		try{
			evaluator.matches(intPojo);
			Assert.fail("Expected an exception");
		} catch (NonexistentFieldException e) {
			Assert.assertEquals("madeUpField", e.getFieldName());
			Assert.assertEquals(IntPojo.class, e.getPojoClass());
		}
	}
	
	private static class IntPojo {
		private final int field;
		public IntPojo(int field) {
			this.field = field;
		}
	}
	
	private static class AllTypePojo {
		private final String stringField;
		private final Byte byteField;
		private final Short shortField;
		private final Integer integerField;
		private final Long longField;
		private final Float floatField;
		private final Double doubleField;
		
		public AllTypePojo(String stringField, Byte byteField,
				Short shortField, Integer integerField, Long longField,
				Float floatField, Double doubleField) {
			this.stringField = stringField;
			this.byteField = byteField;
			this.shortField = shortField;
			this.integerField = integerField;
			this.longField = longField;
			this.floatField = floatField;
			this.doubleField = doubleField;
		}
	}
	
	private static class DatePojo {
		private final Date field;
		public DatePojo(Date date) {
			this.field = date;
		}
	}
}
