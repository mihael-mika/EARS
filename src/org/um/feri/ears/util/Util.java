package org.um.feri.ears.util;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.um.feri.ears.problems.moo.MOSolutionBase;
import org.um.feri.ears.problems.moo.ParetoSolution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Util {
    public static DecimalFormat df0 = new DecimalFormat("####.##");
    public static DecimalFormat df01 = new DecimalFormat("####.####");
    public static DecimalFormat df1 = new DecimalFormat("#,###.#");
    public static DecimalFormat df = new DecimalFormat("#,###.##");
    public static DecimalFormat df3 = new DecimalFormat("#,###.###");
    public static DecimalFormat df6 = new DecimalFormat("#,###.######");
    public static DecimalFormat dfc1 = new DecimalFormat("#,##0.#######E0");
    public static DecimalFormat dfc2 = new DecimalFormat("#,##0.##E0");
    public static DecimalFormat dfcshort = new DecimalFormat("0.##E0");
    public static DecimalFormat intf = new DecimalFormat("###,###,###");
    private static long initialSeed = 316227711;
    public static Random rnd = new MersenneTwister(initialSeed);
    static final String JSON_DIR = "Cache\\Pareto_Cache_%s.json";

    public static double roundDouble3(double r) {
        return roundDouble(r, 3);
    }

    public static double roundDouble(double r, int dec) {
        r = Math.round(r * Math.pow(10, dec));
        return r / Math.pow(10, dec);
    }

    public static String arrayToString(List<Double> d) {
        String s = "";
        for (int i = 0; i < d.size(); i++) {
            s = s + df01.format(d.get(i));
            if (i < d.size() - 1)
                s = s + ", ";
        }
        return s;
    }

    public static String arrayToString(double[] d) {
        String s = "";
        for (int i = 0; i < d.length; i++) {
            s = s + df01.format(d[i]);
            if (i < d.length - 1)
                s = s + ", ";
        }
        return s;
    }

    public static <T extends Number> void addParetoToJSON(String listID, String benchmark, String file, ParetoSolution<T> best) {
        ParetoSolutionCache cache = new ParetoSolutionCache();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        try {
            BufferedReader br = new BufferedReader(new FileReader(String.format(JSON_DIR, file + "_" + benchmark)));
            cache = gson.fromJson(br, ParetoSolutionCache.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (cache == null)
            cache = new ParetoSolutionCache();

        if (cache.data.containsKey(listID)) {
            if (cache.data.get(listID).size() >= cache.limit) {
                return;
            }
            //List<List<double[]>> solutions = cache.data.get(listID);
            List<ParetoWithEval> pareto = cache.data.get(listID);

            ParetoWithEval paretoList = new ParetoWithEval();

            for (MOSolutionBase<T> s : best.solutions) {
                double[] objectives = s.getObjectives();
                //check for NaN
                for (int i = 0; i < objectives.length; i++) {
                    if (Double.isNaN(objectives[i])) {
                        System.err.println("Pareto contains NaN!");
                        return;
                    }
                }

                paretoList.pareto.add(objectives);
            }

            HashMap<String, Double> allQI = best.getAllQiEval();
            for (Entry<String, Double> qi : allQI.entrySet()) {
                if (Double.isNaN(qi.getValue())) {
                    System.out.println("QI value is NaN for: " + qi.getKey());
                    allQI.put(qi.getKey(), Double.MAX_VALUE);
                }
            }

            paretoList.qiEval = allQI;
            pareto.add(paretoList);
            cache.data.put(listID, pareto);

        } else {
            //List<List<double[]>> solutions = new ArrayList<List<double[]>>();
            List<ParetoWithEval> pareto = new ArrayList<ParetoWithEval>();

            ParetoWithEval paretoList = new ParetoWithEval();

            for (MOSolutionBase<T> s : best.solutions) {
                double[] objectives = s.getObjectives();
                //check for NaN
                for (int i = 0; i < objectives.length; i++) {
                    if (Double.isNaN(objectives[i])) {
                        System.err.println("Pareto contains NaN!");
                        return;
                    }
                }

                paretoList.pareto.add(objectives);
            }

            HashMap<String, Double> allQI = best.getAllQiEval();
            for (Entry<String, Double> qi : allQI.entrySet()) {
                if (Double.isNaN(qi.getValue())) {
                    System.out.println("QI value is NaN for: " + qi.getKey());
                    allQI.put(qi.getKey(), Double.MAX_VALUE);
                }
            }

            paretoList.qiEval = allQI;
            pareto.add(paretoList);
            cache.data.put(listID, pareto);
        }

        try (Writer writer = new FileWriter(String.format(JSON_DIR, file + "_" + benchmark))) {
            gson.toJson(cache, writer);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ww");
        }
    }

    public static ParetoSolutionCache readParetoListFromJSON(String file, String benchmark) {
        System.out.println("Loading cache for algorithm: " + file + " and benchmark: " + benchmark);
        ParetoSolutionCache cache = new ParetoSolutionCache();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        try {
            BufferedReader br = new BufferedReader(new FileReader(String.format(JSON_DIR, file + "_" + benchmark)));
            cache = gson.fromJson(br, ParetoSolutionCache.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cache;
    }
	
/*
	public static void printPopulationCompare(double d[][], double e[],
			Problem p, int gen) {
		System.out.println("Generation:" + gen);
		for (int i = 0; i < d.length; i++) {
			System.out.println(arrayToString(d[i]) + " eval:" + e[i]
					+ " real: " + p.eval(d[i]) + " constrain: "
					+ p.constrainsEvaluations(d[i]));
			if (e[i] != p.eval(d[i]))
				System.out.println("ERROR!!!");
		}
	}

	public static void printPopulation(double d[][], Problem p, int gen) {
		System.out.println("Generation:" + gen);
		for (int i = 0; i < d.length; i++) {
			System.out.println(arrayToString(d[i]) + " eval:" + p.eval(d[i])
					+ " constrain: " + p.constrainsEvaluations(d[i]));
		}
	}*/

    public static double divide(double a, double b) {
        if (b == 0)
            return 0;
        return a / b;
    }

    /**
     *
     * @param upperBound
     * @return the next random, uniformly distributed {@code int} value between
     *         {@code 0} (inclusive) and {@code upperBound} (exclusive).
     */
    public static int nextInt(int upperBound) {
        return rnd.nextInt(upperBound);
    }

    public static int nextInt() {
        return rnd.nextInt();
    }

    /**
     * Returns the next random, uniformly distributed {@code int} value between
     * {@code 0} (inclusive) and {@code n} (exclusive).
     *
     * @return the next random, uniformly distributed {@code int} value between
     *         {@code lowerBound} (inclusive) and {@code upperBound} (exclusive).
     */
    public static int nextInt(int lowerBound, int upperBound) {
        return lowerBound + rnd.nextInt(upperBound - lowerBound);
    }

    public static double cauchyrnd(double a, double b) {
        return a + b * Math.tan(Math.PI * (nextFloat() - 0.5));
    }

    public static double nextDouble() {
        return rnd.nextDouble();
    }

    /**
     * Returns the next random, uniformly distributed {@code double} value
     * between {@code lowerBound} and {@code upperBound}.
     *
     * @return the next random, uniformly distributed {@code double} value
     *         between {@code lowerBound} and {@code upperBound}
     */
    public static double nextDouble(double lowerBound, double upperBound) {
        return lowerBound + rnd.nextDouble() * (upperBound - lowerBound);
    }

    public static double nextFloat() {
        return rnd.nextFloat();
    }

    /**
     * Shuffles the elements of the specified array using the same algorithm as
     * {@link Collections#shuffle}.
     *
     * @param <T> the type of element stored in the array
     * @param array the array to be shuffled
     */
    public static <T> void shuffle(T[] array) {
        for (int i = array.length - 1; i >= 1; i--) {
            int j = nextInt(i + 1);

            if (i != j) {
                T temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
    }


    /**
     * Shuffles the elements of the specified list using the same algorithm as
     * {@link Collections#shuffle}.
     *
     * @param <T> the type of element stored in the List
     * @param list the list to be shuffled
     */
    public static <T> void shuffle(List<T> list) {
        for (int i = list.size() - 1; i >= 1; i--) {
            int j = nextInt(i + 1);

            if (i != j) {
                T temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }
    }


    /**
     * Generates a random permutation array between 0 and (length - 1)
     *
     * @param length the length of the array
     * @return a random permutation array between 0 and (length - 1)
     */
    public static int[] randomPermutation(int length) {
        int[] permutation = new int[length];

        for (int i = 0; i < length; i++) {
            permutation[i] = i;
        }

        Util.shuffle(permutation);

        return permutation;
    }

    /**
     * Shuffles the elements of the specified array using the same algorithm as
     * {@link Collections#shuffle}.
     *
     * @param array the array to be shuffled
     */
    public static void shuffle(int[] array) {
        for (int i = array.length - 1; i >= 1; i--) {
            int j = nextInt(i + 1);

            if (i != j) {
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
    }

    /**
     * Returns a randomly selected item from the specified list.
     *
     * @param <T> the type of the elements stored in the list
     * @param list the list from which the item is randomly selected
     * @return a randomly selected item from the specified list
     */
    public static <T> T nextItem(List<T> list) {
        return list.get(Util.nextInt(list.size()));
    }

    /**
     * Returns the next random, uniformly distributed {@code boolean} value.
     *
     * @return the next random, uniformly distributed {@code boolean} value.
     */
    public static boolean nextBoolean() {
        return rnd.nextBoolean();
    }

    public static double nextGaussian() {
        return rnd.nextGaussian();
    }

	/**
	 * Returns a random real number from a Gaussian distribution with mean &mu;
	 * and standard deviation &sigma;.
	 *
	 * @param  mu the mean
	 * @param  sigma the standard deviation
	 * @return a real number distributed according to the Gaussian distribution
	 *         with mean <tt>mu</tt> and standard deviation <tt>sigma</tt>
	 */
	public static double nextGaussian(double mu, double sigma) {
		return mu + sigma * rnd.nextGaussian();
	}

    public static void writeToFile(String fileLocation, String data) {

        File file = new File(fileLocation);
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)))) {
            bw.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFromFile(String fileLocation) {
	    StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileLocation))) {
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static double max(double... args) {
	    double maxValue = args[0];
        for (double arg : args) {
            maxValue = Math.max(maxValue, arg);
        }
        return maxValue;
    }
}
