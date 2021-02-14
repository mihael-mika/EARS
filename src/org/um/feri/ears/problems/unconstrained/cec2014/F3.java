package org.um.feri.ears.problems.unconstrained.cec2014;

import org.um.feri.ears.problems.unconstrained.cec.Functions;

public class F3 extends CEC2014 {
	
	public F3(int d) {
		super(d,3);

		name = "F03 Discus Function";
	}

	@Override
	public double eval(double[] x) {
		return Functions.discus_func(x,numberOfDimensions,OShift,M,1,1) + funcNum * 100.0;
	}
}