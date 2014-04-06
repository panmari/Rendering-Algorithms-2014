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
import rt.integrators.heuristics.*;
import rt.samplers.RandomSampler;
import rt.samplers.UniformSampler;
import rt.util.FloatFunction;
import util.StaticVecmath;

public class AreaLightIntegrator implements Integrator {

	LightList lightList;
	Intersectable root;
	public final Sampler sampler;
	private final FloatFunction heuristic;
	
	public AreaLightIntegrator(Scene scene)
	{
		this.lightList = scene.getLightList();
		this.root = scene.getIntersectable();
		this.sampler = new RandomSampler();
		sampler.init(2);
		this.heuristic = new PowerHeuristic();
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
							
			Spectrum lightSampledSpectrum = sampleLight(hitRecord, lightList.getRandomLight(sampler.makeSamples(1, 1)));
			Spectrum brdfSampledSpectrum = sampleBRDF(hitRecord);
						
			Spectrum[] specs = new Spectrum[]{lightSampledSpectrum, brdfSampledSpectrum};
			Spectrum outgoing = new Spectrum();
			for (Spectrum s: specs) {
				outgoing.add(s);
			}
			return outgoing;
			
		} else return new Spectrum();	
			
	
	}
	
	private Spectrum sampleBRDF(HitRecord hitRecord) {
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


				if (emission != null && shadingSampleHit.normal.dot(shadingSampleHit.w) > 0) { // hit light from ahead
					// compute area probability for this ray
					float areaProbablity = shadingSampleHit.p/lightList.size();
					// make directional probability
					areaProbablity *= Math.abs(cosTheta_i); // abs should not matter
					areaProbablity /= StaticVecmath.dist2(hitRecord.position, shadingSampleHit.position);
					 
					emission.mult(shadingSample.brdf);
					emission.mult(cosTheta_i/shadingSample.p);
					float weight = heuristic.evaluate(shadingSample.p)/(heuristic.evaluate(shadingSample.p) + heuristic.evaluate(areaProbablity));
					emission.mult(weight);
					
					return emission;
				} else //didn't hit light -> stay dark
					return new Spectrum();
			} else // return black with probablity 0, since infinitely far away hit
				return new Spectrum();
		} else //this should not happen and is only here for lazyness
			return new Spectrum(1,0,0);
	}
	
	private Spectrum sampleLight(HitRecord hitRecord, LightGeometry lightSource) {
		float[][] sample = this.sampler.makeSamples(1, 2);
		// Make direction from hit point to light source position; this is only supposed to work with point lights
		HitRecord lightHit = lightSource.sample(sample[0]);
		// adapt probability to hit exactly that light
		lightHit.p *= 1f/lightList.size(); 
		
		Vector3f lightDir = StaticVecmath.sub(lightHit.position, hitRecord.position);
		float d2 = lightDir.lengthSquared();
		lightDir.normalize();
		
		Ray shadowRay = new Ray(hitRecord.position, lightDir, 0, true);
		HitRecord shadowHit = root.intersect(shadowRay);
		if (shadowHit != null &&
				StaticVecmath.dist2(shadowHit.position, hitRecord.position) + 1e-5f < d2) //only if closer than light
			return new Spectrum();
		
		// Evaluate the BRDF, probability is saved in p of hitRecord
		Spectrum brdfValue = hitRecord.material.evaluateBRDF(hitRecord, hitRecord.w, lightDir);		 
		
		// Multiply together factors relevant for shading, that is, brdf * emission * ndotl * geometry term
		Spectrum s = new Spectrum(brdfValue);
		
		// Multiply with emission
		s.mult(lightHit.material.evaluateEmission(lightHit, StaticVecmath.negate(lightDir)));
		
		// Multiply with cosine of surface normal and incident direction
		float ndotl = hitRecord.normal.dot(lightDir);
		ndotl = Math.max(ndotl, 0.f);
		s.mult(ndotl);
		
		float brdfProbability = hitRecord.p;
		//make area probability
		brdfProbability /= Math.abs(ndotl); // abs should not matter
		brdfProbability *= StaticVecmath.dist2(hitRecord.position, lightHit.position);


		// Geometry term
		s.mult(1.f/(d2*lightHit.p));
		float cos = Math.max(lightHit.normal.dot(StaticVecmath.negate(lightDir)), 0);
		s.mult(cos);
		
		float weight = heuristic.evaluate(lightHit.p)/(heuristic.evaluate(lightHit.p) + heuristic.evaluate(brdfProbability));
		s.mult(weight);
		return s;
	}

	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}
}
