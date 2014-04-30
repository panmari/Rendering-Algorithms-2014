package rt.integrators;

import rt.Integrator;
import rt.IntegratorFactory;
import rt.Scene;

public class BidirectionalPathTracingIntegratorFactory implements IntegratorFactory {

	public BidirectionalPathTracingIntegratorFactory(Scene scene) {
		// TODO do something useful with scene, make light buffer thingy
	}

	@Override
	public Integrator make(Scene scene) {
		return new BidirectionalPathTracingIntegrator(scene);
	}

	@Override
	public void prepareScene(Scene scene) {
		// NOTHING
	}

}
