package rt.integrators;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Integrator;
import rt.Intersectable;
import rt.LightGeometry;
import rt.LightList;
import rt.Material.ShadingSample;
import rt.Ray;
import rt.Sampler;
import rt.Scene;
import rt.Spectrum;
import rt.samplers.RandomSampler;
import util.StaticVecmath;

public class AreaLightIntegrator implements Integrator {

	LightList lightList;
	Intersectable root;
	private Sampler sampler;
	
	public AreaLightIntegrator(Scene scene)
	{
		this.lightList = scene.getLightList();
		this.root = scene.getIntersectable();
		this.sampler = new RandomSampler();
	}

	/**
	 * Basic integrator that simply iterates over the light sources and accumulates
	 * their contributions. No shadow testing, reflection, refraction, or 
	 * area light sources, etc. supported.
	 */
	public Spectrum integrate(Ray r) {

		HitRecord hitRecord = root.intersect(r);
		if(hitRecord != null)
		{
			Spectrum emission = hitRecord.material.evaluateEmission(hitRecord, hitRecord.w);
			if (emission != null) // hit light => return emission of light directly
				return emission;
			
			LightGeometry randomLightSource = lightList.getRandomLight();
			Spectrum outgoing = new Spectrum();
				
			WeightedSpectrum lightSampledSpectrum = sampleLight(hitRecord, randomLightSource);
			lightSampledSpectrum.p *= 1f/lightList.size(); // adapt probability to hit exactly that light
			lightSampledSpectrum.mult(lightList.size()); // also adapt spectrum bc of changed probability
			
			WeightedSpectrum brdfSampledSpectrum = sampleBRDF(hitRecord);
						
			WeightedSpectrum[] specs = new WeightedSpectrum[]{lightSampledSpectrum, brdfSampledSpectrum};
			float p_sum = 0;
			for (WeightedSpectrum s: specs) {
				// balance heuristic
				p_sum += s.p;
				// power heuristic:
				//p_sum += s.p*s.p;
			}
			outgoing = new Spectrum();
			for (WeightedSpectrum s: specs) {
				// balance heuristic -> p/p_sum
				float weight = s.p/p_sum;
				// power heuristic -> p*p/p_sum_squares
				// s.mult(s.p*s.p/p_sum);
				weight = Float.isNaN(weight) ? 0 : weight;
				s.mult(weight);
				outgoing.add(s);
			}
			return outgoing;
			
		} else return new Spectrum();	
			
	
	}
	
	private WeightedSpectrum sampleBRDF(HitRecord hitRecord) {
		ShadingSample shadingSample = hitRecord.material.getShadingSample(hitRecord, this.sampler.makeSamples(1, 2)[0]);
		//TODO: every material should return a shading sample
		if (shadingSample != null) { 
			Ray shadingSampleRay = new Ray(hitRecord.position, shadingSample.w, 0, true);
			HitRecord shadingSampleHit = root.intersect(shadingSampleRay);
			
			if (shadingSampleHit != null) {
				assert StaticVecmath.dist2(shadingSample.w, StaticVecmath.negate(shadingSampleHit.w)) < 1e-5f;
				
				Spectrum emission = shadingSampleHit.material.evaluateEmission(shadingSampleHit, shadingSampleHit.w);
			
				float cosTheta_i = hitRecord.normal.dot(hitRecord.w);
				assert cosTheta_i >= 0: "went into strange direction: " + cosTheta_i;

				// compute area probability for this ray
				float areaProbablity = shadingSample.p * Math.abs(cosTheta_i); // abs should not matter
				areaProbablity /= StaticVecmath.dist2(hitRecord.position, shadingSampleHit.position);

				if (emission != null && shadingSampleHit.normal.dot(shadingSampleHit.w) > 0) { // hit light from ahead

					emission.mult(shadingSample.brdf);
					emission.mult(cosTheta_i/shadingSample.p);
					
					return new WeightedSpectrum(emission, areaProbablity);
				} else //didn't hit light -> stay dark
					return new WeightedSpectrum(new Spectrum(0,0,0), areaProbablity);
			} else // return black with probablity 0, since infinitely far away hit
				return new WeightedSpectrum(new Spectrum(0,0,0), 0);
		} else //this should not happen and is only here for lazyness
			return new WeightedSpectrum(new Spectrum(1,0,0), 1);
	}
	
	private WeightedSpectrum sampleLight(HitRecord hitRecord, LightGeometry lightSource) {
		float[][] sample = this.sampler.makeSamples(1, 2);
		// Make direction from hit point to light source position; this is only supposed to work with point lights
		HitRecord lightHit = lightSource.sample(sample[0]);
		Vector3f lightDir = StaticVecmath.sub(lightHit.position, hitRecord.position);
		float d2 = lightDir.lengthSquared();
		lightDir.normalize();
		
		Ray shadowRay = new Ray(hitRecord.position, lightDir, 0, true);
		HitRecord shadowHit = root.intersect(shadowRay);
		if (shadowHit != null &&
				StaticVecmath.dist2(shadowHit.position, hitRecord.position) + 1e-5f < d2) //only if closer than light
			return new WeightedSpectrum(new Spectrum(), lightHit.p);
		
		// Evaluate the BRDF
		Spectrum brdfValue = hitRecord.material.evaluateBRDF(hitRecord, hitRecord.w, lightDir);
		
		// Multiply together factors relevant for shading, that is, brdf * emission * ndotl * geometry term
		Spectrum s = new Spectrum(brdfValue);
		
		// Multiply with emission
		s.mult(lightHit.material.evaluateEmission(lightHit, StaticVecmath.negate(lightDir)));
		
		// Multiply with cosine of surface normal and incident direction
		float ndotl = hitRecord.normal.dot(lightDir);
		ndotl = Math.max(ndotl, 0.f);
		s.mult(ndotl);
		
		// Geometry term: multiply with 1/(squared distance), only correct like this 
		// for point lights (not area lights)!
		s.mult(1.f/(d2*lightHit.p));
		float cos = Math.max(lightHit.normal.dot(StaticVecmath.negate(lightDir)), 0);
		s.mult(cos);
		return new WeightedSpectrum(s, lightHit.p);
	}

	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}

	private class WeightedSpectrum extends Spectrum {
		
		private float p;

		public WeightedSpectrum(Spectrum s, float p) {
			super(s);
			this.p = p;
		}
		
		public String toString() {
			return super.toString() + " p: " + this.p;
		}
	}
}
