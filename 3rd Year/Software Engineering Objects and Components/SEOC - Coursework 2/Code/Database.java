
import java.util.*;

public class Database {

    public static ArrayList<Plant> plant = new ArrayList<Plant>();
    public static ArrayList<DistributionCentre> dc = new ArrayList<DistributionCentre>();

    public boolean checkSKU(String sku) {
        int tmp = 0;
        try {
            tmp = Integer.parseInt(sku);
        } catch (Exception e){}
        
        switch (tmp) {
            case 1020982648:
                return true;
            case 1002350932:
                return true;
            case 1023600676:
                return true;
            case 1043984673:
                return true;
            case 1043986790:
                return true;
            case 1043980694:
                return true;
            case 1043980535:
                return true;
            case 1002374056:
                return true;
            case 1056700145:
                return true;
            case 1002670743:
                return true;
            case 1048000457:
                return true;
            case 1048000458:
                return true;
            case 1023100679:
                return true;
            case 1035620983:
                return true;
            case 1035620560:
                return true;
            case 1035620340:
                return true;
            case 1035620214:
                return true;
            default:
                return false;
        }
    }

    public static DistributionCentre checkDC(String distc) {
        int tmp = 0;
        try {
            tmp = Integer.parseInt(distc);
        } catch (Exception e){}
        switch (tmp) {
            case 531:
                return dc.get(0);
            case 532:
                return dc.get(1);
            case 533:
                return dc.get(2);
            default:
                return null;
        }
    }

    Database() {
        dc.add(new DistributionCentre("531"));
        dc.add(new DistributionCentre("532"));
        dc.add(new DistributionCentre("533"));

        plant.add(new Plant("811"));
        plant.add(new Plant("812"));
        plant.add(new Plant("813"));
        plant.add(new Plant("814"));
        plant.add(new Plant("815"));

    }
}
