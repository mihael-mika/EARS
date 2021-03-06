package org.um.feri.ears.examples;

import org.um.feri.ears.algorithms.so.de.DEAlgorithm;
import org.um.feri.ears.algorithms.so.es.ES1p1sAlgorithm;
import org.um.feri.ears.algorithms.so.random.RandomWalkAMAlgorithm;
import org.um.feri.ears.algorithms.so.random.RandomWalkAlgorithm;
import org.um.feri.ears.algorithms.so.tlbo.TLBOAlgorithm;
import org.um.feri.ears.benchmark.Benchmark;
import org.um.feri.ears.benchmark.RPUOed2Benchmark;
import org.um.feri.ears.rating.Rating;
import org.um.feri.ears.util.Util;

public class MainBenchMarkTest {

    public static void main(String[] args) {
        Util.rnd.setSeed(System.currentTimeMillis());
        Benchmark.printInfo = true; //prints one on one results
        RunMainBestAlgSettings rbs = new RunMainBestAlgSettings(true, false, new RPUOed2Benchmark());
        rbs.addAlgorithm(new RandomWalkAlgorithm(), new Rating(1500, 350, 0.06));
        rbs.addAlgorithm(new RandomWalkAMAlgorithm(), new Rating(1500, 350, 0.06));
        rbs.addAlgorithm(new ES1p1sAlgorithm(), new Rating(1500, 350, 0.06));
        //rbs.addAlgorithm(new SwarmAlgorithm(),new Rating(1500, 350, 0.06));  
        //rbs.addAlgorithm(new BeeColonyAlgorithm(),new Rating(1500, 350, 0.06));  
        rbs.addAlgorithm(new TLBOAlgorithm(), new Rating(1500, 350, 0.06));
        for (DEAlgorithm.Strategy strategy : DEAlgorithm.Strategy.values())
            rbs.addAlgorithm(new DEAlgorithm(strategy, 20), new Rating(1500, 350, 0.06));
        rbs.run(30);
        System.out.println(rbs);

    }
}
