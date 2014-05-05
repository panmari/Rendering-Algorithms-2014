package rt.integrators;

import rt.Integrator;
import rt.IntegratorFactory;
import rt.Scene;

/**
 * Makes a {@link PointLightIntegrator}.
 */
public class AreaLightIntegratorFactory implements IntegratorFactory {

	public Integrator make(Scene scene) {
		return new AreaLightIntegrator(scene);
	}

	public void prepareScene(Scene scene) {
		// not needed for point lights
	}

	@Override
	public void finish(Scene scene) {}

}
