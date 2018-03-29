/** Base Unit
 *
 * Most importantly defined by quantity and length.
 * Length is what relates this unit to other units of the same quantity.
 *
 * Ex: M defined as DISTANCE of length 1.
 * 	 Then KM defined as DISTANCE of length 1000.
 *
 * To allow defining units that have a quantity power greater than 1 without displaying it, BU's can have a defPower.
 * DefPower signifies the power of the base unit.
 *
 * Ex: CC (cubic centimeter) can be defined as DISTANCE of length 0.01 with defPower 3.
 * 	 This will mean that 1 cc = 1 cm^3.
 * 	 (If we used defPower 1, cc would be displayed as cc^3 and we would have 1 cc^3 = 1 cm^3.)
 */
public class BU { // Base Unit
	
	private final double length;
	
	private final Quantity quantity;
	private final int defPower; // Definition power. Ex: Litres is Distance^3 but does not itself have a power. Thus its defPower = 3.
	private final double offset; // The absolute offset. Only used for absolute conversion. All units of same quantity needs to offset to the same point.
	
	private final String shortName;
	private final String longName;
	
	public BU(double length, String shortName, String longName, Quantity quantity, int defPower, double offset) {
		this.length = length;
		this.quantity = quantity;
		this.defPower = defPower;
		this.offset = offset;
		
		this.shortName = shortName;
		this.longName = longName;
	}
	
	public BU(double length, String shortName, String longName, Quantity quantity, int defPower) {
		this(length, shortName, longName, quantity, defPower, 0);
	}
	
	public BU(double length, String shortName, String longName, Quantity quantity) {
		this(length, shortName, longName, quantity, 1);
	}
	
	private BU(BU a, int power, int defPower) {
		this(a.length, a.shortName, a.longName, new Quantity(a.quantity.getBase(), power), defPower);
	}
	
	public BU(BU a, int power) {
		this(a.length, a.shortName, a.longName, new Quantity(a.quantity.getBase(), power), a.defPower);
	}
	
	public BU(U u) {
		this(u.components.get(0), u.components.get(0).getPower(), u.components.get(0).defPower);
	}

	public boolean equals(Object obj) {
		if (obj == null) {
	        return false;
	    }
	    if (!BU.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
	    BU bu = (BU) obj;
	    
		if (this.isSameQuantity(bu) && getLength() == bu.getLength()) {
			return true;
		}
		return false;
	}
	
	public boolean isSameQuantity(BU as) {
		if (this.quantity.equals(as.quantity)) {
			return true;
		}
		return false;
	}
	
	public boolean isSameQuantityLength(BU as) {
		if (isSameQuantityBase(as) && this.length == as.length) {
			return true;
		}
		return false;
	}
	
	public boolean isSameQuantityBase(BU as) {
		if (this.quantity.getBase() == as.quantity.getBase()) {
			return true;
		}
		return false;
	}
	
	/** Perform repeated multiplication of this unit with itself.
	 *
	 * @param p exponent. If 0: result is NONE.
	 *          			 If 1: Result is itself.
	 *          			 If &gt;1: Power.
	 *          			 If &lt;0: Result is inverted and given power.
	 * @return the resulting unit. */
	public BU pow(int p) {
		int newPower = this.getPower() * p;
		if (newPower == 0) newPower = 1;
		return new BU(this, this.getPower() * p);
	}
	
	public BU mul(BU bu) throws UnitMismatchException{
		if (this.isSameQuantityLength(bu)) {
			BU result = new BU(this, this.quantity.getPower() + bu.quantity.getPower(), this.defPower); //TODO Not sure if defPower is correct
			return result;
		} else {
			throw new UnitMismatchException(String.format("Base unit multiplication error: %s * %s.", this.longName(), bu.longName()));
		}
	}
	
	public BU div(BU bu) throws UnitMismatchException{
		if (this.isSameQuantityLength(bu)) {
			return new BU(this, this.quantity.getPower() - bu.quantity.getPower(), this.defPower); //TODO Not sure if defPower is correct
		} else {
			throw new UnitMismatchException(String.format("Base unit division error: %s / %s.", this.longName(), bu.longName()));
		}
	}
	
	public BU inverse() {
		return new BU(this, -this.quantity.getPower(), this.defPower);
	}
	
	public double getLength() {
		return Math.pow(length, getPower() / getDefPower());
	}
	
	public int getPower() {
		return quantity.getPower();
	}
	
	public int getDefPower() {
		return defPower;
	}
	
	public double getOffset() {
		return offset;
	}
	
	public Base getQuantityBase() {
		return quantity.getBase();
	}
	
	public String shortName(boolean inverted) {
		int p = quantity.getPower();// - (defPower - 0);
		if (inverted) p = -p;
		int displayPower = 0;
		if (defPower != 0) displayPower = p/defPower;
		//System.out.println(shortName);
		return getPowerShortName(shortName, displayPower);
	}
	
	public String shortName() {
		return shortName(false);
	}
	
	public String longName(boolean inverted) {
		int p = quantity.getPower();// - (defPower - 0);
		if (inverted) p = -p;
		return getPowerLongName(longName, p/defPower);
	}
	
	public String longName() {
		return longName(false);
	}
	
	private static String getPowerLongName(String name, int power) {
		if (power == 0) {
			return String.format("%s", name);
		} else if (power == 1) {
			return String.format("%s", name);
		} else if (power == 2) {
			return String.format("square %s", name);
		} else if (power == 3) {
			return String.format("cubic %s", name);
		} else {
			return String.format("%s^%s", name, power);
		}
	}
	
	private static String getPowerShortName(String name, int power) {
		if (power == 0) {
			return String.format("%s", name);
		} else if (power == 1) {
			return String.format("%s", name);
		} else {
			return String.format("%s^%s", name, power);
		}
	}
	
	public String toString() {
		return shortName(false);
	}
	
	public String toString(boolean inverted) {
		return shortName(inverted);
	}
	
	public String debugString() {
		return String.format("[NAME: %s, POW: %s, LEN: %s, QUANT: %s]", shortName, getPower(), length, quantity);
	}

}
