package rt.integrators;

import rt.Integrator;
import rt.IntegratorFactory;
import rt.Scene;

public class WhittedIntegratorFactory implements IntegratorFactory {

	@Override
	public Integrator make(Scene scene) {
		return new WhittedIntegrator(scene);
	}

	@Override
	public void prepareScene(Scene scene) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finish(Scene scene) {}
}
