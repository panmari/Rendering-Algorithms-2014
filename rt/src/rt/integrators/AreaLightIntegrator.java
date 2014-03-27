package rt.integrators;

import java.util.Iterator;

import javax.vecmath.*;

import rt.HitRecord;
import rt.Integrator;
import rt.Intersectable;
import rt.LightList;
import rt.LightGeometry;
import rt.Ray;
import rt.Sampler;
import rt.SamplerFactory;
import rt.Scene;
import rt.Spectrum;
import rt.StaticVecmath;
import rt.samplers.RandomSampler;
import rt.samplers.UniformSampler;

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
			Spectrum outgoing = new Spectrum(0.f, 0.f, 0.f);
			Spectrum brdfValue;
			
			// Iterate over all light sources
			Iterator<LightGeometry> it = lightList.iterator();
			while(it.hasNext())
			{
				LightGeometry lightSource = it.next();
				float[][] sample = this.sampler.makeSamples(1, 2);
				// Make direction from hit point to light source position; this is only supposed to work with point lights
				HitRecord lightHit = lightSource.sample(sample[0]);
				Vector3f lightDir = StaticVecmath.sub(lightHit.position, hitRecord.position);
				float d2 = lightDir.lengthSquared();
				lightDir.normalize();
				
				Ray shadowRay = new Ray(hitRecord.position, lightDir, 0, true);
				HitRecord shadowHit = root.intersect(shadowRay);
				if (shadowHit != null &&
						StaticVecmath.dist2(shadowHit.position, hitRecord.position) < d2) //only if closer than light
					continue;
				
				// Evaluate the BRDF
				brdfValue = hitRecord.material.evaluateBRDF(hitRecord, hitRecord.w, lightDir);
				
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
				s.mult(1.f/d2);
				
				// Accumulate
				outgoing.add(s);
			}
			return outgoing;
		} else 
			return new Spectrum(0.f,0.f,0.f);
		
	}

	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}

}
