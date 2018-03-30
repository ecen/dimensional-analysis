import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTests {

	@Test
	public void conversion() throws UnitMismatchException {
		assertEquals(new UV(5, U.L).convert(U.M.pow(3)), new UV(5, U.L));
		assertEquals(new UV(5, U.M).convert(U.CM), new UV(5, U.M));
		assertEquals(new UV(5, U.CC).convert(U.L), new UV(5, U.CC));

		// Custom unit based on multiple quantities converts correctly to original unit
		U cu = new U(U.M.div(U.KG), 0.025, "cu");
		assertEquals(new UV(0.025, U.M.div(U.KG)), new UV(1, cu).convert(U.M.div(U.KG)));
	}
	
	@Test
	public void arithmetic() throws UnitMismatchException {
		assertEquals(new UV(3, U.M).add(2, U.M), new UV(5, U.M));
		assertEquals(new UV(3, U.M).mul(2, U.M), new UV(6, U.M.pow(2)));
		assertEquals(new UV(3, U.M).mul(50, U.CM), new UV(1.5, U.M.pow(2)));
	}

}
