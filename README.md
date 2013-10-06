Filters
=========
A library that includes functionality for parsing simple conditions of the form: 

        fieldName>value1 and fieldName2<value2 or _fieldName3=value3
    
Once parsed the library then allows application of the condition to arbitary Java objects.  For example, the _matches(Object object)_ method will pull out the fields specified in the condition from the object, and use them to evaluate the condition.

For a more concrete example, consider an object representing a point on a grid:

        public class XY {
        	private int x;
    		private int y;
    		
    		public XY(int x, int y) {
    			this.x = x;
    			this.y = y;
    		}
        }
    
Someone may want to build a condition that determines if points are in a certain location, such as x>0 and y>0.  Using this libary, you could accomplish this in the following way:

    	// build a condition that pulls out the fields x and y from the object
		PojoEvaluator evaluator = PojoEvaluator.forCondition("x > 0 and y > 0");
		
		// both the x and y fields match the condition
		boolean matches = evaluator.matches(new XY(2, 2)); // true
		
		// the value of the x field will cause the condition to be false
		matches = evaluator.matches(new XY(-1, 2)); // false

And in some cases, you may have a collection of objects that you want to evaluate, which can be done as follows:

    	// create a list of points
		List<XY> points = new ArrayList<XY>();
		points.add(new XY(2, 2));
		points.add(new XY(-2, -2));
		points.add(new XY(3, 5));
		points.add(new XY(4, -8));
		
		// the evaluator supports filtering the list, so in the following
		// loop, we will only iterate over objects that match the condition
		for(XY matchingPoint : evaluator.filter(points)) {
		    // ...
		}
		
		// alternatively, you can iterate over entries NOT matching the condition
		for(XY nonmatchingPoint : evaluator.filter(points, false)) {
			// ...
		}
		
		// the above filtering techniques also work when you 
        // have an iterator instead of something implementing iterable
		Iterator<XY> matchingIterator = evaluator.filter(points.iterator());
		Iterator<XY> nonmatchingIterator = evaluator.filter(points.iterator(), false);


Conditions
----

The most basic condition supported is a comparison of the form

        fieldName OPERATOR fieldValue

where supported operators include: >, <, >=, <= and =.  The fieldName must match the name of a field in the object, and the type of the field must be either String, Byte, Short, Integer, Long, Float, Double (or any of the primitive types, e.g. int).

The fieldValue should either be numeric (3, 3.3), or a String enclosed in single quotes ('some string').  Single quotes in the string value should be escaped with a single quote ('he won''t go').  

Examples:

        size < 3
        title='john''s life'
        average>=3.3
        animal<='Aardvark'
        duration>10

How comparison operators execute depend on the type of field found in the object.  If it is a numeric field, the comparison will be done numerically regardless of whether the value in the comparison is specified as 27 or '27'.  Similarly, if the field is of type String, it will be compared alphabetically regardless of whether the value is specified as 27 or '27'.

Complex Conditions
----

Simple conditions can be combined using AND and OR.  Nesting is not allowed, so conditions must be specified without the use of ( and ) to indicate precedence.  The AND operator will be executed first, followed by OR.  AND and OR are NOT case sensitive.

Examples:

        temperature > 90 and temperature < 100
        light='red' or light='yellow'
        type='penny' and year<1996 or type='nickel' and year<1974
        
Conditions with Methods
----

Conditions may also contain no parameter methods, but this must be enabled by using PojoEvaluator.forCondition(condition, true), where the second parameter indicates that the condition parsing should allow methods.  With this enabled, the specified method will be called on the objects and the return value will be used to evaluate the condition.

Examples:

        getCount()>=10
        size()=1 or toString()='Special Value'
        

Ordering
--

In addition to the filtering support with conditions is the ability to sort arrays, lists and generate comparators based on an order by clause.

The clauses default to ordering fields in ascending order, and have the form:

        order by identifier [asc|desc] [, identifier2 [asc|desc] ...]
        
With the following code sorting our XY class from earlier:

        XY[] points = {new XY(0, 0), new XY(-1, -1), new XY(2, 0), new XY(2, -1)};
		
        // order the points according to their x field, from low to high
        // --> x value of sorted points will be -1, 0, 2, 2
        PojoOrdering<XY> xyOrdering = PojoOrdering.forOrderBy("order by x");
        xyOrdering.sort(points);
        
        // re-sort the points by y value, followed by x descending
        // x,y values of sorted points will be (2,-1), (-1,-1), (2,0), (0,0)
        xyOrdering = PojoOrdering.forOrderBy("order by y, x desc", true);
        xyOrdering.sort(points);
        
        
And similar to condition parsing, we can support no parameter methods:

		String[] data = {"this is long", "b", "a", "z"};
		
		// sort the strings by length, with strings of equal length sorted lexicographically,
		// resulting in: "a", "b", "z", "this is long"
		PojoOrdering<String> stringOrdering = PojoOrdering.forOrderBy("order by length(), toString()", true);
		stringOrdering.sort(data);
