package rt.integrators.heuristics;

import rt.util.FloatFunction;

public class PowerHeuristic implements FloatFunction {

	@Override
	public float evaluate(float f) {
		return f*f;
	}

}
