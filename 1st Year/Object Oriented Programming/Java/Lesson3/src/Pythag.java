import java.util.Scanner;

public class Pythag {
	
	public boolean isTriple(int a,int b, int c)
	{return c*c==a*a + b*b;}
	
	public int leg1(int a, int b) {
		return(a*a - b*b);
	}
	public int leg2(int a, int b){
		return(2*a*b);
	}
	public int hyp(int a, int b) {
		return (a*a + b*b);
	}
	
	public static void main(String[] args){
		Scanner myScanner = new Scanner(System.in);
		//System.out.print("Please enter the first side: ");
		//int a = myScanner.nextInt();
		//System.out.print("Please enter the second side: ");
		//int b = myScanner.nextInt();
		//System.out.print("Please enter the hypotenuse: ");
		//int c = myScanner.nextInt();
		int leg1;
		int leg2;
		int hyp;
		boolean istriple;
		Pythag triple = new Pythag();
		System.out.println("I\tJ\tleg1\tleg2\thyp\tisTriple");
		for(int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				leg1 = triple.leg1(i, j);
				leg2 = triple.leg2(i, j);
				hyp = triple.hyp(i, j);
				istriple = triple.isTriple(leg1, leg2, hyp);
				System.out.println(i+"\t"+j+"\t"+leg1+"\t"+leg2+"\t"+hyp+"\t"+istriple);
			}
		}
	}

}
