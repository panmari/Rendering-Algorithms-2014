package rt.integrators;

import java.util.Iterator;

import javax.vecmath.Point3f;
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
import rt.StaticVecmath;

public class WhittedIntegrator implements Integrator {

	LightList lightList;
	Intersectable root;
	private final int MAX_DEPTH = 5;
	
	public WhittedIntegrator(Scene scene)
	{
		this.lightList = scene.getLightList();
		this.root = scene.getIntersectable();
	}

	@Override
	public Spectrum integrate(Ray r) {

		HitRecord hitRecord = root.intersect(r);
		if (hitRecord == null)
			return new Spectrum(0.f,0.f,0.f);
		
		//follow specular refraction until maxdepth
		Spectrum reflectedPart = new Spectrum(0,0,0);
		Spectrum refractedPart = new Spectrum(0,0,0);
		if(hitRecord.material.hasSpecularReflection() && r.depth < MAX_DEPTH) {
			ShadingSample s = hitRecord.material.evaluateSpecularReflection(hitRecord);
			reflectedPart = new Spectrum(s.brdf);
						
			Ray recursiveRay = new Ray(hitRecord.position, s.w, r.depth + 1, true);
			Spectrum spec = integrate(recursiveRay);
			reflectedPart.mult(spec);
		}
		if(hitRecord.material.hasSpecularRefraction() && r.depth < MAX_DEPTH) {
			ShadingSample s = hitRecord.material.evaluateSpecularRefraction(hitRecord);
			refractedPart = new Spectrum(s.brdf);
						
			Ray recursiveRay = new Ray(hitRecord.position, s.w, r.depth + 1, true);
			Spectrum spec = integrate(recursiveRay);
			refractedPart.mult(spec);
		}
		
		if ((hitRecord.material.hasSpecularRefraction() || hitRecord.material.hasSpecularReflection())) {
			Spectrum refractPlusReflect = new Spectrum();
			refractPlusReflect.add(refractedPart);
			refractPlusReflect.add(reflectedPart);
			return refractPlusReflect;
		}
		Spectrum outgoing = new Spectrum(0.f, 0.f, 0.f);
		Spectrum brdfValue;
		// Iterate over all light sources
		Iterator<LightGeometry> it = lightList.iterator();
		while(it.hasNext())
		{
			LightGeometry lightSource = it.next();
			
			// Make direction from hit point to light source position; this is only supposed to work with point lights
			HitRecord lightHit = lightSource.sample(null);
			Vector3f lightDir = StaticVecmath.sub(lightHit.position, hitRecord.position);
			float d2 = lightDir.lengthSquared();
			lightDir.normalize();
			
			Ray shadowRay = new Ray(hitRecord.position, lightDir, 0, true);
			HitRecord shadowHit = root.intersect(shadowRay);
			if (shadowHit != null && shadowHit.material.castsShadows() &&
					StaticVecmath.dist2(shadowHit.position, hitRecord.position) < d2) //only if closer than light
				continue;

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
	}

	@Override
	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}

}
