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
	
	public WhittedIntegrator(Scene scene)
	{
		this.lightList = scene.getLightList();
		this.root = scene.getIntersectable();
	}

	@Override
	public Spectrum integrate(Ray r) {

		HitRecord hitRecord = root.intersect(r);
		
		//follow specular refraction until maxdepth
		int depth = 0;
		Spectrum brdfValueRefr = new Spectrum(1,1,1);

		while(hitRecord != null && hitRecord.material.hasSpecularReflection() && depth < 10) {
			ShadingSample s = hitRecord.material.evaluateSpecularReflection(hitRecord);
			brdfValueRefr.mult(s.brdf);
			
			Point3f posPlusEpsilon = new Point3f();
			posPlusEpsilon.scaleAdd(0.0001f, s.w, hitRecord.position);
			
			Ray recursiveRay = new Ray(posPlusEpsilon, s.w);
			hitRecord = root.intersect(recursiveRay);
			depth++;
		}
		/*
		while(hitRecord != null && hitRecord.material.hasSpecularRefraction() && depth < 10) {
			ShadingSample s = hitRecord.material.evaluateSpecularRefraction(hitRecord);
			brdfValueRefr.mult(s.brdf);
			Ray recursiveRay = new Ray(hitRecord.position, s.w);
			hitRecord = root.intersect(recursiveRay);
			depth++;
		}
		*/
		if(hitRecord != null)
		{
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
				
				Point3f shadowRayStart = new Point3f(lightDir);
				shadowRayStart.scaleAdd(0.001f, hitRecord.position);

				Ray shadowRay = new Ray(shadowRayStart, lightDir);
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
			//outgoing.mult(brdfValueRefr);
			return outgoing;
		} else 
			return new Spectrum(0.f,0.f,0.f);
		
	}

	@Override
	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}

}
