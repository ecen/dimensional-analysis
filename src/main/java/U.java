import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/** Compund Unit
 * 
 * Class and methods primarily for defining, calculating and converting between units of basic and compounded quantities.
 * 
 * Some additional functionality exists for doing absolute conversion between scales, as well as units such as months and years.
 * Those do however have limited functionality. This code should not be used to implement calendar systems. 
 */
public class U { // Compound Unit

	private double lengthFactor = 1; // Not 1 for defined units based on compound units but with different length.
	ArrayList<BU> components = new ArrayList<BU>(0);
	
	//Static list containing all created units. Used for parsing, lookup etc.
	//private static ArrayList<U> units = new ArrayList<U>();

	public static final U NONE = new U(1, "none", "none", new Quantity(Base.NONE));

	public static final U M = new U(1, "m", "meter", new Quantity(Base.DISTANCE));
	public static final U MM = new U(M, 1.0 / 1000, "mm", "millimeter");
	public static final U CM = new U(M, 1.0 / 100, "cm", "centimeter");
	public static final U DM = new U(M, 1.0 / 10, "dm", "decimeter");
	public static final U KM = new U(M, 1000, "km", "kilometer");
	public static final U INCH = new U(M, 1 / 39.3701, "in", "inch");
	public static final U FOOT = new U(INCH, 12, "ft", "foot");
	public static final U YARD = new U(FOOT, 3, "yd", "yard");
	public static final U MILE = new U(YARD, 1760, "mi", "mile");
	public static final U LIGHTYEAR = new U(M, 9460730472580800.0, "lightyear", "lightyear");

	public static final U L = new U(DM, 1, "L", "liter", 3);
	public static final U ML = new U(L, 0.001, "ml", "milliliter");
	public static final U TEASPOON = new U(ML, 4.92892159375, "tspn", "teaspoon");
	public static final U TABLESPOON = new U(TEASPOON, 3, "tbsp", "tablespoon");
	public static final U FLOZ = new U(TABLESPOON, 2, "fl oz", "fluid ounce");
	public static final U CUP = new U(FLOZ, 8, "cup", "cup");
	public static final U PINT = new U(CUP, 2, "pt", "pint");
	public static final U GALLON = new U(PINT, 8, "gal", "gallon");
	public static final U BARREL = new U(GALLON, 31.5, "barrel", "barrel");

	public static final U G = new U(1, "g", "gram", new Quantity(Base.MASS));
	public static final U KG = new U(G, 1000, "kg", "kilogram");
	public static final U TON = new U(KG, 1000, "tonne", "metric ton");
	public static final U OUNCE = new U(G, 28.349523125, "oz", "ounce");
	public static final U POUND = new U(OUNCE, 16, "lb", "pound");
	public static final U TON_UK = new U(POUND, 2240, "ton", "long ton");
	public static final U TON_US = new U(POUND, 2000, "ton", "short ton");

	public static final U S = new U(1, "s", "second", new Quantity(Base.TIME));
	public static final U MS = new U(S, 0.001, "ms", "millisecond");
	public static final U MIN = new U(S, 60, "min", "minute");
	public static final U H = new U(MIN, 60, "h", "hour");
	public static final U DAY = new U(H, 24, "day", "day");
	public static final U WEEK = new U(DAY, 7, "week", "week");
	public static final U YEAR = new U(DAY, 365.25, "year", "year");
	public static final U MONTH = new U(YEAR, 1.0 / 12, "month", "month");
	
	public static final U RADIAN = new U(1, "rad", "radian", new Quantity(Base.ROTATION));
	public static final U DEGREE = new U(RADIAN, 0.0174533,"deg", "degree");
	
	public static final U KELVIN = new U(1, "K", "kelvin", new Quantity(Base.TEMPERATURE));
	public static final U CELSIUS = new U(KELVIN, 1, 273.15, "°C", "celsius");
	public static final U FAHRENHEIT = new U(CELSIUS, 5.0 / 9.0, 459.67, "°F", "fahrenheit");
	
	public static final U N = new U(U.KG.mul(U.M).div(U.S.mul(U.S)));
	public static final U CC = new U(CM, 1, "cc", "cubic centimeter", 3);

	/** Empty constructor. */
	private U() {
	}

	/** Compound unit copy constructor.
	 *
	 * @param u the compound unit which to copy
	 */
	private U(U u) {
		this.components.addAll(u.components);
	}
	
