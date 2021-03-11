package org.um.feri.ears.algorithms.so.pso;

import org.um.feri.ears.algorithms.Algorithm;
import org.um.feri.ears.algorithms.AlgorithmInfo;
import org.um.feri.ears.algorithms.Author;
import org.um.feri.ears.algorithms.EnumAlgorithmParameters;
import org.um.feri.ears.problems.DoubleSolution;
import org.um.feri.ears.problems.StopCriterionException;
import org.um.feri.ears.problems.Task;
import org.um.feri.ears.util.Util;

import java.util.ArrayList;

public class PSO extends Algorithm {

    private int popSize;
    private ArrayList<PsoSolution> population;
    private DoubleSolution gBest; //global best
    // C1- cognitive coefficient, C2 - social coefficient, omega- inertia weight
    private double omega, phiG, phiP;
    private Task task;

    public PSO() {
        this(10, 0.7, 2, 2);
    }

    public PSO(int popSize, double om, double c1, double c2) {
        super();
        this.popSize = popSize;
        this.omega = om;
        this.phiP = c1;
        this.phiG = c2;
        setDebug(debug);  //EARS prints some debug info
        ai = new AlgorithmInfo("PSO", "Particle Swarm Optimization", "");
        ai.addParameter(EnumAlgorithmParameters.POP_SIZE, popSize + "");
        ai.addParameter(EnumAlgorithmParameters.C1, c1 + "");
        ai.addParameter(EnumAlgorithmParameters.C2, c2 + "");
        ai.addParameter(EnumAlgorithmParameters.UNNAMED1, om + "");
        //ai.addParameter(EnumAlgorithmParameters., F + "");
        au = new Author("Matej", "matej.crepinsek@um.si");
    }

    @Override
    public DoubleSolution execute(Task taskProblem) throws StopCriterionException {
        task = taskProblem;
        initPopulation();
        double[] velocity;
        while (!task.isStopCriterion()) {
            for (int i = 0; i < popSize; i++) {
                velocity = new double[task.getNumberOfDimensions()];
                // r*vec(x) double r = Util.rnd.nextDouble();
                for (int d = 0; d < task.getNumberOfDimensions(); d++) {
                    velocity[d] = omega * (
                            population.get(i).velocity[d]) +
                            phiP * Util.rnd.nextDouble() * (population.get(i).pBest.getValue(d) - population.get(i).getValue(d)) +
                            phiG * Util.rnd.nextDouble() * (gBest.getValue(d) - population.get(i).getValue(d));
                    //if (v[d]>(taskProblem.getIntervalLength()[d])) v[d]=taskProblem.getIntervalLength()[d];
                    //if (v[d]<(taskProblem.getIntervalLength()[d])) v[d]=-taskProblem.getIntervalLength()[d];
                }

                population.get(i).updatePosition(velocity);
                task.setFeasible(population.get(i));
                task.eval(population.get(i));
                if (task.isFirstBetter(population.get(i), population.get(i).pBest))
                    population.get(i).pBest = new PsoSolution(population.get(i));

                if (task.isFirstBetter(population.get(i), gBest)) gBest = new PsoSolution(population.get(i));
                if (task.isStopCriterion()) break;
            }
            task.incrementNumberOfIterations();
        }
        return gBest;
    }

    private void initPopulation() throws StopCriterionException {
        population = new ArrayList<>();
        for (int i = 0; i < popSize; i++) {
            if (task.isStopCriterion()) break;
            population.add(new PsoSolution(task));
            if (i == 0) gBest = population.get(0);
            else if (task.isFirstBetter(population.get(i), gBest)) gBest = new PsoSolution(population.get(i));
        }
    }

    @Override
    public void resetToDefaultsBeforeNewRun() {

    }
}