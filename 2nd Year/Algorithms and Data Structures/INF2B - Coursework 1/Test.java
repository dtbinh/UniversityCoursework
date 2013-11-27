package closestPair;

public class Test {

        public static void main(String[] argv) throws InvalidNumberOfTestsException, TrivialClosestPairException, UnknownSortOptionException {

    /* CODE ... */

        	ClosestPair.closestPairCheck(400, 900);
        /* CODE ... */

    /* CODE ... */
         /*   for(int i=0; i<10000; i++) {
        	PointSet P = PointSet.generatePoints(600);
        	int low = StudentCode.closestPair(P);
        	int low2 = PointSet.naiveClosestPair(P);
        	if(low!=low2) {
            System.out.println("This is StudentCode: "+low+" and this is Naive: "+low2);
            System.out.println("Blah");
        	}
            } */

            //ClosestPair.getRuntimes(10,400,"/afs/inf.ed.ac.uk/user/s09/s0943941/Desktop/closestPairTimes2");
    		//ClosestPair.getRatios(10,400,"/afs/inf.ed.ac.uk/user/s09/s0943941/Desktop/f3.txt");
    		//ClosestPair.plotRuntimes(57.7177, 0.8011, "/afs/inf.ed.ac.uk/user/s09/s0943941/Desktop/closestPairTimes");
    		//ClosestPair.plotRuntimes(57.3269, 0.8336, "/afs/inf.ed.ac.uk/user/s09/s0943941/Desktop/closestPairTimes2");
        }

    
}

