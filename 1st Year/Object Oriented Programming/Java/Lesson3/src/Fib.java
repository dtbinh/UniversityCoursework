import java.util.Scanner;

public class Fib {
	
	public static int FibCalc (int pos) {
		int var1=1;
		int var2=1;
		int var3=0;
		
		if(pos<2) {
			return 1;
		}
			for(int i=1; i<pos; i++) {
				var3=var1+var2;
				var1=var2;
				var2=var3;
			}
		return var2;
	}
	//========
	public int FibRec(int pos) {
		if(pos<2) {
			return 1;
		}
		return (FibRec(pos-1)+FibRec(pos-2));
	}
	public static void main(String[] args){
		Scanner input = new Scanner(System.in);
		System.out.print("How far do you want to go? ");
		int n = input.nextInt();
		Fib calc = new Fib();
		for(int i=0; i<n; i++) {
			System.out.print(calc.FibRec(i)+" ");
		}
	}

}
