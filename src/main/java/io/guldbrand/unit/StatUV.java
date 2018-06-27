package io.guldbrand.unit;

class StatUV { // Package private until done. Should maybe be a different project.

	private UV unitValue;
	
	private UV min; // Current min
    private UV max; // Current max
    private UV minimum; // Absolute min
    private UV maximum; // Absolute max
    
    /**
     * 
     * @param value The initial value of this stat.
     * @param unit The unit of this stat. This is only important to specify dimension and to specify what units the constructor will use now.
     * @param min The initial min value. This is intended to be able to be changed.
     * @param max The initial max value. This is intended to be able to be changed.
     * @param minimum The absolute minimal value. This is a hard limit that should not be possible to change.
     * @param maximum The absolute maximal value. This is a hard limit that should not be possible to change.
     */
    public StatUV(double value, U unit, double min, double max, double minimum, double maximum) {
		this.unitValue = new UV(value, unit);
		this.min = new UV(min, unit);
		this.max = new UV(max, unit);
		this.minimum = new UV(minimum, unit);
		this.maximum = new UV(maximum, unit);
	}
    
    public StatUV(double value, U unit, double min, double max, double minimum) {
		this(value, unit, min, max, minimum, Double.MAX_VALUE / Math.max(unit.getLength(), 1));
	}
    
    public StatUV(double value, U unit, double min, double max) {
		this(value, unit, min, max, Double.MIN_VALUE / Math.max(unit.getLength(), 1));
	}
    
    public StatUV(double value, U unit, double min) {
		this(value, unit, min, Double.MAX_VALUE / Math.max(unit.getLength(), 1));
	}
    
	public StatUV(double value, U unit) {
		this(value, unit, Double.MIN_VALUE / Math.max(unit.getLength(), 1));
	}
	
	/** Add to any UV with custom min, max, minimum and maximum.
	 * 
	 * @param addition That which you want to add.
	 * @param uv The container you're adding to.
	 * @param min The containers minimum.
	 * @param max The containers maximum.
	 * @return A new UV representing the new value of uv.
	 * @throws UnitMismatchException
	 */
	private static UV add(UV addition, UV uv, UV min, UV max) throws UnitMismatchException {
		UV term = new UV(addition);
		UV newValue = uv.add(term); //The UV we would have if we added without capping 
		
		if (newValue.compareTo(max) <= 0 && newValue.compareTo(min) >= 0) { // New value is within bounds
			uv.set(uv.add(term));
			return term;
		} else if (newValue.compareTo(max) > 0) { // New value is greater than max
			term = max.sub(uv);
			uv.set(max);
			return term;
		} else if (newValue.compareTo(min) < 0) { // New value is less than min
			term = max.sub(uv);
			uv.set(min);
			return term;
		}
		throw new UnitMismatchException("This should never happen.");
	}
	
	/** Adds to the value of this stat.
	 * 
	 * @param value numerical value of unit vector
	 * @param unit unit U of unit vector
	 * @return The amount that was actually added. This will differ from the given value only if the max is reached.
	 * @throws UnitMismatchException if units does not match
	 */
	public UV add(double value, U unit) throws UnitMismatchException {
		return StatUV.add(new UV(value, unit), unitValue, min, max);
	}
	
	public UV add(UV uv) throws UnitMismatchException {
		return add(uv.value(), uv.unit());
	}
	
	public UV sub(UV uv) throws UnitMismatchException {
		return add(-uv.value(), uv.unit());
	}
	
	public UV sub(double value, U unit) throws UnitMismatchException {
		return add(-value, unit);
	}
	
	public U getUnit() {
		return unitValue.unit();
	}
	
	public void setUV(UV uv) throws UnitMismatchException {
		unitValue = add(uv.sub(unitValue));
	}
	
	public UV getUV() {
		return new UV(unitValue);
	}
	
	public void addMax(UV addition) throws UnitMismatchException {
		System.out.format("2. Add: %s, To: %s, Min: %s, Max: %s\n", addition, this.max, this.min, this.maximum);
		StatUV.add(addition, this.max, this.min, this.maximum);
	}
	
	public void setMax(UV max) throws UnitMismatchException {
		this.max.set(max);
	}
	
	public void addMin(UV addition) throws UnitMismatchException {
		StatUV.add(addition, min, this.minimum, this.max);
	}
	
	public void setMin(UV min) throws UnitMismatchException {
		this.min.set(min);
	}
	
	public String toString() {
		return unitValue.toString();
	}

}
