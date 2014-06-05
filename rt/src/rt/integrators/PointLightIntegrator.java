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
import rt.Scene;
import rt.Spectrum;
import rt.samplers.RandomSampler;
import util.ImprovedNoise;
import util.MyMath;
import util.StaticVecmath;

/**
 * Integrator for Whitted style ray tracing. This is a basic version that needs to be extended!
 */
public class PointLightIntegrator implements Integrator {

	LightList lightList;
	Intersectable root;
	private RandomSampler sampler;
	private final boolean FOGGY = true;
	
	public PointLightIntegrator(Scene scene)
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
				
				// Make direction from hit point to light source position; this is only supposed to work with point lights
				HitRecord lightHit = lightSource.sample(null);
				Vector3f lightDir = StaticVecmath.sub(lightHit.position, hitRecord.position);
				float d2 = lightDir.lengthSquared();
				lightDir.normalize();
				
				Ray shadowRay = new Ray(hitRecord.position, lightDir, r.t, 0, true);
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
			
			Spectrum T = new Spectrum(1);
			Spectrum L = new Spectrum(0);
			if (FOGGY) {
				float dist = MyMath.sqrt(StaticVecmath.dist2(r.origin, hitRecord.position));
				float ds = stepsize;
				HitRecord lightHit = lightList.getRandomLight(makePixelSamples(sampler, 2)).sample(null);
				float prob = 1f/lightList.size();
				//HitRecord lightHit = lightList.get(1).sample(null);
				//float prob = 1;// 1f/lightList.size();
				for (float s_i = ds; s_i <= dist; s_i += ds) {
					Point3f p = r.pointAt(s_i); //asserts r.dir is normalized!!!
					Spectrum inscattering = new Spectrum(T);
					Vector3f lightDir = StaticVecmath.sub(lightHit.position, p);
					float d2 = lightDir.lengthSquared();
					lightDir.normalize();
					Ray shadowRay = new Ray(p, lightDir, r.t, 0, true);
					HitRecord shadowHit = root.intersect(shadowRay);
					if (shadowHit != null &&
							StaticVecmath.dist2(shadowHit.position, hitRecord.position) < d2) //only if closer than light
						inscattering.mult(0);
					else {
						inscattering.mult(L_ve(p)); //not in shadow
						Spectrum l = lightHit.material.evaluateEmission(lightHit, StaticVecmath.negate(lightDir));
						l.mult(1/prob);
						float d = MyMath.sqrt(d2);
						float shadowds = stepsize;
						for(float shadows_i = shadowds; shadows_i <= d; shadows_i += shadowds ) {
							Point3f shadowp = shadowRay.pointAt(shadows_i);
							float shadowSigma = sigmaS(shadowp);
							if (shadowSigma == 0) // lights are above nebula, break if outside
								break;
							l.mult(1 - shadowSigma*shadowds);
						}
						inscattering.mult(l);
					}
					
					L.add(inscattering);
					float sigma_s = sigmaS(p);
					if (sigma_s == 0 && r.direction.y > 0) // nebula is only on floor, break if outside of nebula.
						break;
					T.mult(1 - sigma_s*ds);
				}
				L.mult(ds);
				outgoing.mult(T); //times surface reflection L_s
			}
			L.add(outgoing);
			return L;
		} else 
			return new Spectrum(0.f,0.f,0.f);
		
	}
	
	private final float threshold = 0;
	private final float stepsize = 0.1f;
	
	private Spectrum L_ve(Point3f p) {
		return new Spectrum(0.002f*dampen(p));
	}
	
	private float dampen(Point3f p) {
		float dist = threshold - p.y;
		if (dist < 0)
			return 0;
		float s = MyMath.powE(dist) - 1;
		//System.out.println(s);
		return s;
	}

	/**
	 * For now only float is returned, but could be spectrum
	 * @param p
	 * @return
	 */
	public float sigmaS(Point3f p){
		float d = dampen(p);
		p.scale(20);
		float s = (float)(ImprovedNoise.noise(p.x, p.y, p.z) + 1)*0.6f; // sigma at the current point p
		return s*d;
	}

	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}

}
