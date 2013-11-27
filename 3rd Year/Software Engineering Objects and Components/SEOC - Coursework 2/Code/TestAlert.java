import static org.junit.Assert.*;

import org.junit.Test;


public class TestAlert {

	@Test
	public void testAlertPlant() {
		Database db = new Database();
		Order order = new Order("1020982648", "230", db.checkDC("533"));
		Alert alert = new Alert();
		int quantity1 = Database.plant.get(0).getQueue().size();
		alert.alertPlant(order, Database.plant);
		java.util.Date date = new java.util.Date();
		int quantity2 = Database.plant.get(0).getQueue().size();
		assertTrue("Result", quantity1<quantity2);		
		assertEquals("Result",Long.valueOf(order.getTimestamp().getTime())/10, Long.valueOf(date.getTime())/10);

	}

	@Test
	public void testSendMinorAlert() {
		Alert alert = new Alert();
		Plant plant = new Plant("811");
		alert.sendMinorAlert(plant);
		assertEquals("Result", true, plant.getMinorAlert());
	}

	@Test
	public void testSendMajorAlert() {
		Alert alert = new Alert();
		Plant plant = new Plant("811");
		alert.sendMajorAlert(plant);
		assertEquals("Result", true, plant.getMajorAlert());
	}

}
