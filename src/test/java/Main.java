import io.github.ecen.unit.U;
import io.github.ecen.unit.UV;
import io.github.ecen.unit.UnitMismatchException;

public class Main {

	public static void main(String args[]) throws UnitMismatchException {
		//UV a = new UV(3, U.M);
		//UV b = new UV(2, U.S);

		//UV c = new UV(10, U.KM.div(U.H));

		U carl = new U(U.M.div(U.KG), 0.025, "carl", "carl");
		//U carl = new U(U.M, 1.93, "carl", "carl");
		//System.out.println(new UV(1, carl).convert(U.M.div(U.KG)));
		//System.out.println(new UV(1, U.M.div(U.KG)).convert(carl));
		
		//System.out.println(new UV(3, U.M).mul(50, U.CM).convert(U.M.mul(U.CM)));
		//System.out.println(new UV(2, carl));
		//System.out.println(new UV(2, carl).convert(U.M.div(U.KG)));
		//System.out.println(new UV(2, carl).mul(new UV(2, carl)).convert(U.M.mul(U.M).div(U.KG).div(U.KG)));
		//System.out.printf("Main Mul: %s, %e\n", new UV(2, carl).mul(new UV(2, carl)), new UV(2, carl).mul(new UV(2, carl)).unit().getLength());
		//System.out.printf("Main Pow: %s, %e\n", new UV(2, carl).pow(2), new UV(2, carl).pow(2).unit().getLength());
		System.out.printf("Pow root: %s\n", new UV(3, carl).pow(1.0/2.0));
		
		
	}
}
