package rt.integrators.heuristics;

import util.FloatFunction;

public class PowerHeuristic implements FloatFunction {

	@Override
	public float evaluate(float f) {
		return f*f;
	}

}
