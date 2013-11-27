public class Sums {

    public static void main(String[] args)
    {
	int [] myArray = {1,2,3,4,5,6,7,8,9,10};
	int sum = 0;
	for(int i=0; i<10; i++)
	    {//start for
		sum = sum + myArray[i];
		    System.out.println("Array index "+i+" has value "+myArray[i]);
	    }
	System.out.println("The sum of everything is "+sum);
    }}
		