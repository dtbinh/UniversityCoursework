import java.util.*;
import java.lang.Math;
import java.sql.Timestamp;
import java.util.Date;

public class Plant {


    private Queue<Order> queue = new LinkedList<Order>();
    
    //Default plant values
    private String plant_Code = null;
    private String plant_Name = null;
    private String plant_Address1 = null;
    private String plant_Address2 = null;
    private String plant_Address3 = null;
    private String plant_Address4 = null;
    private String plant_Postcode = null;
    private boolean minorAlert = false;
    private boolean majorAlert = false;
    
    public void fetchOrder(Order order) {
        Alert alert = new Alert();
        if (order.getStatus()) {//Check if there is truly an order

            if (order.getPlant().equals(plant_Code)) {//If its the proper plant

                //Start the production 
                 new Production(order);
                
                alert.alertOrderSuccess(order);

            }
        } else {//Should never be used
            System.out.println("No Plants can produce this order");
        }
    }
    
    public Queue<Order> getQueue() {
    	return queue;
    }

    public void receiveAlert(Order order) { //Receive an alert that an order has been made
        fetchOrder(order);
    }

    public String getName() {
        return plant_Name;
    }
    
    public boolean getMinorAlert() {
    	return minorAlert;
    }
    
    public boolean getMajorAlert() {
    	return majorAlert;
    }
    

    public void setMinorAlert(){ //Activates a minor alert on the plant - trivial time delay
        if(minorAlert){
            Date date= new Date();
            minorAlert = false;
            queue.element().setTimestamp(new Timestamp(date.getTime()));
        }
        else
            minorAlert = true;
        System.out.println("\nMinor Alert is now set to " + minorAlert);
    }
    
    public void setMajorAlert(){ //Activates a major alert on the plant - aborts the current order        
        majorAlert = true;
        System.out.println("\nMajor Alert is now set to " + majorAlert);
    }

    public Plant(String code) {//Construct plant object with details, depending on the Code
        int switchcode = Integer.parseInt(code);
        switch (switchcode) {
            case 811:
                plant_Code = "811";
                plant_Name = "London";
                plant_Address1 = "The Heights";
                plant_Address2 = "Brooklands";
                plant_Address3 = "";
                plant_Address4 = "";
                plant_Postcode = "KT13 0XP";
                break;
            case 812:
                plant_Code = "812";
                plant_Name = "Manchester";
                plant_Address1 = "Trafford Park Road";
                plant_Address2 = "Trafford Park";
                plant_Address3 = "";
                plant_Address4 = "";
                plant_Postcode = "M17 1NX";
                break;
            case 813:
                plant_Code = "813";
                plant_Name = "Newcastle";
                plant_Address1 = "Whitley Road";
                plant_Address2 = "Longbenton";
                plant_Address3 = "";
                plant_Address4 = "";
                plant_Postcode = "NE12 9TS";
                break;
            case 814:
                plant_Code = "814";
                plant_Name = "Reading";
                plant_Address1 = "452 Basingstoke Road";
                plant_Address2 = "Reading";
                plant_Address3 = "";
                plant_Address4 = "";
                plant_Postcode = "RG2 0QE";
                break;
            case 815:
                plant_Code = "815";
                plant_Name = "Seaton Deleval";
                plant_Address1 = "Avenue Road";
                plant_Address2 = "Seaton Deleval";
                plant_Address3 = "";
                plant_Address4 = "";
                plant_Postcode = "NE25 0QJ";
                break;
            default:
                break;
            
        }
         
    }

    public void add(Order placeorder) {//Add the order to the placeorder
        queue.add(placeorder);
    }

    public void print(){//Display the values currently in production
    	
        Production production = new Production(queue); 
        int quantity = production.update(minorAlert, majorAlert); //Pass it any alerts and update the values for every element in the queue
        
        Iterator<Order> iterator = queue.iterator(); //Create an iterator for all elements in the queue
        Order tmp; //Current Order we are showing
       
        majorAlert = false;
        boolean done = false;
        boolean producing = false;
        Production pdt = null;
        
        if(iterator.hasNext()){ //if there is an order in the queue, print it out and its progress
            producing = true;
            tmp = (Order) iterator.next();
            tmp.print();
            if(tmp.getComplete()){
                System.out.println("\tOrder is completed and sent to Distribution Center " + tmp.getDC());
                queue.remove();
                done = true;
            }
        }
        
        if (done){ //if done with first order in queue, begin the following order
            iterator = queue.iterator();
            if(iterator.hasNext()){ //begin the following order if there is a following order
                tmp = (Order) iterator.next();
                pdt = new Production(tmp);
                tmp = pdt.order;
                tmp.print();
            }
        }
        
        while(iterator.hasNext()){ //print remaining items in the queue
            tmp = (Order) iterator.next();
            tmp.print();
        }
        
        if(!producing)
            System.out.println("\t(No orders are in queue)");
    }

}