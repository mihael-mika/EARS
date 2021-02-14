package org.um.feri.ears.problems.unconstrained.cec2014;

import org.um.feri.ears.problems.unconstrained.cec.Functions;

public class F12 extends CEC2014 {

    public F12(int d) {
        super(d, 12);

        name = "F12 Katsuura function";
    }

    @Override
    public double eval(double[] x) {
        return Functions.katsuura_func(x, numberOfDimensions, OShift, M, 1, 1) + funcNum * 100.0;
    }
}