	/** Source Base Unit Constructor. Constructs a new unit from one base unit.
	 * 
	 * @param bu The base unit to base new unit on.
	 * @param lengthFactor The number of old units that goes in one new unit. 
	 * @param offset The absolute offset. Only used for absolute conversion. All units of same quantity needs to offset to the same point.
	 * @param shortName abbreviation for the new unit
	 * @param longName full name of the new unit
	 * @param defPower The power of the new unit compared to the old unit. Ex: L = DM^3 so to define L we need defPower = 3. 
	 */
	private U(BU bu, double lengthFactor, double offset, String shortName, String longName, int defPower) {
		int power = bu.getPower() * defPower;
		BU newBu = new BU(bu.pow(defPower).getLength() * lengthFactor, shortName, longName,
				 new Quantity(bu.getQuantityBase(), power), power, offset);
		addComponent(newBu, components);
	}

	public U(U u, double lengthFactor, String shortName) {
		this.lengthFactor = lengthFactor;
		for (BU bu : u.components){
			addComponent(bu, components);
		}
	}
	
		/** Simple Base Unit Constructor. Constructs a compound unit representation of given base unit.
		 *
		 * @param bu base unit for this unit
		 */
		public U(BU bu) {
			//Replace here to handle special case for NONE
			this(bu, 1, 0, bu.shortName(), bu.longName(), bu.getDefPower());
		}
		
			/** Simple BUC. No Base Unit or offset.
			 *
			 * Constructs a compound unit given the definition of a base unit.
			 *
			 * @param length length of this unit
			 * @param shortName abbreviation of this unit
			 * @param longName full name of this unit
			 * @param quantity the quantity this unit represents
			 * @param defPower the power of quantity this unit has. Ex: CC would have 3, because it is volume cubed.
			 */
			public U(double length, String shortName, String longName, Quantity quantity, int defPower) {
				this(new BU(length, shortName, longName, quantity, defPower));
			}
			
				/** Simple BUC. No Base Unit, defPower or offset.
				 *
				 * @param length length of this unit
				 * @param shortName abbreviation of this unit
				 * @param longName full name of this unit
				 * @param quantity the quantity this unit represents
				 */
				 public U(double length, String shortName, String longName, Quantity quantity) {
					this(length, shortName, longName, quantity, 1);
				}
	
	/** Base Unit Constructor, from existing U.
	 *
	 * Constructs a new compound unit from the first base unit of a given compound unit, with modified values.
	 * 
	 * @param u The old unit to base new unit on. This unit may only have one quantity.
	 * @param lengthFactor The number of old units that goes in one new unit. 
	 * @param offset The absolute offset. Only used for absolute conversion. All units of same quantity needs to offset to the same point.
	 * @param shortName abbreviation of this unit
	 * @param longName full name of this unit
	 * @param defPower The power of the new unit compared to the old unit. Ex: L = DM^3 so to define L we need defPower = 3. 
	 */
	public U(U u, double lengthFactor, double offset, String shortName, String longName, int defPower) {
		this(u.components.get(0), lengthFactor, offset, shortName, longName, defPower);
	}

		/** UC. No offset.
		 *
		 * @param u The old unit to base new unit on. This unit may only have one quantity.
		 * @param lengthFactor The number of old units that goes in one new unit.
		 * @param shortName abbreviation of this unit
		 * @param longName full name of this unit
		 * @param defPower The power of the new unit compared to the old unit. Ex: L = DM^3 so to define L we need defPower = 3.
		 */
		public U(U u, double lengthFactor, String shortName, String longName, int defPower) {
			this(u, lengthFactor, 0, shortName, longName, defPower);
		}

			/** UC. No defPower.
			 *
			 * @param u The old unit to base new unit on. This unit may only have one quantity.
			 * @param lengthFactor The number of old units that goes in one new unit.
			 * @param offset The absolute offset. Only used for absolute conversion. All units of same quantity needs to offset to the same point.
			 * @param shortName abbreviation of this unit
			 * @param longName full name of this unit
			 */
			public U(U u, double lengthFactor, double offset, String shortName, String longName) {
				this(u, lengthFactor, offset, shortName, longName, 1);
			}

				/** UC. No offset or defPower.
				 *
				 * @param u The old unit to base new unit on. This unit may only have one quantity.
				 * @param lengthFactor The number of old units that goes in one new unit.
				 * @param shortName abbreviation of this unit
				 * @param longName full name of this unit
				 */
				public U(U u, double lengthFactor, String shortName, String longName) {
					this(u, lengthFactor, 0, shortName, longName, 1);
				}
				
