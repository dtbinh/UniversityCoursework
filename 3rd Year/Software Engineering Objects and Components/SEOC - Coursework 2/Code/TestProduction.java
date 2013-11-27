import static org.junit.Assert.*;

import java.util.Queue;

import org.junit.Test;


public class TestProduction {

	@Test
	public void testCreateProducts() {
		DistributionCentre dc = new DistributionCentre("533");
		Order order = new Order("1020982648", "2", dc);
		Production pdt = new Production(order);
		java.util.Date date = new java.util.Date();
		assertEquals("Result",Long.valueOf(pdt.order.getTimestamp().getTime())/10, Long.valueOf(date.getTime())/10);
	}

	@Test
	public void testUpdate() {
		Queue<Order> queue = null;   
		DistributionCentre dc = new DistributionCentre("533");
		Order order = new Order("1020982648", "2", dc);
		Order order2 = new Order("1002350932", "2", dc);
		Plant plant = new Plant("811");
		plant.add(order);
		plant.add(order2);
		plant.fetchOrder(order);
		plant.fetchOrder(order2);
		Production pdt = new Production(plant.getQueue());
		int quantity1 = pdt.update(false, false);
		//Because this is updated in real time, we add a delay
		try{Thread.sleep(2000);}
		catch(Exception e) {}
		int quantity2 = pdt.update(false, false);
		int quantity3 = pdt.update(true, false);
		int quantity4 = pdt.update(true, false);
		
		assertTrue("Result", quantity1<quantity2); //Ensures that the time dependent function works
		assertFalse("Result", quantity2==quantity3); //Ensures that the delaying function works
		assertEquals("Result", quantity3, quantity4); //Checks that two delays to a productions time leaves it the same
		assertEquals("Result", 2, plant.getQueue().size()); //Ensures we are dealing with 2 plants
		quantity1 = pdt.update(false, true); //Activate a major alert
		assertEquals("Result", 1, plant.getQueue().size()); //Ensures the major alert removed 1 plant
		quantity1 = pdt.update(true, true); //Activate two alerts
		assertEquals("Result", 0, plant.getQueue().size()); //Ensures that the major alert took priority and removed 1 plant
		
		
	}

}
