package rt.integrators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.*;

import rt.HitRecord;
import rt.Integrator;
import rt.Intersectable;
import rt.LightList;
import rt.LightGeometry;
import rt.Material.ShadingSample;
import rt.Ray;
import rt.Sampler;
import rt.SamplerFactory;
import rt.Scene;
import rt.Spectrum;
import rt.samplers.RandomSampler;
import rt.samplers.UniformSampler;
import util.StaticVecmath;

/**
 * Integrator for Whitted style ray tracing. This is a basic version that needs to be extended!
 */
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
			List<WeightedSpectrum> specs = new ArrayList<>(2);
			// Iterate over all light sources
			WeightedSpectrum lightSampledSpectrum = sampleLight(hitRecord, randomLightSource);
			lightSampledSpectrum.p *= 1f/lightList.size(); // adapt probability to hit exactly that light
			lightSampledSpectrum.mult(1/lightSampledSpectrum.p);
			specs.add(lightSampledSpectrum);
			specs.add(sampleBRDF(hitRecord));
			
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
				s.mult(s.p/p_sum);
				// balance heuristic -> p*p/p_sum_squares //TODO
				//s.mult(s.p*s.p/p_sum);
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
				float cosTheta_i_prime = hitRecord.normal.dot(shadingSample.w);
				float areaProbablity = shadingSample.p*Math.abs(cosTheta_i_prime);
				areaProbablity /= StaticVecmath.dist2(hitRecord.position, shadingSampleHit.position);
				Spectrum lightHit = shadingSampleHit.material.evaluateEmission(shadingSampleHit, StaticVecmath.negate(shadingSample.w));
				if (lightHit != null) {
					lightHit.mult(shadingSample.brdf);
					float ndotl = hitRecord.normal.dot(shadingSample.w);
					ndotl = Math.max(ndotl, 0.f);
					lightHit.mult(ndotl/shadingSample.p);
					return new WeightedSpectrum(lightHit, areaProbablity);
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
		s.mult(1.f/(d2));
		
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