	/** Multiply this unit with another unit.
	 * 
	 * @param a the unit to multiply with.
	 * @return The resulting (compound) unit.
	 */
	public U mul(U a) {
		U u = new U();
		U.addCompound(this, u);
		U.addCompound(a, u);
		return u;
	}

	/** Divide this unit with another unit.
	 * 
	 * @param a the unit to divide with.
	 * @return The resulting (compound) unit.
	 */
	public U div(U a) {
		U u = new U();
		U.addCompound(this, u);
		U.addCompound(a.inverse(), u);
		return u;
	}

	/** Perform repeated multiplication of this unit with itself.
	 * 
	 * @param p The exponent. 0: result is NONE. 1: Result is itself. &gt;1: Power. &lt;0: Result is inverted and given power.
	 * @return The resulting unit.
	 */
	public U pow(int p) {
		U u = new U();
		if (p > 0) {
			u = this;
			for (int i = 1; i < p; i++) {
				u = u.mul(this);
			}
		} else if (p < 0) {
			u = this.div(this.mul(this));
			for (int i = -1; i > p; i--) {
				u = u.div(this);
			}
		}
		return u;
	}

	/** Equivalent to pow(-1)
	 * 
	 * @return The resulting inverted unit.
	 */
	public U inverse() {
		U u = new U();
		for (BU b : components) {
			U.addComponent(b.inverse(), u.components);
		}
		return u;
	}
	
	/** Checks the unit for multiple instances of the same quantity and combines them.
	 * 
	 * @return the reduced compound unit.
	 */
	public U reduce() {
		int qs[] = new int[Base.values().length];
		for (BU bu : components) {
			qs[bu.getQuantityBase().ordinal()] += bu.getPower();
		}
		
		U result = new U();
		for (BU bu : components) {
			if (qs[bu.getQuantityBase().ordinal()] != 0) {
				U.addComponent(new BU(bu, qs[bu.getQuantityBase().ordinal()]), result.components);
				qs[bu.getQuantityBase().ordinal()] = 0;
			}
		}
		return result;
	}

	/** Adds a Basic Unit component to this compound unit. This is basically multiplying with one BU.
	 * 
	 * @param a the basic unit to multiply into compound unit.
	 * @param components the component list for the compound unit.
	 */
	private static void addComponent(BU a, ArrayList<BU> components) {
		if (a.getPower() == 0) return; // Do not add NONE
		if (components.size() == 0) {
			components.add(a);
			return;
		}
		boolean added = false;
		for (int i = 0; i < components.size(); i++) {
			BU c = components.get(i);
			if (a.isSameQuantityLength(c)) { // If same quantity and length, add their powers
				try { // We've already checked isSameQuantityLength so this cannot fail.
					components.set(i, a.mul(c));
				} catch (UnitMismatchException e) {
					e.printStackTrace();
				}
				added = true;
				break;
			}
		}
		if (!added) { // If component is not same quantityLength as any other, just add it
			components.add(a);
			added = true;
		}

		for (Iterator<BU> iterator = components.iterator(); iterator.hasNext();) {
			BU c = iterator.next();
			if (c.getPower() == 0) iterator.remove(); // Remove any NONE elements
		}
	}

	/** Adds an entire compound unit to this compound unit. This is basically multiplying.
	 * 
	 * @param from the unit to pull basic units from
	 * @param to the unit to add basic units to
	 */
	private static void addCompound(U from, U to) {
		for (BU c : from.components) {
			addComponent(c, to.components);
		}
	}

	/** @param b the target unit
	 * @return the unit that you should multiply with to get the other unit.
	 **/
	public U dimDiff(U b) {
		return U.dimDiff(this, b);
	}

	/** Dimension Difference
	 * 
	 * @param a source unit
	 * @param b target unit
	 * @return The unit that you should multiply a with to get b.
	 **/
	private static U dimDiff(U a, U b) {
		// System.out.format("A: %s, B: %s \n", a, b);
		U diff = new U();
		for (BU c : a.components) {
			if (!b.hasComponent(c)) U.addComponent(c.inverse(), diff.components);
		}

		for (BU c : b.components) {
			if (!a.hasComponent(c)) U.addComponent(c, diff.components);
		}

		return diff;
	}

	/** Checks whether or not this unit and unit b has the same total quantity. (Basically if they could be added.)
	 * 
	 * @param b the unit to check against
	 * @return True iff the units have the same quantity.
	 */
	public boolean isSameQuantity(U b) {
		U diff = this.dimDiff(b);
		if (diff.reduce().components.size() == 0) return true;
		return false;
	}

