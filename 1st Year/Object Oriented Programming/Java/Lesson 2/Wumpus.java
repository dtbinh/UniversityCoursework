import java.util.Random;
import java.util.Scanner;

public class Wumpus {//start Wumpus

    /*   public static void main(String[] args) {
	Random generator = new Random();
	Scanner myScanner = new Scanner(System.in);
	System.out.print("Please enter the width of the world: ");
	int mySize = myScanner.nextInt();
	int randnum = generator.nextInt(mySize);
	System.out.println(randnum);
	for(int i=0; i<mySize; i++) {//start for
	    System.out.print("#");}
	System.out.println();
	for(int i=0; i<mySize; i++) {//start for
	    if(randnum==i)
		{System.out.print("W");}
	    else
		{System.out.print(" ");}
	}
	System.out.println();
	for(int i=0; i<mySize; i++) {//start for
	System.out.print("#");} 
	    

	
	} */

    public static void main(String[] args) {
	//variables
	  Random generator = new Random();
	  Scanner myScanner = new Scanner(System.in);
	
	//Get width of world 	
	  System.out.print("Please enter the width of the world: ");
	  int width = myScanner.nextInt();
        
	//Get height of world
	  System.out.print("Please enter the height of the world: ");
          int height = myScanner.nextInt();
	
	//Create array
	  int [] [] locations = new int[height][width];
	
	//Random wumpus location
	  int randnum1 = generator.nextInt(height);
	  int randnum2 = generator.nextInt(width);

	//assigning locations
	  for(int i=0; i<height; i++){//start for
	      for(int j=0; j<width; j++) {//start inner for
		  locations[i][j]=0;}}
	    locations[randnum1][randnum2] = 1;
	
        //Checking shit
          System.out.println(randnum1);
	  System.out.println(randnum2);

	  //Showing the array
	    for(int i=0; i<(height); i++) {//start for
		System.out.print("#");
		for(int j=0; j<width; j++) {//start inner for
	    if(locations[i][j] == locations[randnum1][randnum2])
		{System.out.print("W");}
	    else
		{System.out.print(" ");}
		}//end inner for
	     System.out.print("#\n");

	    }//end for
    }


}//end 