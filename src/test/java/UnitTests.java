import org.junit.Assert;
import org.junit.Test;


public class UnitTests {

	@Test
	public void conversion() throws UnitMismatchException {
		//Simple defPower unit converts correctly to unit corresponding unit without defPower.
		Assert.assertEquals(new UV(5, U.L).convert(U.M.pow(3)), new UV(5, U.L));

		Assert.assertEquals(new UV(5, U.M).convert(U.CM), new UV(5, U.M));
		Assert.assertEquals(new UV(5, U.CC).convert(U.L), new UV(5, U.CC));

		// Custom unit based on multiple quantities converts correctly to original unit
		U cu = new U(U.M.div(U.KG), 0.025, "cu", "compound unit");
		Assert.assertEquals(new UV(0.025, U.M.div(U.KG)), new UV(1, cu).convert(U.M.div(U.KG)));
	}

	@Test
	public void defPower() throws UnitMismatchException {
		// Units defined with defPower != 1 converts correctly
		U dms = new U(U.M.pow(2), 2, "DMS", "DoubleMeterSq");
		Assert.assertEquals(new UV(1, dms), new UV(2, U.M.pow(2)));
		Assert.assertNotEquals(new UV(1, dms), new UV(1, U.M.pow(2)));
		Assert.assertNotEquals(new UV(1, dms), new UV(4, U.M.pow(2)));
	}
	
	@Test
	public void arithmetic() throws UnitMismatchException {
		Assert.assertEquals(new UV(3, U.M).add(2, U.M), new UV(5, U.M));
		Assert.assertEquals(new UV(3, U.M).mul(2, U.M), new UV(6, U.M.pow(2)));
		Assert.assertEquals(new UV(3, U.M).mul(50, U.CM), new UV(1.5, U.M.pow(2)));
	}

}
