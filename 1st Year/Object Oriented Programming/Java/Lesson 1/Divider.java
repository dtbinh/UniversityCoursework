import java.util.Scanner;

public class Divider {

    public static void main(String[] args)
    {Scanner input = new Scanner (System.in);
	int num1, num2;
	System.out.print("Please give me a first number: ");
	num1 = input.nextInt();
	System.out.print("Please give me a second number: ");
	num2 = input.nextInt();
	while(num2==0)
	    {System.out.print("This cannot happen. Wormholes. Please give me a better number: ");
		num2 = input.nextInt();}
	
	System.out.println("The quotient of "+num1+" and "+num2+" equals "+(num1/num2));

    }


}