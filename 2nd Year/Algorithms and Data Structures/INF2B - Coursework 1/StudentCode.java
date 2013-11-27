package closestPair;

import java.util.*;

/** The student's implementation of Shamos's Algorithm. */
public class StudentCode {

    /** Find the square distance of the closest pairs in the point set. This static function is the preparation step for the recursive part of the algorithm defined in the method closestPairAux. */
    public static int closestPair(PointSet P) 
            throws TrivialClosestPairException, UnknownSortOptionException
    {
    	//These steps create two arrays to contain the provided PointSet, and then store the sorted arrays by 'x' and 'y' for the recursive part of the algorithm to use.
    		Point[] X = new Point[P.size()]; 			
    		Point[] Y = new Point[P.size()];
    		X = P.sort('x');
    		Y = P.sort('y');
    		
		return closestPairAux(X,Y);
    }

    /** The recursive part of Shamos's Algorithm. The parameter X is an array of points sorted by the X axis and the parameter Y is the same array of points sorted by the Y axis. The burden of work is going on here. Good luck! */
    public static int closestPairAux(Point[] X, Point[] Y) 
            throws TrivialClosestPairException, UnknownSortOptionException
    {
    	//Variables
    		int n = X.length;
    		int m = Y.length;
    		int middle = (n-1)/2;
    		int midline = X[middle].getX(); 						//midline of the array determined by the x Coordinate
    		int delta = 0; 											//Minimum value overall
    		
    	//Brute force check	
    		if (n <= 3) {
    			if(n < 2) //ensures there are at least two points	
    				throw new TrivialClosestPairException();		
    			
    			if(n == 2) //If only two points, this returns the distance between them using the Euclidean distance provided by point.java
    				return X[0].sqrDist(X[1]);						
    			
    			if(n == 3) {//If three points, check the distance of one value against the other two. If its the lowest, then it is returned						
    				
    				int a = X[0].sqrDist(X[1]);
    				int b = X[0].sqrDist(X[2]);
    				int c = X[1].sqrDist(X[2]);
    				
    				if(a <= b && a <= c) 
    					return a;
    				
    				if(b <= a && b <= c)
    					return b;
    				
    				if(c <= a && c <= b)
    					return c;
    			}

    		}
    		
    	//Recursive part (more than 3 elements requiring a split)	
    		else {
    			//Create two arrays to divide the X array into smaller, more manageable parts. These arrays will be recursively called upon, to check and see if the size is small enough to give brute force answers.
    			//These arrays are split so that the left will contain more points if necessary with the Math.ceil function.
    			  Point[] XL = new Point[(n-1)/2 + 1];
    			  Point[] XR = new Point[n/2];
    			  
    			//These array lists are created to split Y in half, while still based on the respective X values.
    			  ArrayList<Point> ylList = new ArrayList<Point>();
    			  ArrayList<Point> yrList = new ArrayList<Point>();
    			
    			//Duplicates counts the number of values along the midline. This is used to assign them to their proper locations without the array going out of bounds.
    			//Set to 1, because there will always be at least one point on the midline.    
    			  int duplicates = 1;
    			  
      			//The for loop copies the first half for XL, the bigger side. It also counts the duplicates in the list, to keep track of them when you split Y
    			  for(int i = 0; i < (n-1)/2 + 1; i++) {
    				  XL[i] = X[i];  
    				  if(i > 0 && XL[i].getX() == XL[i-1].getX()) 
    					  duplicates++;
    			  }

    			//Copy half of the X array into XR
    			  System.arraycopy(X, (n-1)/2 + 1, XR, 0, n/2);
    			  
    			//Split Y into seperate halves
    			  for(int i = 0; i < (m); i++) {

    				//If the current point X is less than the midline, add the point to the left side. 
    				  if(Y[i].getX() < midline)
    					  ylList.add(Y[i]);

    				//If the current point X is greater than the midline, add it to the right side. 
    				  if(Y[i].getX() > midline) 
    					  yrList.add(Y[i]);

    				//If it equals the midline, it checks how many duplicates are on the midline. If there are still duplicates, it adds them to the left. If not, to the right. 
    				  if(Y[i].getX() == midline) {  
    					  
    					  if(duplicates != 0) {
    						  ylList.add(Y[i]);
    						  duplicates--;
    					  }  
    					  
    					  else 
    						  yrList.add(Y[i]);
    					  
    				  }
     			  }
    			  
    			//Creates arrays to hold the divided y for the recursive call
    			  Point[] YL = new Point[ylList.size()];
    			  Point[] YR = new Point[yrList.size()];
    			
    			//Converts the y arraylists into arrays for the recursive call. 
    			  YL = ylList.toArray(YL);
    			  YR = yrList.toArray(YR);    			  
    			     			
    			//Recursively call closestPairAux on the divided array, searching for a length less than 4 to give an answer. If not, the function recursively runs again.
     			  int minimumL = closestPairAux(XL, YL);
    			  int minimumR = closestPairAux(XR, YR);
        		
    		    //Delta is chosen as the minimum of the two minimums found above. 
    			  delta = Math.min(minimumL, minimumR);	  
    			  
    			//In case the shortest distance lies between the split arrays, we check the strip formed by delta distance away from the midline.
    			//This is done by checking the sorted Y list, and comparing it to the other values (save for the last point)
    			  for(int i = 0; i < (m-1); i++) {
    				  
    				  if(Math.pow( midline - Y[i].getX(), 2 ) <= Math.pow( delta,2 )) {				//Checks that the X value is within the strip provided. Only then does it proceed to check the Y values. 
 
    					  for(int j = 1; j < 8; j++) {												//Because of the minimum distance requirement, we check the next seven points. Anything greater will exceed delta.
    																				
    							 if((i+j < m) && Y[i].sqrDist(Y[i+j]) < delta) 						//Checks that the array isn't out of bounds, and that the distance between the points is smaller than the minimum distance...
 
    								 delta = Y[i].sqrDist(Y[i+j]);  								//This becomes the new minimum distance to check against.
 
    						 
    					  }
    				  }
    			  } 			
    		
    		}
    		
    	return delta; //gives the minimum distance
    	
    }

}

