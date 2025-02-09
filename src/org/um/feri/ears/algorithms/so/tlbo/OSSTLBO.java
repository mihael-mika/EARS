package org.um.feri.ears.algorithms.so.tlbo;

import org.um.feri.ears.algorithms.Algorithm;
import org.um.feri.ears.algorithms.AlgorithmInfo;
import org.um.feri.ears.algorithms.Author;
import org.um.feri.ears.algorithms.EnumAlgorithmParameters;
import org.um.feri.ears.problems.DoubleSolution;
import org.um.feri.ears.problems.StopCriterionException;
import org.um.feri.ears.problems.Task;
import org.um.feri.ears.util.Comparator.TaskComparator;
import org.um.feri.ears.util.Util;
import org.um.feri.ears.util.annotation.AlgorithmParameter;

import java.util.Arrays;

import static java.util.Arrays.stream;

public class OSSTLBO extends Algorithm {

    @AlgorithmParameter(name = "population size")
    private int popSize;

    private DoubleSolution best;
    private Task task;
    private DoubleSolution[] population;

    private int m = 0;

    public OSSTLBO() {
        this(20);
    }

    public OSSTLBO(int popSize) {

        this.popSize = popSize;
        //https://github.com/MohammadJomaa/OSS-TLBO
        au = new Author("miha", "miha.ravber@um.si");
        ai = new AlgorithmInfo("OSSTLBO", "Opposite Stopping Swarm Teaching–learning-Based Optimization",
                "@article{jomaa2014opposite,"
                        + "title={Opposite Stopping Swarm Teaching–learning-Based Optimization},"
                        + "author={Jomaa, Mohammad and Juneidi, Wassim },"
                        + "booktitle={Higher Institute for Applied Sciences and Technology}}"
        );
        ai.addParameter(EnumAlgorithmParameters.POP_SIZE, popSize + "");
    }

    @Override
    public DoubleSolution execute(Task taskProblem) throws StopCriterionException {
        task = taskProblem;

        initPopulation();

        while (!task.isStopCriterion()) {

            teacherPhase();
            learnerPhase();
            updatePopulation();
            task.incrementNumberOfIterations();
        }

        return best;
    }

    private void initPopulation() throws StopCriterionException {
        population = new DoubleSolution[popSize];

        best = task.getRandomEvaluatedSolution();
        population[0] = new DoubleSolution(best);
        for (int i = 1; i < popSize; i++) {
            if (task.isStopCriterion())
                break;
            population[i] = task.getRandomEvaluatedSolution();
            if (task.isFirstBetter(population[i], best)) {
                best = new DoubleSolution(population[i]);
            }
        }
        dynamicOppositeLearning();
    }

    private void dynamicOppositeLearning() throws StopCriterionException {
        for (int i = 0; i < popSize; i++) {
            double[] newX = new double[task.getNumberOfDimensions()];
            for (int n = 0; n < task.getNumberOfDimensions(); n++) {
                double X = population[i].getValue(n);
                double XO = (task.getLowerLimit(n) + task.getUpperLimit(n) - X);
                double XOD = X + Util.nextDouble() * (Util.nextDouble() * XO - X);
                if (XOD < task.getLowerLimit(n) || XOD > task.getUpperLimit(n)) {
                    XOD = Util.nextInt((int) (task.getUpperLimit(n) - task.getLowerLimit(n)));
                }
                newX[n] = XOD;
            }
            if (task.isStopCriterion())
                return;

            DoubleSolution newSolution = task.eval(newX);

            if (task.isFirstBetter(newSolution, population[i])) {
                population[i] = newSolution;
                if (task.isFirstBetter(newSolution, best)) {
                    best = new DoubleSolution(newSolution);
                }
            }
        }
    }

    private void teacherPhase() throws StopCriterionException {

        for (int i = 0; i < popSize; i++) {
            double[] newX = new double[task.getNumberOfDimensions()];
            double[] means = calculateXMean();
            for (int j = 0; j < task.getNumberOfDimensions(); j++) {
                double Tf = Util.nextInt(1, 3);
                double differenceMean = Util.nextDouble() * (best.getValue(j) - Tf * means[j]);
                double differenceTeacherStudent = Util.nextDouble() * (best.getValue(j) - population[i].getValue(j));
                double x = population[i].getValue(j) + differenceMean + differenceTeacherStudent;

                if (!task.isFeasible(x, j)) {
                    x = Util.nextDouble(task.getLowerLimit(j), task.getUpperLimit(j));
                }
                newX[j] = x;
            }

            if (task.isStopCriterion())
                return;

            DoubleSolution newSolution = task.eval(newX);

            if (task.isFirstBetter(newSolution, population[i])) {
                population[i] = newSolution;

                if (task.isFirstBetter(newSolution, best)) {
                    best = new DoubleSolution(newSolution);
                }
            }
        }
    }

    private void learnerPhase() throws StopCriterionException {

        for (int i = 0; i < popSize; i++) {
            DoubleSolution solution = population[i];
            DoubleSolution randomSolution = population[Util.nextInt(popSize)];
            while (randomSolution == solution)
                randomSolution = population[Util.nextInt(popSize)];

            double[] newX = new double[task.getNumberOfDimensions()];

            if (task.isFirstBetter(randomSolution, solution)) {
                for (int j = 0; j < task.getNumberOfDimensions(); j++) {
                    double x = solution.getValue(j) + Util.nextDouble() * (randomSolution.getValue(j) - (solution.getValue(j)));
                    if (!task.isFeasible(x, j)) {
                        x = Util.nextDouble(task.getLowerLimit(j), task.getUpperLimit(j));
                    }
                    newX[j] = x;
                }
            } else {
                for (int j = 0; j < task.getNumberOfDimensions(); j++) {
                    double x = solution.getValue(j) + Util.nextDouble() * (solution.getValue(j) - (randomSolution.getValue(j)));
                    if (!task.isFeasible(x, j)) {
                        x = Util.nextDouble(task.getLowerLimit(j), task.getUpperLimit(j));
                    }
                    newX[j] = x;
                }
            }

            if (task.isStopCriterion())
                return;

            DoubleSolution newSolution = task.eval(newX);

            if (task.isFirstBetter(newSolution, population[i])) {
                population[i] = newSolution;

                if (task.isFirstBetter(newSolution, best)) {
                    best = new DoubleSolution(newSolution);
                }
            }
        }
    }

    private void updatePopulation() throws StopCriterionException {
        Arrays.sort(population, new TaskComparator(task));
        m = m + 1;

        if (m == 20) {
            m = 0;
            dynamicOppositeLearning();
        }
    }

    private double[] calculateXMean() {
        double[] means = new double[task.getNumberOfDimensions()];

        for (DoubleSolution solution : population) {
            for (int j = 0; j < task.getNumberOfDimensions(); j++) {
                means[j] += solution.getValue(j);
            }
        }
        return stream(means).map(d -> d / popSize).toArray();
    }

    @Override
    public void resetToDefaultsBeforeNewRun() {

    }
}
