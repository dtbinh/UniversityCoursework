              public class Foo {
public static void main( String [] args )
{
int sumEven = 0;
int sumOdd = 0;
for ( int i = 0; i < args.length; i++ )
{int temp = Integer.parseInt(args[i]);
     if ( temp % 2 == 0 ) {
sumEven += temp; }

 else {
                    sumOdd += temp;}}
System.out.println("The sum of the even numbers is: " + sumEven);
System.out.println("The sum of the odd numbers is: " + sumOdd);
}
        }

