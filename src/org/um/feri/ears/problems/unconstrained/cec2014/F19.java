package org.um.feri.ears.problems.unconstrained.cec2014;

import java.util.List;

import org.um.feri.ears.problems.unconstrained.cec.Functions;

public class F19 extends CEC2014 {
	
	public F19(int d) {
		super(d,19);

		name = "F19 Hybrid Function 3";
	}

	@Override
	public double eval(List<Double> ds) {
		double F;
		F = Functions.hf03(ds,numberOfDimensions,OShift,M,SS,1,1);
		F+=1900.0;
		return F;
	}
	
	public double eval(double x[]) {
		double F;
		F = Functions.hf03(x,numberOfDimensions,OShift,M,SS,1,1);
		F+=1900.0;
		return F;
	}

}