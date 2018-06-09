import java.util.ArrayList;

/**
 * Unit Vector
 * <p>
 * A compound unit with a value. This value is not the same as unit length.
 * <p>
 * Unit vectors with different units can be multiplied and divided. The result will have correct unit and length.
 * Unit vector can easily be converted to any unit with the same total quantity.
 * <p>
 * Ex: 1 m / 2 seconds = 0.5 m/s = 1.8 km/h != 0.5 kg/s
 */
public class UV implements Comparable<UV> { // Unit Vector

	private double value; // The value of this vector
	private U unit;

	//This is the value that two UV's may differ (due to floating point errors) and still be considered equal.
	private static final double maxError = 0.0000000000000000000001;

	public UV(double value, U unit) {
		this.value = value;
		this.unit = unit;
	}

	public UV(UV uv) {
		this.value = uv.value;
		this.unit = uv.unit;
	}
	
	/**
	 * @param to The Unit to convert to.
	 * @return A UV with the same value as this one but expressed in a different unit.
	 *
	 * @throws UnitMismatchException If this unit can't be converted to the target unit. (If they don't share the same quantity.)
	 */
	public UV convert(U to) throws UnitMismatchException {
		if (!unit.isSameQuantity(to)) {
			throw new UnitMismatchException(String.format("%s can not be converted to %s because they are not the same quantity. The difference is %s.",
					  unit, to, unit.dimDiff(to)));
		}
		double a = unit.getLength();
		double b = to.getLength();
		System.out.printf("Converting %s with l=%e to %s with l=%e.\n", this, a, new UV((this.value * a) / b, to), b);
		return new UV((this.value * unit.getLength()) / to.getLength(), to);
	}
	
	/** Converts this unit accounting for offset. Use this for converting between scales, such as X Fahrenheit into Y Celsius.
	 * @param to
	 * @return
	 *
	 * @throws UnitMismatchException
	 */
	public UV convertAbsolute(U to) throws UnitMismatchException {
		UV result = this.convert(to);
		result = result.add(new UV(unit.getOffset(), unit)).sub(new UV(to.getOffset(), to));
		return result;
	}

	public UV add(UV uv) throws UnitMismatchException {
		UV result;
		if (unit.isSameQuantity(uv.unit)) {
			result = new UV(value + uv.convert(unit).value, unit);
		} else {
			throw new UnitMismatchException(String.format("Tried adding %s to %s.", uv.unit, unit));
		}
		return result;
	}

	public UV add(double value, U u) throws UnitMismatchException {
		return add(new UV(value, u));
	}

	public UV sub(UV uv) throws UnitMismatchException {
		return add(uv.neg());
	}

	public UV sub(double value, U u) throws UnitMismatchException {
		return sub(new UV(value, u));
	}

	public UV mul(UV uv) {
		UV result = uv;
		U resultUnit;

		try {
			if (unit.equals(U.NONE) && !uv.unit.equals(U.NONE)) { // This unit is NONE, result will have other unit.
				unit = uv.unit;
				resultUnit = uv.unit;
				result = new UV(value * uv.convert(unit).value, resultUnit);
			} else if (!unit.equals(U.NONE) && uv.unit.equals(U.NONE)) { // Other unit is NONE, result will have this unit.
				uv.unit = unit;
				resultUnit = unit;
				result = new UV(value * uv.convert(unit).value, resultUnit);
			} else {
				resultUnit = unit.mul(uv.unit).reduce(); // As a side-effect, resultUnit has been converted to composite units.
				U resultMe = resultUnit.div(uv.unit).reduce(); // this.unit but expressed in same composites as resultUnit
				U resultThem = resultUnit.div(unit).reduce(); // uv.unit but expressed in same composites as resultUnit
				result = new UV(this.convert(resultMe).value * uv.convert(resultThem).value, resultUnit);
				System.out.printf("Mul: %s * %s = %s. I am %s. They are %s. \n", this, uv, result, resultMe, resultThem);
				if (unit.isSameQuantity(uv.unit)) {
					result.convert(unit.pow(2));
				}
			}
		} catch (UnitMismatchException e) {
			System.err.format("[ERROR] %s and %s could not be multiplied and caused a UnitMismatchException.\n", this, uv);
			e.printStackTrace();
		}
		return result;
	}

	public UV mul(double value, U u) {
		return mul(new UV(value, u));
	}

	public UV div(UV uv) {
		return this.mul(uv.inverse());
	}

	public UV div(double value, U u) {
		return div(new UV(value, u));
	}
	
