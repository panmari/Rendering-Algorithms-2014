package rt.samplers;

import rt.Sampler;

public class UniformSampler implements Sampler {

	/**
	 * Expects n to be a square number, d to be 2.
	 * If not, n will be rounded up to the next square number
	 */
	@Override
	public float[][] makeSamples(int n, int d) {
		int k = (int) Math.ceil(Math.sqrt(n));
		float dist = 1.f/k;
		float offset = 1.f/(k*2);
		float samples[][] = new float[k*k][d];
		for (int i = 0; i < k*k; i++) {
			samples[i][0] = offset + dist*(i % k);
			samples[i][1] = offset + dist*(i / k); //integer division here!
		}

		return samples;
	}

}
