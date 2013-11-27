import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.Queue;
import java.lang.Math;

public class Production {
    
    Order order;
    Queue<Order> queue;    
    
    public Production(Order placeorder) {
    	//Create a production for a single order
        order = placeorder;
        createProducts();
        queue = null;
    }
    
    public Production(Queue<Order> q) {
    	//Create a production for a queue of orders
        queue = q;
        order = null;
    }
    
    public void createProducts(){
    	//Get a realtime update for when the product was created
        Date date= new Date();
        order.setTimestamp(new Timestamp(date.getTime()));
    }
    
        
    public int update(boolean minorAlert, boolean majorAlert){
    	//This function updates the current values of an order.
    	
        Date date= new Date();
        Timestamp CurrentTime = new Timestamp(date.getTime());        
        Iterator<Order> iterator = queue.iterator();
        Order tmp;
        int quantity = 0;

        if(iterator.hasNext()){                 //if there is an order in the queue
            tmp = (Order) iterator.next(); //Begin the first order
            quantity = tmp.getQuantityProduced();
            
            if(majorAlert) {	//In case of a major Alert, remove the order from the queue
            	queue.remove(); //Removes order at the front of the queue
            	minorAlert = false; //Assume all minor alerts don't matter at this point
                majorAlert = false;
            }
            if(minorAlert) { //In case of a minor Alert, pause production
                quantity = tmp.getQuantityProduced();
                tmp.setTimestamp(CurrentTime);
                tmp.setECT(1.0/0.0);
                System.out.println("\tMinor Alert is set to " + minorAlert + "\n\tProduction will continue given another Minor Alert");
            } else {
            	//update the quantity to reflect a real-time production
                quantity += (int)((CurrentTime.getTime() - tmp.getTimestamp().getTime())/tmp.getSPT());
                tmp.setTimestamp(CurrentTime);
                tmp.setECT(tmp.getSPT()*(tmp.getQuantity()-quantity));
            }

            //Check to see if the production is done. 
            if (quantity > tmp.getQuantity()){
                tmp.setComplete(true);
                tmp.setQuantityProduced(tmp.getQuantity());
            } 
            else {
                tmp.setQuantityProduced(quantity);
            }
        }
        return quantity;
    }
    
}
