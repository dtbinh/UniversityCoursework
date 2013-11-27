
public class DistributionCentre {

    private String dc_Code = null;
    private String dc_Name = null;
    private String dc_Address1 = null;
    private String dc_Address2 = null;
    private String dc_Address3 = null;
    private String dc_Address4 = null;
    private String dc_Postcode = null;



    public String getAddress() {
        return dc_Address2;
    }

    public int getIntCode() {
        return Integer.parseInt(dc_Code);
    }

    public String getStringCode() {
        return dc_Code;
    }

    public String getName() {
        return dc_Name;
    }

    public void print() {
        System.out.println("\n\t(" + dc_Code + "): " + dc_Name);
        System.out.println("\t" + dc_Address1);
        System.out.println("\t" + dc_Address2);
        //System.out.println("\t" + dc_Address3); //always an empty string, so i will ignore
        System.out.println("\t" + dc_Address4);
        System.out.println("\t" + dc_Postcode);

    }

    public DistributionCentre(String code) {

        int parsecode = Integer.parseInt(code);
        switch (parsecode) {
            case 531:
                dc_Code = "531";
                dc_Name = "P&G Distribution";
                dc_Address1 = "Hedley Avenue";
                dc_Address2 = "West Thurrock";
                dc_Address3 = "";
                dc_Address4 = "Essex";
                dc_Postcode = "RM20 4AL";
                break;
            case 532:
                dc_Code = "532";
                dc_Name = "P&G Distribution";
                dc_Address1 = "Pimbo Road";
                dc_Address2 = "Skelmersdale";
                dc_Address3 = "";
                dc_Address4 = "Lancashire";
                dc_Postcode = "WN8 9PE";
                break;
            case 533:
                dc_Code = "533";
                dc_Name = "P&G Distribution";
                dc_Address1 = "Wallisdown Road";
                dc_Address2 = "Bournemouth";
                dc_Address3 = "";
                dc_Address4 = "Dorset";
                dc_Postcode = "BH11 8PL";
                break;
            default:
                break;
        }

    }
}
