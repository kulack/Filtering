package org.cp.ordering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.cp.pojoconditions.FieldTypeException;
import org.cp.pojoconditions.IncompatibleFieldsException;
import org.cp.pojoconditions.NonexistentIdentifierException;
import org.junit.Test;

public class PojoOrderingTests {
	@Test
	public void testSimpleComparator() {
		IntPojo pojo1 = new IntPojo(1);
		IntPojo pojo2 = new IntPojo(2);
		IntPojo anotherPojo2 = new IntPojo(2);
		
		// get and test a simple comparator where the values of field increase with order
		PojoOrdering<IntPojo> ordering = PojoOrdering.forOrderBy("order by field");
		Comparator<IntPojo> cmp = ordering.getComparator();
		
		Assert.assertEquals(-1, cmp.compare(pojo1, pojo2));
		Assert.assertEquals(0, cmp.compare(pojo2, anotherPojo2));
		Assert.assertEquals(1, cmp.compare(pojo2, pojo1));
	}
	
	@Test
	public void testSimpleComparatorDesc() {
		IntPojo pojo1 = new IntPojo(1);
		IntPojo pojo2 = new IntPojo(2);
		IntPojo anotherPojo2 = new IntPojo(2);
		
		// get and test a simple comparator where the values of field decrease with order
		PojoOrdering<IntPojo> reverseOrdering = PojoOrdering.forOrderBy("order by field desc");
		Comparator<IntPojo> reverseCmp = reverseOrdering.getComparator();
		
		Assert.assertEquals(-1, reverseCmp.compare(pojo2, pojo1));
		Assert.assertEquals(0, reverseCmp.compare(pojo2, anotherPojo2));
		Assert.assertEquals(1, reverseCmp.compare(pojo1, pojo2));
	}
	
	@Test
	public void testSimpleMethodComparator() {
		IntPojo pojo1 = new IntPojo(1);
		IntPojo pojo2 = new IntPojo(2);
		IntPojo anotherPojo2 = new IntPojo(2);
		
		// get and test a simple comparator where the values of field decrease with order
		PojoOrdering<IntPojo> ordering = PojoOrdering.forOrderBy("order by getField()");
		Comparator<IntPojo> cmp = ordering.getComparator();
		
		Assert.assertEquals(-1, cmp.compare(pojo1, pojo2));
		Assert.assertEquals(0, cmp.compare(pojo2, anotherPojo2));
		Assert.assertEquals(1, cmp.compare(pojo2, pojo1));
	}
	
	@Test
	public void testMethodMatchingDisabled() {
		// this should parse fine, since the default is to match methods
		PojoOrdering<IntPojo> ordering = PojoOrdering.forOrderBy("order by getField()");

		try {
			// but this should fail since method matching is disabled
			ordering = PojoOrdering.forOrderBy("order by getField()", false);
			Assert.fail("Expected an exception to be thrown");
		} catch (IllegalArgumentException e) {
			
		}
	}
	
	@Test
	public void testComplexComparator() {
		AnimalColor aardvarkBlue = new AnimalColor("Aardvark", "Blue");
		AnimalColor aardvarkRed = new AnimalColor("Aardvark", "Red");
		AnimalColor dogBlue = new AnimalColor("Dog", "Blue");
		AnimalColor dogRed = new AnimalColor("Dog", "Red");
		
		PojoOrdering<AnimalColor> acOrdering = PojoOrdering.forOrderBy("order by animal, color");
		Comparator<AnimalColor> acComparator = acOrdering.getComparator();
		
		Assert.assertEquals(-1, acComparator.compare(aardvarkBlue, aardvarkRed));
		Assert.assertEquals(-1, acComparator.compare(aardvarkRed, dogBlue));
		Assert.assertEquals(-1, acComparator.compare(dogBlue, dogRed));
		
		Assert.assertEquals(0, acComparator.compare(aardvarkRed, aardvarkRed));
		Assert.assertEquals(0, acComparator.compare(dogBlue, dogBlue));
		
		Assert.assertEquals(1, acComparator.compare(aardvarkRed, aardvarkBlue));
		Assert.assertEquals(1, acComparator.compare(dogBlue, aardvarkRed));
		Assert.assertEquals(1, acComparator.compare(dogRed, dogBlue));
	}
	
	@Test
	public void testComplexComparatorDesc() {
		AnimalColor aardvarkBlue = new AnimalColor("Aardvark", "Blue");
		AnimalColor aardvarkRed = new AnimalColor("Aardvark", "Red");
		AnimalColor dogBlue = new AnimalColor("Dog", "Blue");
		AnimalColor dogRed = new AnimalColor("Dog", "Red");
		
		PojoOrdering<AnimalColor> acOrdering = PojoOrdering.forOrderBy("order by animal desc, color desc");
		Comparator<AnimalColor> acComparator = acOrdering.getComparator();
		
		Assert.assertEquals(-1, acComparator.compare(aardvarkRed, aardvarkBlue));
		Assert.assertEquals(-1, acComparator.compare(dogBlue, aardvarkRed));
		Assert.assertEquals(-1, acComparator.compare(dogRed, dogBlue));
		
		Assert.assertEquals(0, acComparator.compare(aardvarkRed, aardvarkRed));
		Assert.assertEquals(0, acComparator.compare(dogBlue, dogBlue));
		
		Assert.assertEquals(1, acComparator.compare(aardvarkBlue, aardvarkRed));
		Assert.assertEquals(1, acComparator.compare(aardvarkRed, dogBlue));
		Assert.assertEquals(1, acComparator.compare(dogBlue, dogRed));
	}
	
