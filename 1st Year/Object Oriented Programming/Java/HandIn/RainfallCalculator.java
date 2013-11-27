
public class RainfallCalculator {

	    public static void main (String[] args) {
		double test, average;
		double full=0;
		int count=0;
		for(String s: args) {
		    test = Double.parseDouble(s);
		    full+=test;
		    count++;
	        }
		average = full/count;
		System.out.println("The amount of rainfall over the last "+count+" days was "+average+"mm");
	    }


}
