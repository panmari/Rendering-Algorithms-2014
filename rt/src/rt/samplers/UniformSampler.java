package rt.samplers;

import rt.Sampler;

public class UniformSampler implements Sampler {

	public UniformSampler() {
		throw new RuntimeException("not implemented yet");
	}
	
	@Override
	public float[][] makeSamples(int n, int d) {
		float dist = 1.f/n;
		float samples[][] = new float[n][d];
		for(int i=0; i<n; i++)
		{
			float sample = dist*(i+1);
			
			for(int j=0; j<d; j++)
			{
				samples[i][j] = sample + dist*(i + 1);
			}
		}

		return samples;
	}

}