	@Test
	public void testListSorting() {
		AnimalColor aardvarkBlue = new AnimalColor("Aardvark", "Blue");
		AnimalColor aardvarkRed = new AnimalColor("Aardvark", "Red");
		AnimalColor dogBlue = new AnimalColor("Dog", "Blue");
		AnimalColor dogRed = new AnimalColor("Dog", "Red");
		
		List<AnimalColor> animalColors = new ArrayList<AnimalColor>();
		animalColors.add(dogBlue);
		animalColors.add(dogRed);
		animalColors.add(aardvarkRed);
		animalColors.add(aardvarkBlue);
		
		PojoOrdering<AnimalColor> acOrdering = PojoOrdering.forOrderBy("order by color, animal");
		
		acOrdering.sort(animalColors);
		
		Assert.assertEquals(4, animalColors.size());
		Assert.assertEquals(aardvarkBlue, animalColors.get(0));
		Assert.assertEquals(dogBlue, animalColors.get(1));
		Assert.assertEquals(aardvarkRed, animalColors.get(2));
		Assert.assertEquals(dogRed, animalColors.get(3));
	}
	
	@Test
	public void testHeterogenousListSortingWithMethods() {
		List<String> listOfSize3 = Arrays.asList(new String[3]);
		List<Integer> listOfSize7 = Arrays.asList(new Integer[7]);
		List<Date> listOfSize2 = Arrays.asList(new Date[2]);
		
		List<List<?>> lists = new ArrayList<List<?>>();
		lists.add(listOfSize3);
		lists.add(listOfSize7);
		lists.add(listOfSize2);
		
		PojoOrdering<List<?>> sizeOrdering = PojoOrdering.forOrderBy("order by size()");
		sizeOrdering.sort(lists);
		
		Assert.assertEquals(2, lists.get(0).size());
		Assert.assertEquals(3, lists.get(1).size());
		Assert.assertEquals(7, lists.get(2).size());
	}
	
	@Test(expected=NonexistentIdentifierException.class)
	public void testNonexistentFieldInComparator() {
		AnimalColor ac = new AnimalColor("Aardvark", "Red");
		PojoOrdering<AnimalColor> acOrdering = PojoOrdering.forOrderBy("order by otherfield");
		
		Comparator<AnimalColor> acComparator =  acOrdering.getComparator();
		acComparator.compare(ac, ac);
	}
	
	@Test(expected=NonexistentIdentifierException.class)
	public void testNonexistentFieldInListSorting() {
		AnimalColor[] ac = {new AnimalColor("Aardvark", "Red"), new AnimalColor("Aardvark", "Blue")};
		PojoOrdering<AnimalColor> acOrdering = PojoOrdering.forOrderBy("order by otherfield");
		acOrdering.sort(Arrays.asList(ac));
	}
	
	@Test(expected=FieldTypeException.class)
	public void testUnsupportedFieldInComparator() {
		ObjectPojo oof = new ObjectPojo(new Object());
		PojoOrdering<ObjectPojo> acOrdering = PojoOrdering.forOrderBy("order by field");
		
		Comparator<ObjectPojo> acComparator =  acOrdering.getComparator();
		acComparator.compare(oof, oof);
	}
	
	@Test(expected=FieldTypeException.class)
	public void testUnsupportedFieldInListSorting() {
		ObjectPojo[] oof = {new ObjectPojo(new Object()), new ObjectPojo(new Object())};
		PojoOrdering<ObjectPojo> acOrdering = PojoOrdering.forOrderBy("order by field");
		acOrdering.sort(Arrays.asList(oof));
	}
	
	@Test
	public void testSortingHeterogenousListWithCompatibleFields() {
		List<Object> objects = new ArrayList<Object>();
		objects.add(new IntPojo(2));
		objects.add(new ObjectPojo(Integer.valueOf(1)));
		
		PojoOrdering<Object> acOrdering = PojoOrdering.forOrderBy("order by field");
		acOrdering.sort(objects);
		
		Assert.assertTrue(objects.get(0) instanceof ObjectPojo);
		Assert.assertTrue(objects.get(1) instanceof IntPojo);
	}
	
	@Test
	public void testSortingHeterogenousListWithIncompatibleFields() {
		List<Object> objects = new ArrayList<Object>();
		objects.add(new IntPojo(2));
		objects.add(new ObjectPojo("Cat"));
		
		PojoOrdering<Object> acOrdering = PojoOrdering.forOrderBy("order by field");
		
		try {
			acOrdering.sort(objects);
			Assert.fail("Should've thrown exception");
		} catch (IncompatibleFieldsException e) {
			Assert.assertEquals("field", e.getIdentifier());
			Assert.assertEquals(Integer.class, e.getFieldType1());
			Assert.assertEquals(IntPojo.class, e.getPojoClass1());
			Assert.assertEquals(String.class, e.getFieldType2());
			Assert.assertEquals(ObjectPojo.class, e.getPojoClass2());
		}
	}
	
	private static class IntPojo {
		private final int field;
		public IntPojo(int field) {
			this.field = field;
		}
		
		public int getField() {
			return field;
		}
	}
	
	private static class AnimalColor {
		private final String animal;
		private final String color;
		
		public AnimalColor(String animal, String color) {
			this.animal = animal;
			this.color = color;
		}
	}
	
	private static class ObjectPojo {
		private final Object field;
		
		public ObjectPojo(Object object) {
			this.field = object;
		}
	}
}