	private boolean hasComponent(BU u) {
		if (components.contains(u)) return true;
		return false;
	}

	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (!U.class.isAssignableFrom(obj.getClass())) { return false; }
		U u = (U) obj;

		if (this.dimDiff(u).components.size() == 0) return true;
		return false;
	}

	public double getLength() {
		double len = 1;
		for (BU u : components) {
			len *= u.getLength();
		}
		return len * lengthFactor;
	}
	
	public double getOffset() {
		double offset = 0;
		for (BU u : components) {
			offset += u.getOffset(); // Offset for compound units is not yet really defined. 
		}
		return offset;
	}
	
	/** Calculates and returns the most suitable unit to display a given UV in.
	 *
	 * @param uv the source unit vector you want the best unit for
	 * @param target the target value of the numeric part.
	 * @return the most suitable unit to display UV in.
	 */
	public static U getBestUnit(UV uv, double target) {
		ArrayList<U> si = new ArrayList<U>(Arrays.asList(U.MM, U.CM, U.M,
				U.MM.pow(2), U.CM.pow(2), U.DM.pow(2), U.M.pow(2),
				U.ML, U.CC, U.L, U.M.pow(3),
				U.MS, U.S, U.MIN, U.H, U.DAY, U.WEEK, U.MONTH, U.YEAR,
				U.G, U.KG, U.TON,
				
				U.L.div(U.S), U.L.div(U.H), U.L.div(U.DAY),
				U.ML.div(U.S), U.ML.div(U.H), U.ML.div(U.DAY)));
		U reduced = uv.unit().reduce();
		ArrayList<U> correctQuantity = new ArrayList<U>(0); //List with all units of correct quantity
		for (U u : si) {
			if (u.isSameQuantity(reduced)) correctQuantity.add(u);
		}
		//Search the list of correct quantities for the unit which makes the value as close to 1 as possible
		U closestLengthUnit = uv.unit(); //Default unit is the one we have to begin with
		double closestLength = Double.MAX_VALUE;
		try {
			for (U u : correctQuantity) {
				//The logarithm gives a smaller value the closer to 1 we are.
				//We can divide the value in the abs to increase the number we want to get close to. 
				//Dividing by 10 makes it so we're finding the closest to 10
				double distance = Math.abs(Math.log(Math.abs(uv.convert(u).value()/target)));
				if (distance < closestLength) {
					closestLengthUnit = u;
					closestLength = distance; 
				}
			}
		} catch (UnitMismatchException e) {
			System.err.println("An appropriate unit could not be found. This error should never happen.");
			e.printStackTrace();
		}
		return closestLengthUnit;
	}

	public String toString() {
		// Sort components
		ArrayList<BU> mults = new ArrayList<BU>(0);
		ArrayList<BU> divs = new ArrayList<BU>(0);
		for (BU bu : components) {
			if (bu.getPower() > 0) mults.add(bu);
			if (bu.getPower() < 0) divs.add(bu);
		}
		
		// Write numerators
		String s = "";
		if (mults.size() > 1) s = s.concat("(");
		for (int i = 0; i < mults.size(); i++) {
			BU bu = mults.get(i);
			s = s.concat(bu.shortName());
			if (i + 1 != mults.size()) {
				s = s.concat("*");
			} else if (mults.size() > 1) {
				s = s.concat(")");
			}
		}
		// Write denominators
		if (divs.size() > 0 && mults.size() > 0) { // If there are numerators
			s = s.concat("/");
			if (divs.size() > 1) s = s.concat("(");
			for (int i = 0; i < divs.size(); i++) {
				BU bu = divs.get(i);
				s = s.concat(bu.shortName(true));
				if (i + 1 != divs.size()) {
					s = s.concat(" * ");
				} else if (divs.size() > 1) {
					s = s.concat(")");
				}
			}
		} else if (divs.size() > 0) { // If there are no numerators
			for (int i = 0; i < divs.size(); i++) {
				BU bu = divs.get(i);
				s = s.concat(bu.shortName(false));
				if (i + 1 != divs.size()) {
					s = s.concat(" * ");
				}
			}
		}

		return s;
	}
	
	public String debugString() {
		String s = "{";
		for (BU bu : components) {
			s += String.format("[n:%s,  p:%s, l: %s, q: %s] ", bu.shortName(), bu.getPower(), bu.getLength(), bu.getQuantityBase());
		}
		s += "}";
		return s;
	}

}
