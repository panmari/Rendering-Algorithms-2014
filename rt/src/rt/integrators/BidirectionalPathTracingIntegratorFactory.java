package rt.integrators;

import rt.Integrator;
import rt.IntegratorFactory;
import rt.Scene;

public class BidirectionalPathTracingIntegratorFactory implements IntegratorFactory {

	@Override
	public Integrator make(Scene scene) {
		return new PathTracingIntegrator(scene);
	}

	@Override
	public void prepareScene(Scene scene) {
		// NOTHING
	}

}
