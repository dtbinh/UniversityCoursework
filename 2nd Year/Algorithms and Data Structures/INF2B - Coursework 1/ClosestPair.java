package closestPair;

import java.util.*;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.RowFilter;

/** The software toolkit for the excercise. */
public class ClosestPair {

    /** Check if the student's implementation produces correct outputs by hardcore tests on t point sets containing n points. */
    public static void closestPairCheck(int t, int n) throws InvalidNumberOfTestsException {
        try{
            
            if (t <= 0)
                throw new InvalidNumberOfTestsException();

            boolean isSuccess = true;
    
            for (int i = 1; i <= t; i++) {
                System.out.print("Test " + i + ": ");
                PointSet p = PointSet.generatePoints(n);
                System.out.print("(Naive algorithm: ");
                int naiveResult = PointSet.naiveClosestPair(p);
                System.out.print("done) (Student algorithm: ");
                int studentResult = StudentCode.closestPair(p);
                System.out.print("done)");
                if (naiveResult == studentResult) {
                    System.out.println(" ... comparison successful");
                } else {
                    isSuccess = false;
                    System.out.println(" ... comparison unsuccessful");
                    System.out.println();
                    System.out.println("The result from the naive algorithm    : " + naiveResult);
                    System.out.println("The result from the student's algorithm: " + studentResult);
                    break;
                }
            }
    
            if (isSuccess) {
                System.out.println();
                System.out.println("Congratulations! Your program has passed the heavy test.");
            } else {
                System.out.println();
                System.out.println("The program has terminated due to an unsuccessful test.");
            }

        } catch (UnknownSortOptionException e) {
            System.out.println("UnknownSortOptionException occurs.");
        } catch (TrivialClosestPairException e) {
            System.out.println("TrivialClosestPairException occurs.");
        }
    }

