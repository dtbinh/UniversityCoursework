public class OddTriangle {


    public static void main(String[] args) {


	for(int i = 1; i<10; i++)
	    {//start for

		for(int j=1; j<10; j++)
		    {//start for
			if (j <= i)
			    {
				System.out.print (i);
			    }
		    }
		System.out.print ("\n");
	    }
    }
}