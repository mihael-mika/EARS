package org.um.feri.ears.algorithms.so.lshade;

import org.apache.commons.lang3.ArrayUtils;
import org.um.feri.ears.algorithms.Algorithm;
import org.um.feri.ears.algorithms.AlgorithmInfo;
import org.um.feri.ears.algorithms.Author;
import org.um.feri.ears.algorithms.EnumAlgorithmParameters;
import org.um.feri.ears.problems.DoubleSolution;
import org.um.feri.ears.problems.StopCriterionException;
import org.um.feri.ears.problems.Task;
import org.um.feri.ears.util.Util;
import org.um.feri.ears.util.annotation.AlgorithmParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LSHADE extends Algorithm {

    @AlgorithmParameter(name = "population size")
    private int popSize;
    @AlgorithmParameter(name = "archive size")
    private int arcSize;
    @AlgorithmParameter(name = "population rate", min = "15", max = "25")
    private int popRate;
    @AlgorithmParameter(name = "archive rate", min = "1.0", max = "3.0")
    private double arcRate;
    @AlgorithmParameter(name = "current best mutation", description = "value for current-to-pbest/1 mutation", min = "0.05", max = "0.15")
    private double pBestRate;
    @AlgorithmParameter(name = "historical memory size", description = "H", min = "2", max = "10")
    private int memorySize;

    private int reductionIndNum;

    private DoubleSolution[] population;
    private DoubleSolution[] offspringPopulation;
    private DoubleSolution best;
    private Task task;

    boolean initPopSizeSet = false;
    int initialPopSize;

    public LSHADE(int initialPopSize) {
        this();
        this.initialPopSize = initialPopSize;
        initPopSizeSet = true;
        ai.addParameter(EnumAlgorithmParameters.POP_SIZE, popSize + "");
    }

    public LSHADE() {
        arcRate = 2.6;
        popRate = 18;
        pBestRate = 0.11; //p value for current-to-pbest/1 mutation
        memorySize = 6; //H historical memory size

        au = new Author("miha", "miha.ravber@um.si");
        ai = new AlgorithmInfo("LSHADE", "Linear Population Size Reduction Success-History based Adaptive DE",
                "@article{tanabe2014improving,"
                        + "title={Improving the search performance of SHADE using linear population size reduction},"
                        + "author={Tanabe, Ryoji and Fukunaga, Alex S},"
                        + "booktitle={2014 IEEE congress on evolutionary computation (CEC)},"
                        + "pages={1658--1665},"
                        + "year={2014},"
                        + "organization={IEEE}}"
        );
    }

    @Override
    public DoubleSolution execute(Task taskProblem) throws StopCriterionException {
        task = taskProblem;
        if(initPopSizeSet) // if initial population size set
            popSize = initialPopSize;
        else
            popSize = Math.round(task.getNumberOfDimensions() * popRate);

        arcSize = (int) Math.round(popSize * arcRate);

        initPopulation();

        //for external archive
        int arcIndCount = 0;
        int randomSelectedArcInd;
        DoubleSolution[] archive = new DoubleSolution[arcSize];

        int numSuccessParams;
        List<Double> successSf = new ArrayList<>();
        List<Double> successCr = new ArrayList<>();
        List<Double> difFitness = new ArrayList<>();

        // the contents of M_f and M_cr are all initialiezed 0.5
        double[] memorySf = new double[memorySize];
        double[] memoryCr = new double[memorySize];

        Arrays.fill(memorySf, 0.5);
        Arrays.fill(memoryCr, 0.5);

        double tempSumSf;
        double tempSumCr;
        double sum;
        double weight;

        //memory index counter
        int memoryPos = 0;

        //for new parameters sampling
        double muSf;
        double muCr;
        int randomSelectedPeriod;
        double[] popSf = new double[popSize];
        double[] popCr = new double[popSize];

        //for current-to-pbest/1
        int pBestInd;
        int pNum = (int) Math.round(popSize * pBestRate);
        int[] sortedArray = new int[popSize];

        // for linear population size reduction
        int maxPopSize = popSize;
        int minPopSize = 4;
        int planPopSize;

        double[] offspring = new double[task.getNumberOfDimensions()];

        //main loop
        while (!task.isStopCriterion()) {

            for (int i = 0; i < popSize; i++) {
                sortedArray[i] = i;
            }
            sortedArray = getSortedArray();
            //sortIndexWithQuickSort(temp_fit[0], 0, pop_size - 1, sorted_array);

            for (int target = 0; target < popSize; target++) {
                //In each generation, CR_i and F_i used by each individual x_i are generated by first selecting an index r_i randomly from [1, H]
                randomSelectedPeriod = Util.nextInt(0, memorySize); //(int)(Configuration.rand.getFloat() % memory_size);
                muSf = memorySf[randomSelectedPeriod];
                muCr = memoryCr[randomSelectedPeriod];

                //generate CR_i and repair its value
                if (muCr == -1) {
                    popCr[target] = 0;
                } else {
                    popCr[target] = Util.nextGaussian(muCr, 0.1);
                    if (popCr[target] > 1) {
                        popCr[target] = 1;
                    } else if (popCr[target] < 0) {
                        popCr[target] = 0;
                    }
                }

                //generate F_i and repair its value
                do {
                    popSf[target] = Util.cauchyrnd(muSf, 0.1);
                } while (popSf[target] <= 0);

                if (popSf[target] > 1) {
                    popSf[target] = 1;
                }

                //p-best individual is randomly selected from the top pop_size *  p_i members
                //p_best_ind = sorted_array[(int)(Configuration.rand.getFloat() % p_num)];
                pBestInd = sortedArray[Util.nextInt(0, pNum)];

                int r1, r2;
                double crossRate = popCr[target];
                double scalingFactor = popSf[target];
                do {
                    r1 = Util.nextInt(0, popSize);  //Math.round(Configuration.rand.getFloat() % pop_size);
                } while (r1 == target);
                do {
                    r2 = Util.nextInt(0, popSize + arcIndCount);  //Math.round(Configuration.rand.getFloat() % (pop_size + arc_ind_count));
                } while ((r2 == target) || (r2 == r1));

                int random_variable = Util.nextInt(0, task.getNumberOfDimensions());  //Math.round(Configuration.rand.getFloat() % task.getNumberOfDimensions());

                DoubleSolution solution;
                if (r2 >= popSize) {
                    r2 -= popSize;
                    solution = archive[r2];
                } else
                    solution = population[r2];

                for (int i = 0; i < task.getNumberOfDimensions(); i++) {
                    if ((Util.nextFloat() < crossRate) || (i == random_variable)) {
                        offspring[i] = population[target].getValue(i) + scalingFactor * (population[pBestInd].getValue(i) - population[target].getValue(i)) + scalingFactor * (population[r1].getValue(i) - solution.getValue(i));
                    } else {
                        offspring[i] = population[target].getValue(i);
                    }
                }

                //If the mutant vector violates bounds, the bound handling method is applied
                modifySolutionWithParentMedium(offspring, population[target]);

                if (task.isStopCriterion())
                    break;
                // evaluate the children's fitness value
                DoubleSolution newSolution = task.eval(offspring);
                offspringPopulation[target] = newSolution;
                //update the best solution and check the current number of fitness evaluations
                if (task.isFirstBetter(newSolution, best))
                    best = new DoubleSolution(newSolution);
            }

            //generation alternation
            for (int i = 0; i < popSize; i++) {
                if (offspringPopulation[i].getEval() == population[i].getEval()) {
                    population[i] = offspringPopulation[i];
                } else if (task.isFirstBetter(offspringPopulation[i], population[i])) {
                    difFitness.add(Math.abs(population[i].getEval() - offspringPopulation[i].getEval()));

                    population[i] = offspringPopulation[i];

                    //successful parameters are preserved in S_F and S_CR
                    successSf.add(popSf[i]);
                    successCr.add(popCr[i]);

                    //parent vectors x_i which were worse than the trial vectors u_i are preserved
                    if (arcSize > 1) {
                        if (arcIndCount < arcSize) {
                            archive[arcIndCount] = new DoubleSolution(population[i]);
                            arcIndCount++;
                        }
                        //Whenever the size of the archive exceeds, randomly selected elements are deleted to make space for the newly inserted elements
                        else {
                            randomSelectedArcInd = Util.nextInt(0, arcSize);  //(int)(Configuration.rand.getFloat() % arc_size);
                            for (int j = 0; j < task.getNumberOfDimensions(); j++) {
                                archive[randomSelectedArcInd] = new DoubleSolution(population[i]);
                            }
                        }
                    }
                }
            }

            numSuccessParams = successSf.size();

            // if numeber of successful parameters > 0, historical memories are updated
            if (numSuccessParams > 0) {
                memorySf[memoryPos] = 0;
                memoryCr[memoryPos] = 0;
                tempSumSf = 0;
                tempSumCr = 0;
                sum = 0;

                for (int i = 0; i < numSuccessParams; i++) {
                    sum += difFitness.get(i);
                }

                //weighted lehmer mean
                for (int i = 0; i < numSuccessParams; i++) {
                    weight = difFitness.get(i) / sum;

                    memorySf[memoryPos] = memorySf[memoryPos] + weight * successSf.get(i) * successSf.get(i);
                    tempSumSf += weight * successSf.get(i);

                    memoryCr[memoryPos] = memoryCr[memoryPos] + weight * successCr.get(i) * successCr.get(i);
                    tempSumCr += weight * successCr.get(i);
                }

                memorySf[memoryPos] = memorySf[memoryPos] / tempSumSf;

                if (tempSumCr == 0 || memoryCr[memoryPos] == -1) {
                    memoryCr[memoryPos] = -1;
                } else {
                    memoryCr[memoryPos] = memoryCr[memoryPos] / tempSumCr;
                }

                //increment the counter
                memoryPos++;
                if (memoryPos >= memorySize) {
                    memoryPos = 0;
                }

                //clear out the S_F, S_CR and delta fitness
                successSf.clear();
                successCr.clear();
                difFitness.clear();
            }

            // calculate the population size in the next generation
            planPopSize = (int) Math.round((((minPopSize - maxPopSize) / (double) task.getMaxEvaluations()) * task.getNumberOfEvaluations()) + maxPopSize);

            if (popSize > planPopSize) {
                reductionIndNum = popSize - planPopSize;
                if (popSize - reductionIndNum < minPopSize) {
                    reductionIndNum = popSize - minPopSize;
                }

                reducePopulationWithSort();

                // resize the archive size
                arcSize = (int) (popSize * arcRate);
                if (arcIndCount > arcSize) {
                    arcIndCount = arcSize;
                }

                // resize the number of p-best individuals
                pNum = (int) Math.round(popSize * pBestRate);
                if (pNum <= 1) {
                    pNum = 2;
                }
            }
            task.incrementNumberOfIterations();
        }
        return best;
    }

    private int[] getSortedArray() {
        int[] indices = new int[population.length];
        for (int i = 0; i < indices.length; i++)
            indices[i] = i;

        for (int i = 0; i < indices.length; i++) {
            for (int j = i + 1; j < indices.length; j++) {
                if (task.isFirstBetter(population[indices[j]], population[indices[i]])) {
                    int tmp = indices[i];
                    indices[i] = indices[j];
                    indices[j] = tmp;
                }
            }
        }
        return indices;
    }

    private void initPopulation() throws StopCriterionException {
        population = new DoubleSolution[popSize];
        offspringPopulation = new DoubleSolution[popSize];
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
    }

    private void reducePopulationWithSort() {
        int worst_ind;

        for (int i = 0; i < reductionIndNum; i++) {
            worst_ind = 0;
            for (int j = 1; j < popSize; j++) {
                if (task.isFirstBetter(population[worst_ind], population[j])) {
                    worst_ind = j;
                }
            }
            population = ArrayUtils.removeElement(population, worst_ind);
            popSize--;
        }
    }

    private void modifySolutionWithParentMedium(double[] child, DoubleSolution parent) {
        double[] lowerLimit = task.getLowerLimit();
        double[] upperLimit = task.getUpperLimit();

        for (int i = 0; i < task.getNumberOfDimensions(); i++) {
            if (child[i] < lowerLimit[i]) {
                child[i] = (lowerLimit[i] + parent.getValue(i)) / 2.0;
            } else if (child[i] > upperLimit[i]) {
                child[i] = (upperLimit[i] + parent.getValue(i)) / 2.0;
            }
        }
    }

    @Override
    public void resetToDefaultsBeforeNewRun() {

    }
}
