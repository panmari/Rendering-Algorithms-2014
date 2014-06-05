package rt.integrators;

import java.util.Random;

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
import rt.samplers.RandomSampler;
import util.ImprovedNoise;
import util.MyMath;
import util.StaticVecmath;
import util.StdHelper;

public class PathTracingIntegrator implements Integrator {

	private LightList lightList;
	private Intersectable root;
	private RandomSampler sampler;
	private Random bulletGenerator;
	private StdHelper stdHelper;
	private final int MAX_BOUNCES = 10;
	private static int count = 0;
	
	public PathTracingIntegrator(Scene scene) {
		this.lightList = scene.getLightList();
		this.root = scene.getIntersectable();
		this.sampler = new RandomSampler();
		this.bulletGenerator = new Random(count);
		this.sampler.init(count++);
		this.stdHelper = new StdHelper(scene.getSPP());
		this.stdHelper.update(1, 1); //add some dummy to prevent NaN in beginning
	}
	
	@Override
	public Spectrum integrate(Ray primaryRay) {
		Ray currentRay = primaryRay;
		Spectrum outgoing = new Spectrum();
		Spectrum alpha = new Spectrum(1);
		RussianRouletteIterator rr = new RussianRouletteIterator(0,0,0,0,.5f);
		int bounce = 0;
		boolean segmentIsSpecular = false;
		for(;bounce < MAX_BOUNCES ;bounce++) {
			HitRecord hit = root.intersect(currentRay);
			if (hit == null)
				break;
			Spectrum emission = hit.material.evaluateEmission(hit, hit.w);
			if (emission != null) {
				if (bounce == 0 || segmentIsSpecular)
					outgoing.add(emission);
				break;
			}

			Spectrum x = sampleLight(hit, currentRay.t);
			Spectrum currentBounceContribution = new Spectrum(alpha);
			currentBounceContribution.mult(x);
			if (bounce == 0)
				currentBounceContribution = doFog(currentRay, hit, currentBounceContribution);
			outgoing.add(currentBounceContribution);
			assert !Float.isNaN(outgoing.getLuminance());
			Float rrProbability = rr.next();
			if (bulletGenerator.nextFloat() < rrProbability)
				break;
			ShadingSample s = hit.material.getShadingSample(hit, this.sampler.makeSamples(1, 2)[0]);
			if (s == null) // Total internal refraction or some bs
				break;
			currentRay = new Ray(hit.position, s.w, currentRay.t, bounce + 1, true);
			alpha.mult(s.brdf);
			if (!s.isSpecular) {
				segmentIsSpecular = false;
				float cosTerm = hit.normal.dot(s.w);
				alpha.mult(cosTerm);
			} else
				segmentIsSpecular = true;
			alpha.mult(1/(s.p*(1 - rrProbability)));
			assert !Float.isNaN(alpha.getLuminance());
		}
		stdHelper.update(outgoing.getLuminance(), bounce + 1);
		return outgoing;
	}
	
	private Spectrum sampleLight(HitRecord hitRecord, float t) {
		float[][] sample = this.sampler.makeSamples(1, 2);
		LightGeometry lightSource = lightList.getRandomLight(this.sampler.makeSamples(1, 2));

		HitRecord lightHit = lightSource.sample(sample[0]);
		
		Vector3f lightDir = StaticVecmath.sub(lightHit.position, hitRecord.position);
		float d2 = lightDir.lengthSquared();
		lightDir.normalize();
		
		float cosLight;
		if (lightHit.normal != null) {
			cosLight = lightHit.normal.dot(StaticVecmath.negate(lightDir));
			if (cosLight <= 0)
				return new Spectrum(); // stay black if hit light from behind
		} else cosLight = 1; //for point lights
		
		// Evaluate the BRDF, probability is saved in p of hitRecord, must be done first bc may change hitRecord.normal
		Spectrum brdfValue = hitRecord.material.evaluateBRDF(hitRecord, hitRecord.w, lightDir);		 

		float cosHit = hitRecord.normal.dot(lightDir);
		cosHit = Math.max(cosHit, 0.f);
		
		Spectrum s = new Spectrum(brdfValue);
		// Multiply with emission
		Spectrum emission = lightHit.material.evaluateEmission(lightHit, StaticVecmath.negate(lightDir));
		
		// Multiply with cosine of surface normal and incident direction
		emission.mult(cosHit);
		
		s.mult(emission);	
		
		
		// adapt probability to hit exactly that light
		float probability = lightHit.p/lightList.size();

		// turn into directional probability
		float dirProbablity = probability * d2 / cosLight;
		s.mult(1f/dirProbablity);
		
		// russian roulette for shadow ray, probability for continuing ray
		float delta = stdHelper.getDelta();
		float contribution = s.getLuminance();
		float rrProbability = Math.min(1, contribution/(delta + 1e-5f));
		if (bulletGenerator.nextFloat() > rrProbability || contribution == 0)
			return new Spectrum();
		
		Ray shadowRay = new Ray(hitRecord.position, lightDir, t, 0, true);
		HitRecord shadowHit = root.intersect(shadowRay);
		if (shadowHit != null &&
				StaticVecmath.dist2(shadowHit.position, hitRecord.position) + 1e-5f < d2) //only if closer than light
			return new Spectrum();

		s.mult(1f/rrProbability);
		assert !Float.isNaN(s.getLuminance());
		return s;
	}


	private Spectrum doFog(Ray r, HitRecord hitRecord, Spectrum outgoing) {
		Spectrum T = new Spectrum(1);
		Spectrum L = new Spectrum(0);
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
		L.add(outgoing);
		return L;
	}
	
	private final float threshold = 0;
	private final float stepsize = .1f; //0.1f;
	
	private Spectrum L_ve(Point3f p) {
		return new Spectrum(0.001f*dampen(p));
	}
	
	private float dampen(Point3f p) {
		float dist = threshold - p.y;
		if (dist < 0)
			return 0;
		float s = MyMath.powE(dist) - 1;
		return s;
	}

	/**
	 * For now only float is returned, but could be spectrum
	 * @param p
	 * @return
	 */
	public float sigmaS(Point3f p){
		float d = dampen(p);
		p.scale(5);
		float s = (float)(ImprovedNoise.noise(p.x, p.y, p.z) + 1)*0.3f; // sigma at the current point p
		return s*d;
	}

	@Override
	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}
}
