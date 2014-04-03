package rt.integrators.heuristics;

import rt.util.FloatFunction;

public class MoarPowerHeuristic implements FloatFunction {

	private Float daPowah;

	public MoarPowerHeuristic(Float daPowah) {
		this.daPowah = daPowah;
	}
	
	@Override
	public float evaluate(float f) {
		return (float) Math.pow(f, daPowah);
	}

}