    /** Get the runtime of the naive algorithm. */
    public static long getNaiveRuntime(PointSet p) {
        try {
            long start = System.nanoTime();
            int naiveResult = PointSet.naiveClosestPair(p);
            long stop = System.nanoTime();
            return (stop - start) / 1000;
        } catch (TrivialClosestPairException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** Get the runtime of the student's implementation of Shamos's Algorithm. */
    public static long getStudentRuntime(PointSet p) {
        try {
            long start = System.nanoTime();
            int studentResult = StudentCode.closestPair(p);
            long stop = System.nanoTime();
            return (stop - start) / 1000;
        } catch (UnknownSortOptionException e) {
            e.printStackTrace();
            return -1;
        } catch (TrivialClosestPairException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** Measure worst-case runtimes of the naive algorithm and the student's implementation by hardcore tests (on p point sets for various sizes starting from 10 to 10t points) and save it into a text file. */
    public static void getRuntimes(int p, int t, String f) {
        try{

            BufferedWriter ofhdl = new BufferedWriter(new FileWriter(f));
            ofhdl.write("//  Input-size\t    Naive-RT\t    Student-RT\n");
            ofhdl.flush();

            for (int i = 1; i <= t; i++) {
                int size = 10 * i;
                System.out.print(">> Testing with " + p + " sets of " + size + " points ");
                System.out.flush();
                PointSet pointset = PointSet.generatePoints(size);
                long worstNaiveRuntime = 0;
                long worstStudentRuntime = 0;
    
                for (int j = 0; j < p; j++) {
                    System.out.print(".");
                    System.out.flush();
                    long naiveRuntime = getNaiveRuntime(pointset);
                    long studentRuntime = getStudentRuntime(pointset);
                    if (naiveRuntime > worstNaiveRuntime)
                        worstNaiveRuntime = naiveRuntime;
                    if (studentRuntime > worstStudentRuntime)
                        worstStudentRuntime = studentRuntime;
                }

                System.out.println(" done");
                System.out.flush();

                ofhdl.write(String.format(
                    "%14d\t%12d\t%14d\n", 
                    size, worstNaiveRuntime, worstStudentRuntime
                ));
                ofhdl.flush();
            }

            System.out.println(">> Complete!");

            ofhdl.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Get the ratios of worst-case runtimes of the student's implementation of Shamos's Algorithm divided by n lg n and save it into a text file. */
    public static void getRatios(int p, int t, String f) {
        try{

            ArrayList<Double> ratios = new ArrayList<Double>();

            BufferedWriter ofhdl = new BufferedWriter(new FileWriter(f));
            ofhdl.write("//  Input-size\t         Ratio\n");
            ofhdl.flush();

            for (int i = 1; i <= t; i++) {
                int size = 10 * i;
                System.out.print(">> Testing with " + p + " sets of " + size + " points ");
                System.out.flush();
                PointSet pointset = PointSet.generatePoints(size);
                long worstStudentRuntime = 0;
    
                for (int j = 0; j < p; j++) {
                    System.out.print(".");
                    System.out.flush();
                    long studentRuntime = getStudentRuntime(pointset);
                    if (studentRuntime > worstStudentRuntime)
                        worstStudentRuntime = studentRuntime;
                }

                double ratio = 1.0 * worstStudentRuntime / (size * Math.log(size));
                ratios.add(ratio);

                System.out.println(" done");
                System.out.flush();

                ofhdl.write(String.format(
                    "%14d\t%14.4f\n", 
                    size, ratio
                ));
                ofhdl.flush();
            }

            double[] sortedRatios = new double[ratios.size()];
            for (int i = 0; i < ratios.size(); i++)
                sortedRatios[i] = ratios.get(i);
            Arrays.sort(sortedRatios);

            double maxRatio = sortedRatios[sortedRatios.length - 1];
            double avgRatio = 0.0;
            for (int i = 0; i < sortedRatios.length; i++)
                avgRatio += sortedRatios[i];
            avgRatio /= sortedRatios.length;

            ofhdl.write("Sorted ratios are:");
            for (int i = 0; i < sortedRatios.length; i++) {
                if (i % 5 == 0) ofhdl.write("\n    ");
                ofhdl.write(String.format("%.4f", sortedRatios[i]));
                if (i != sortedRatios.length - 1) ofhdl.write(", ");
            }
            ofhdl.write("\n");
            ofhdl.write(String.format("Maximum ratio is: %14.4f\n", maxRatio));
            ofhdl.write(String.format("Average ratio is: %14.4f\n", avgRatio));

            System.out.println(">> Complete!");

            ofhdl.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Graphically plot the worst-case runtimes read from the text file and also plot the asymptotic lines for the maximum and average ratios. */
	public static void plotRuntimes(double maxRatio, double avgRatio, String inputRuntimesPath) {
		try {
			BufferedReader ifhdl = new BufferedReader(new FileReader(inputRuntimesPath));
			Hashtable<Integer, Integer> naiveTbl = new Hashtable<Integer, Integer>();
			Hashtable<Integer, Integer> studentTbl = new Hashtable<Integer, Integer>();
			try {
				while (true) {
					String line = ifhdl.readLine();
					if (line == null) break;
					line = line.trim();
					if (line.startsWith("//")) continue;
					String[] tokens = line.split("[ \t]+");
					if (tokens.length == 0) continue;
					if (tokens.length != 3) throw new IllegalArgumentException();
					int inputSize = Integer.parseInt(tokens[0]);
					int naiveRT = Integer.parseInt(tokens[1]);
					int studentRT = Integer.parseInt(tokens[2]);
                    /*
					if (!maxTbl.containsKey(inputSize))
						maxTbl.put(inputSize, Integer.MAX_VALUE);
					if (maxTbl.get(inputSize) > studentRT) {
						maxTbl.remove(inputSize);
						maxTbl.put(inputSize, studentRT);
					}
                    */
                    naiveTbl.put(inputSize, naiveRT);
                    studentTbl.put(inputSize, studentRT);
				}
			} catch (IOException e) {
			}

			double[][] data = new double[naiveTbl.size()+1][3];

            data[0][0] = 0.0;
            data[0][1] = 0.0;
            data[0][2] = 0.0;

			Integer[] keys = new Integer[naiveTbl.size()];
			keys = naiveTbl.keySet().toArray(keys);
			Arrays.sort(keys);
			int inputSize = 0;
            int naiveRT = 0;
			int studentRT = 0; 
			for (int i = 0; i < keys.length; i++) {
				inputSize = keys[i];
				naiveRT = naiveTbl.get(inputSize);
				studentRT = studentTbl.get(inputSize);
				data[i+1][0] = inputSize;
				data[i+1][1] = ((double) naiveRT) / 1000;
				data[i+1][2] = ((double) studentRT) / 1000;
			}

			JFrame f = new JFrame();
		    f.setTitle("Runtime Plot (INF2B Coursework 1)");
		    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    GraphingData gd = new GraphingData(1.0, data, 1, 10, maxRatio, avgRatio, "plot.jpg");
		    f.add(gd);
		    f.setSize(800,800);
		    f.setLocation(200,200);
		    f.setVisible(true);
		    gd.save();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println(">> Illegal file format: Graph plotter has terminated unexpectedly.");
		}
	}
}