	public UV pow(double p) throws UnitMismatchException{
		//System.out.println("Powering.");
		System.out.println("Pow 1");
		U u = this.unit.pow(p);
		System.out.println("Pow 2");
		U root = u.pow(1.0/p);
		System.out.printf("Powering %s with length %e to %f. Unit will be %s with length %e and root is %s.\n", this, unit().getLength(), p, u, u.getLength(), root);
		//System.out.printf("%s should have length %e\n", u, this.mul(this).unit().getLength());
		UV result = null;
		try {
			result = new UV(Math.pow(this.convert(root).value, p), u);
		} catch (UnitMismatchException e){
			System.err.format("The power of %s could not be calculated. This means that the root of the powered unit could not be calculated. \n", this);
			e.printStackTrace();
		}
		System.out.println("Powered to " + result);
		return result;
	}
	
	/** Invert this unit but essentially retain its value (although the value is also inverted).
	 * Ex: inverse(2 m^2) = 1/2 m^-2
	 *
	 * @return A UV with inverted unit and with newValue = 1 / oldValue.
	 */
	public UV inverse() {
		return new UV(1.0 / value, unit.inverse());
	}

	/**
	 * Negate.
	 *
	 * @return A UV with the same unit vector but negative value compared to before.
	 */
	public UV neg() {
		return new UV(-value, unit);
	}

	/**
	 * Negate a percentage.
	 *
	 * @return A UV with the value 1 - this.value()
	 */
	public UV negPercent() {
		return new UV(1 - this.value(), this.unit());
	}


	/**
	 * Compares two compatible unit vectors and returns the largest of them.
	 *
	 * @param a first unit vector
	 * @param b second unit vector
	 * @return the greatest of two UVs.
	 * @throws UnitMismatchException if units are not of the same quantity.
	 */
	public static UV max(UV a, UV b) throws UnitMismatchException {
		if (!a.unit().isSameQuantity(b.unit())) {
			throw new UnitMismatchException(String.format("%s can not be compared to %s because they are not the same quantity.", a, b));
		}

		if (a.compareTo(b) >= 0) {
			return a;
		} else {
			return b;
		}
	}

	/**
	 * Compares two compatible unit vectors and returns the smallest of them.
	 *
	 * @param a first unit vector
	 * @param b second unit vector
	 * @return the smallest of two UVs.
	 * @throws UnitMismatchException if units are not of the same quantity.
	 */
	public static UV min(UV a, UV b) throws UnitMismatchException {
		if (!a.unit().isSameQuantity(b.unit())) {
			throw new UnitMismatchException(String.format("%s can not be compared to %s because they are not the same quantity.", a, b));
		}

		if (a.compareTo(b) <= 0) {
			return a;
		} else {
			return b;
		}
	}

	public double value() {
		return value;
	}

	public U unit() {
		return unit;
	}

	/**
	 * Sets this UV to be a copy of another UV. To be used with care.
	 *
	 * @param uv the unit vector to set this unit vector to
	 */
	public void set(UV uv) {
		this.value = uv.value();
		this.unit = uv.unit();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!UV.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		UV uv = (UV) obj;
		
		return compareTo(uv) == 0;
	}

	@Override
	public int compareTo(UV uv) {
		try {
			// Make sure uv and this are the same quantity and convert so that values can be compared later.
			uv = uv.convert(unit());
		} catch (UnitMismatchException e) {
			// CompareTo cannot cast exception. Have to be handled here.
			System.err.format("[ERROR] %s and %s could not be compared because they are not the same quantity.\n", unit(), uv.unit());
			e.printStackTrace();
			return 0;
		}
		System.out.printf("Comparing %s and %s. Values %.20f and %.20f. Lengths %e and %e.\n", this, uv, value(), uv.value(), unit().getLength(), uv.unit().getLength());
		System.out.println("CompareTo returning " + U.compareDouble(value(), uv.value()));
		return U.compareDouble(value(), uv.value());
	}

	/**
	 * Calculates and returns the unit most appropriate for displaying a given value.
	 *
	 * @param target target value of the numeric part of this unit.
	 * @return the most appropriate unit for displaying this value.
	 */
	public UV convertAuto(double target) {
		try {
			return convert(U.getBestUnit(this, target));
		} catch (UnitMismatchException e) {
			System.err.println("This error can never happen.");
			e.printStackTrace();
			return this;
		}
	}

	@Override
	public String toString() {
		double abs = Math.abs(value);
		if (abs >= 10000) return String.format("%g %s", value, unit);
		else if (abs >= 100) return String.format("%.0f %s", value, unit);
		else if (abs >= 10) return String.format("%.1f %s", value, unit);
		else if (abs >= 1) return String.format("%.2f %s", value, unit);
		else return String.format("%.2g %s", value, unit);
	}

}
