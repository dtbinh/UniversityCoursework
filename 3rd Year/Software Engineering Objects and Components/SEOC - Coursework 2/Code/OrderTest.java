import java.security.Timestamp;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class OrderTest extends TestCase {

	public void testGetSKU() {
		Order ot = new Order("1020982648","1",null);
		Order ot2 = new Order("1002350932","1",null);
		Order ot3 = new Order("1023600676","1",null);
		assertEquals("Result","1020982648",ot.getSKU());
		assertEquals("Result","1002350932",ot2.getSKU());
		assertEquals("Result","1023600676",ot3.getSKU());
	}

	public void testGetMass() {
		Order ot = new Order("1020982648","1",null);
		Order ot2 = new Order("1043984673","2",null);
		Order ot3 = new Order("1023600676","3",null);
	    double d =1*0.95;
	    double d2 = 2*0.2;
	    double d3 =3*0.4;
	    assertEquals("Result",d,ot.getMass());
	    assertEquals("Result",d2,ot2.getMass());
	    assertEquals("Result",d3,ot3.getMass());
	}

	public void testGetQuantity() {
		Order ot = new Order("1020982648","1",null);
		Order ot2 = new Order("1020982648","2",null);
		Order ot3= new Order("1020982648","3",null);
		assertEquals("Result","1",Integer.toString(ot.getQuantity()));
		assertEquals("Result","2",Integer.toString(ot2.getQuantity()));
		assertEquals("Result","3",Integer.toString(ot3.getQuantity()));
	}

	public void testGetQuantityProduced() {
		Order ot = new Order("1020982648","10",null);
		Order ot2 = new Order("1020982648","11",null);
		Order ot3 = new Order("1020982648","12",null);
		ot.setQuantityProduced(10);
		ot2.setQuantityProduced(11);
		ot3.setQuantityProduced(12);
		assertEquals("Result",10,ot.getQuantityProduced());
		assertEquals("Result",11,ot2.getQuantityProduced());
		assertEquals("Result",12,ot3.getQuantityProduced());
	}

	public void testSetQuantityProduced() {
		Order ot = new Order("1020982648","10",null);
		Order ot2 = new Order("1020982648","11",null);
		Order ot3 = new Order("1020982648","12",null);
		ot.setQuantityProduced(10);
		ot2.setQuantityProduced(10);
		ot3.setQuantityProduced(10);
		assertEquals("Result","10",Integer.toString(ot.getQuantityProduced()));
		assertEquals("Result","10",Integer.toString(ot2.getQuantityProduced()));
		assertEquals("Result","10",Integer.toString(ot3.getQuantityProduced()));
	}

	public void testGetPlant() {
		Order ot = new Order("1020982648","1",null);
		Order ot2 = new Order("1002350932","1",null);
		Order ot3 = new Order("1043980694","1",null);
		
		assertEquals("Result","811",ot.getPlant());
		assertEquals("Result","811",ot2.getPlant());
		assertEquals("Result","812",ot3.getPlant());
	}

	public void testGetDC() {
		DistributionCentre dc = new DistributionCentre("531");
		DistributionCentre dc2 = new DistributionCentre("532");
		DistributionCentre dc3 = new DistributionCentre("533");
		assertEquals("Result","531",Integer.toString(dc.getIntCode()));
		assertEquals("Result","532",Integer.toString(dc2.getIntCode()));
		assertEquals("Result","533",Integer.toString(dc3.getIntCode()));
	}
	

	public void testGetStatus() {
		Order ot = new Order("1043980694","1",null);
		assertEquals("Result",true,ot.getStatus());
	}

	public void testGetComplete() {
		Order ot = new Order("1043980694","1",null);
		Order ot2 = new Order("1043980694","1",null);
		ot.setComplete(true);
		ot2.setComplete(false);
		assertEquals("Result",true,ot.getComplete());
		assertEquals("Result",false,ot2.getComplete());
	}

	public void testSetComplete() {
		Order ot = new Order("1020982648","10",null);
		Order ot2 = new Order("1020982648","11",null);
		
		ot.setComplete(true);
		ot2.setComplete(false);
		
		assertEquals("Result",true,ot.getComplete());
		assertEquals("Result",false,ot2.getComplete());
		
	}

	public void testGetSPT() {
		Order ot = new Order("1043980694","1",null);
		Order ot2 = new Order("1002350932","1",null);
		assertEquals("Result",100,ot.getSPT());
		assertEquals("Result",300,ot2.getSPT());
	}

	public void testGetTimestamp() {
		Order ot = new Order("1043980694","1",null);
		Date d =new Date();
		d.getTime();
		java.sql.Timestamp t= new java.sql.Timestamp(d.getTime());
		ot.setTimestamp(t);
		assertEquals("Result",t,ot.getTimestamp());
	}

	public void testSetTimestamp() {
		Order ot = new Order("1043980694","1",null);
		Date d =new Date();
		d.getTime();
		java.sql.Timestamp t= new java.sql.Timestamp(d.getTime());
		ot.setTimestamp(t);
		assertEquals("Result",t,ot.getTimestamp());;
	}

	public void testGetECT() {
		Order ot = new Order("1043980694","1",null);
		ot.setECT(2.2);
		assertEquals("Result",2.2,ot.getECT());;
	}

	public void testSetECT() {
		Order ot = new Order("1043980694","1",null);
		ot.setECT(2.3);
		assertEquals("Result",2.3,ot.getECT());;
	}


	
	
	public static Test suite(){
		return new TestSuite(OrderTest.class);
	}


}
