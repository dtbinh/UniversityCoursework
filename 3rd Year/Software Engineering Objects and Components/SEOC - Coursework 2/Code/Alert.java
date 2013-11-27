
import java.util.ArrayList;


//Class for communicating various alerts between the classes
public class Alert {

	//Receive a standard order, pass it along to the appropriate plant
    public void alertPlant(Order order, ArrayList<Plant> plant) {
        int i = -1;
        switch(Integer.parseInt(order.getPlant())){
            case 811:
                i = 0;
                break;
            case 812:
                i = 1;
                break;
            case 813:
                i = 2;
                break;
            case 814:
                i = 3;
                break;
            case 815:
                i = 4;
                break;
            default:
                break;
        }
        plant.get(i).receiveAlert(order);
        plant.get(i).add(order);
    }

    //Alert orders upon successful start of an order
    public void alertOrderSuccess(Order order) {
        order.receiveSuccessAlert();
    }

    //Alert orders upon failure of an order
    public void alertOrderFailure(Order order, int failure) {
        order.receiveFailedAlert(failure);
    }
    
    //Alert the plant of a minor alert inside
    public void sendMinorAlert(Plant plant){
        plant.setMinorAlert();
    }
    
    //Alert the plant of a major alert inside
    public void sendMajorAlert(Plant plant){
        plant.setMajorAlert();
    }

}
