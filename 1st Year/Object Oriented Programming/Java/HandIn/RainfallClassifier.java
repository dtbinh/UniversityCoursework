public class RainfallClassifier {

	    public static void main (String[] args) {
		double test, average;
		double full=0;
		int count=0;
		String category = "";
		for(String s: args) {
		    test = Double.parseDouble(s);
		    full+=test;
		    count++;
	        }
		average = full/count;
		if(count==1)
		    {System.out.println("The average rainfall over the last day was "+average+"mm");}
		else{
		    System.out.println("The average rainfall over the last "+count+" days was "+average+"mm");}
		if(average>0)
		    {category="Light";}
		if(average>3)
		    {category="Medium";}
		if(average>6)
		    {category="Heavy";}
		if(average>=9)
		    {category="Scottish";}
		System.out.println("The amount of rainfall is calssified as: "+category);
	    }




}