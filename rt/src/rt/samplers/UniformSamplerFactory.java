package rt.samplers;

import rt.Sampler;
import rt.SamplerFactory;

public class UniformSamplerFactory implements SamplerFactory {

	@Override
	public Sampler make() {
		return new UniformSampler();
	}

}
