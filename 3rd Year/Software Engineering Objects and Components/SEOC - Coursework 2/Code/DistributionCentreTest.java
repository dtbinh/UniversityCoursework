import static org.junit.Assert.*;

import org.junit.Test;


public class DistributionCentreTest {

	@Test
	public void testGetAddress() {
		DistributionCentre dc = new DistributionCentre("531");
		assertEquals("Result","West Thurrock", dc.getAddress());
		DistributionCentre dc2 = new DistributionCentre("532");
		assertEquals("Result","Skelmersdale", dc2.getAddress());
		DistributionCentre dc3 = new DistributionCentre("533");
		assertEquals("Result","Bournemouth", dc3.getAddress());
	}

	@Test
	public void testGetIntCode() {
		DistributionCentre dc = new DistributionCentre("531");
		assertEquals("Result", 531, dc.getIntCode());
		DistributionCentre dc2 = new DistributionCentre("532");
		assertEquals("Result", 532, dc2.getIntCode());
		DistributionCentre dc3 = new DistributionCentre("533");
		assertEquals("Result", 533, dc3.getIntCode());
	}

	@Test
	public void testGetStringCode() {
		DistributionCentre dc = new DistributionCentre("531");
		assertEquals("Result", "531", dc.getStringCode());
		DistributionCentre dc2 = new DistributionCentre("532");
		assertEquals("Result", "532", dc2.getStringCode());
		DistributionCentre dc3 = new DistributionCentre("533");
		assertEquals("Result", "533", dc3.getStringCode());
	}

	@Test
	public void testGetName() {
		DistributionCentre dc = new DistributionCentre("531");
		assertEquals("Result", "P&G Distribution", dc.getName());
		DistributionCentre dc2 = new DistributionCentre("532");
		assertEquals("Result", "P&G Distribution", dc2.getName());
		DistributionCentre dc3 = new DistributionCentre("533");
		assertEquals("Result", "P&G Distribution", dc3.getName());
	}

}
