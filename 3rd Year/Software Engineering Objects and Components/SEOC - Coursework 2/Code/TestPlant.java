import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;


public class TestPlant {

	@Test
	public void testFetchOrder() {
		Plant plant = new Plant("811");
		DistributionCentre dc = new DistributionCentre("533");
		Order order = new Order("1020982648", "46", dc);
		plant.fetchOrder(order);
		Date date = new Date();
		
		/*Because this functions in real time, and the function may
		occasionally take longer than a millisecond to process,
		we divide the result by 10 to eliminate 1/10th of a
		millisecond from the accuracy */
		
        assertEquals("Result", Long.valueOf(order.getTimestamp().getTime())/10, Long.valueOf(date.getTime())/10);
        
        }

	@Test
	public void testGetName() {
		Plant plant = new Plant("811");
		assertEquals("Result", "London", plant.getName());
		Plant plant2 = new Plant("812");
		assertEquals("Res		int quantity3 = pdt.update(true, false);ult", "Manchester", plant2.getName());	
		Plant plant3 = new Plant("813");
		assertEquals("Result", "Newcastle", plant3.getName());	
	}

	@Test
	public void testSetMinorAlert() {
		Plant plant = new Plant("811");
		DistributionCentre dc = new DistributionCentre("533");
		Order order = new Order("1020982648", "46", dc);
		plant.add(order);
		assertEquals("Result", false, plant.getMinorAlert());
		plant.setMinorAlert();
		assertEquals("Result", true, plant.getMinorAlert());
		plant.setMinorAlert();
		assertEquals("Result", false, plant.getMinorAlert());
	}

	@Test
	public void testSetMajorAlert() {
		Plant plant = new Plant("811");
		DistributionCentre dc = new DistributionCentre("533");
		Order order = new Order("1020982648", "46", dc);
		plant.add(order);
		assertEquals("Result", false, plant.getMajorAlert());
		plant.setMajorAlert();
		assertEquals("Result", true, plant.getMajorAlert());
	}

	@Test
	public void testAdd() {
		Plant plant = new Plant("811");
		DistributionCentre dc = new DistributionCentre("533");
		Order order = new Order("1020982648", "46", dc);
		plant.getQueue().add(order);
		assertEquals("Result", plant.getQueue().element(), order);
	}

	@Test
	public void testPrint() {
		Plant plant = new Plant("811");
		DistributionCentre dc = new DistributionCentre("533");
		Order order = new Order("1020982648", "466", dc);
		Order order2 = new Order("1002350932", "656", dc);
		plant.add(order);
		plant.add(order2);
		plant.fetchOrder(order);
		plant.fetchOrder(order2);
		assertEquals("Result", 2, plant.getQueue().size());
		plant.getQueue().element().setComplete(true);
		plant.print();
		assertEquals("Result", 1, plant.getQueue().size());		
		plant.print();
		assertEquals("Result", 1, plant.getQueue().size());
		plant.getQueue().element().setComplete(true);
		plant.print();
		assertEquals("Result", 0, plant.getQueue().size()); 
	}

}
