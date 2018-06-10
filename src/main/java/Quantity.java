/** Quantity
 *
 * A base dimension and a power.
 *
 * Ex: DISTANCE of power 2 represents AREA but allows AREA / DISTANCE = DISTANCE
 *
 */
public class Quantity {
	
	private Base base;
	private int power;
	
	public Quantity(Base base, int power) {
		// Power == 0 or NONE both mean the same thing
		if (power == 0 || base.equals(Base.NONE)) {
			this.base = Base.NONE;
			this.power = 0;
		} else {
			this.base = base;
			this.power = power;
		}
	}
	
	public Quantity(Base base) {
		this(base, 1);
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
	        return false;
	    }
	    if (!BU.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
	    Quantity q = (Quantity) obj;
	    
	    if (base == q.base && power == q.power) return true;
	    return false;
	}

	public Base getBase() {
		return base;
	}

	public int getPower() {
		return power;
	}

	@Override
	public String toString(){
		return base + " ^ " + power;
	}

}
