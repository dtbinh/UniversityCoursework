import java.sql.Timestamp;

public class Order {

    private String ps_Name = null;
    private double ps_Mass = 0;
    private int ps_QIP = 0;
    private int spt = 0;
    private String ps_Plant = null;
    private String SKU = null;
    private int ps_quantity = 0;
    private int quantity_produced = 0;
    private String ps_dc = null;
    private boolean order_status = false;
    private boolean order_complete = false;
    private double est_compl_time = 0;
    DistributionCentre dc;
    private Timestamp time;

  //Access and set methods
    public String getSKU() {
        return SKU;
    }

    public double getMass() {
        return ps_quantity * ps_Mass;
    }

    public int getQuantity() {
        return ps_quantity;
    }

    public int getQuantityProduced() {
        return quantity_produced;
    }
    
    public void setQuantityProduced(int quantity){
        quantity_produced = quantity;
    }

    public String getPlant() {
        return ps_Plant;
    }

    public String getDC() {
        return ps_dc;
    }

    public boolean getStatus() {
        return order_status;
    }
    
    public boolean getComplete(){
        return order_complete;
    }
    
    public void setComplete(boolean setComplete){
        order_complete = setComplete;
    }

    public int getSPT() {
        return spt;
    }

    public Timestamp getTimestamp(){
        return time;
    }
    
    public void setTimestamp(Timestamp t){
        time = t;
    }
    
    public void setECT(double ect){
        est_compl_time = ect;
    }
    
    public double getECT() {
    	return est_compl_time;
    }
    
    public void receiveSuccessAlert() {
        System.out.println("The Plant Team is starting Order " + getSKU() + "\n");
    }

    public void receiveFailedAlert(int failed) {
        System.out.println("The Plant Team has failed to complete the order.");
        System.out.println("We have added " + failed + "to the Distribution Center before failure");
    }

    public void print(){
        double tmp = est_compl_time/600;
        if (tmp < 0)
            tmp = 0;
        System.out.println("\tProduct Name: " + ps_Name);
        System.out.println("\tSKU: " + SKU);
        System.out.printf("\tEstimated Time Remaining: "+ "%.2f" + " seconds\n", tmp);
        System.out.println("\tProgress: " + quantity_produced*100/ps_quantity + "%");
        System.out.println("\t\t" + quantity_produced + "/" + ps_quantity);
    }
    
    public Order(String sku, String quantity, DistributionCentre dc_test) {

        ps_quantity = Integer.parseInt(quantity);
        order_status = true; //There is now an order
        dc = dc_test;

        switch (Integer.parseInt(sku)) {
            case 1020982648:
                SKU = "1020982648";
                ps_Name = "Ariel";
                ps_Mass = 0.95;
                ps_QIP = 45;
                spt = 100;
                ps_Plant = "811";
                break;
            case 1002350932:
                SKU = "1002350932";
                ps_Name = "Pampers";
                ps_Mass = .45;
                ps_QIP = 45;
                spt = 300;
                ps_Plant = "811";
                break;
            case 1023600676:
                SKU = "1023600676";
                ps_Name = "Febreeze";
                ps_Mass = .4;
                ps_QIP = 150;
                spt = 200;
                ps_Plant = "811";
                break;
            case 1043984673:
                SKU = "1043984673";
                ps_Name = "Wella";
                ps_Mass = .2;
                ps_QIP = 250;
                spt = 400;
                ps_Plant = "812";
                break;
            case 1043986790:
                SKU = "1043986790";
                ps_Name = "Clairol";
                ps_Mass = .21;
                ps_QIP = 250;
                spt = 300;
                ps_Plant = "812";
                break;
            case 1043980694:
                SKU = "1043980694";
                ps_Name = "Head & Shoulders";
                ps_Mass = .41;
                ps_QIP = 125;
                spt = 100;
                ps_Plant = "812";
                break;
            case 1043980535:
                SKU = "1043980535";
                ps_Name = "Pantene";
                ps_Mass = .26;
                ps_QIP = 225;
                spt = 100;
                ps_Plant = "813";
                break;
            case 1002374056:
                SKU = "1002374056";
                ps_Name = "Olay";
                ps_Mass = .08;
                ps_QIP = 400;
                spt = 100;
                ps_Plant = "813";
                break;
            case 1056700145:
                SKU = "1056700145";
                ps_Name = "Crest";
                ps_Mass = .105;
                ps_QIP = 400;
                spt = 100;
                ps_Plant = "813";
                break;
            case 1002670743:
                SKU = "1002670743";
                ps_Name = "Vicks";
                ps_Mass = .035;
                ps_QIP = 400;
                spt = 200;
                ps_Plant = "813";
                break;
            case 1048000457:
                SKU = "1048000457";
                ps_Name = "Gillette Mach 3";
                ps_Mass = .45;
                ps_QIP = 125;
                spt = 500;
                ps_Plant = "814";
                break;
            case 1048000458:
                SKU = "1048000458";
                ps_Name = "Gillette Fusion";
                ps_Mass = .4;
                ps_QIP = 300;
                spt = 600;
                ps_Plant = "814";
                break;
            case 1023100679:
                SKU = "1023100679";
                ps_Name = "Duracell AA Batteries";
                ps_Mass = .4;
                ps_QIP = 300;
                spt = 400;
                ps_Plant = "814";
                break;
            case 1035620983:
                SKU = "1035620983";
                ps_Name = "Hugo Boss";
                ps_Mass = .31;
                ps_QIP = 225;
                spt = 100;
                ps_Plant = "815";
                break;
            case 1035620560:
                SKU = "1035620560";
                ps_Name = "Ghost";
                ps_Mass = .28;
                ps_QIP = 225;
                spt = 200;
                ps_Plant = "815";
                break;
            case 1035620340:
                SKU = "1035620340";
                ps_Name = "Lacoste";
                ps_Mass = .29;
                ps_QIP = 225;
                spt = 400;
                ps_Plant = "815";
                break;
            case 1035620214:
                SKU = "1035620214";
                ps_Name = "Max Factor";
                ps_Mass = .015;
                ps_QIP = 300;
                spt = 500;
                ps_Plant = "815";
                break;
            default:
                System.out.println("\nUnexpected Bad SKU value");
                order_status = false;
                break;
            
        }
        


        if (dc != null) {
            ps_dc = dc.getStringCode();
        } else {
            System.out.println("\nUnexpected Bad DC value");
        }

        if (order_status == true && dc_test != null) { //error checks for sku and dc inputs
            System.out.println("\nSending Alert to Plants about the newly placed Order\n");
        } else {
            System.out.println("\nOrder Placed Unsuccessfully\n");
        }
    }
}
