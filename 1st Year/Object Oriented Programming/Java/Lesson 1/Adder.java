import java.util.Scanner;

public class Adder {

    public static void main(String[] args){
	    int num1, num2;
	    Scanner input = new Scanner( System.in );
	    System.out.print("Please give me a first number: ");
	    num1 = input.nextInt();
	    System.out.print("Please give me a second number: ");
	    num2 = input.nextInt();
	    System.out.println("The sum of "+num1+" and "+num2+" equals "+(num1+num2));
		}
    }