import java.io.*;
import java.util.*;


public class FileReverse {

        public static void main(String[] args) {

                ArrayList<String> lines = new ArrayList<String>();

                try {
                        File myFile = new File("/home/s0943941/Desktop/java/inputfile.txt");
                        Scanner input = new Scanner( myFile );
                        while ( input.hasNextLine() ) {
                                lines.add( input.nextLine() );
                        }
                } catch ( Exception ex ) {
                        System.out.println("Error: Could not load file");
                }

                try {
                        FileWriter writer = new FileWriter("outputFile");
                        for ( int i = lines.size() - 1; i >= 0; i-- ) {
                                writer.write(lines.get(i)+"\n");
                        }
                        writer.close();
                } catch ( Exception e ) {
                        System.out.println("Error: Could not write to file");
                }

        }

}
