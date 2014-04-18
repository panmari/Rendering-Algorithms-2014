package rt.integrators;

import java.util.Iterator;
import java.util.Random;

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
import util.StdHelper;

public class PathTracingIntegrator implements Integrator {

	final int nrBounces = 1;
	private LightList lightList;
	private Intersectable root;
	private RandomSampler sampler;
	private Random bulletGenerator = new Random();
	private StdHelper stdHelper;
	
	public PathTracingIntegrator(Scene scene) {
		this.lightList = scene.getLightList();
		this.root = scene.getIntersectable();
		this.sampler = new RandomSampler();
		this.stdHelper = new StdHelper(10);
	}
	
	@Override
	public Spectrum integrate(Ray primaryRay) {
		Ray currentRay = primaryRay;
		Spectrum outgoing = new Spectrum();
		Spectrum alpha = new Spectrum(1);
		RussianRouletteIterator rr = new RussianRouletteIterator(0,0,0,0,0,0.5f);
		for(int bounce = 0;; bounce++) {
			HitRecord hit = root.intersect(currentRay);
			if (hit == null)
				break;
			Spectrum emission = hit.material.evaluateEmission(hit, hit.w);
			if (emission != null) {
				if (bounce == 0)
					outgoing.add(emission);
				break;
			}

			Spectrum x = sampleLight(hit);
			Spectrum currentBounceContribution = new Spectrum(alpha);
			currentBounceContribution.mult(x);
			outgoing.add(currentBounceContribution);
			Float rrProbability = rr.next();
			if (bulletGenerator.nextFloat() < rrProbability)
				break;
			ShadingSample s = hit.material.getShadingSample(hit, this.sampler.makeSamples(1, 2)[0]);
			currentRay = new Ray(hit.position, s.w, bounce + 1, true);
			alpha.mult(s.brdf);
			alpha.mult(hit.normal.dot(s.w)/(s.p*(1 - rrProbability)));
		}
		stdHelper.update(outgoing.getLuminance());
		return outgoing;
	}
	
	private Spectrum sampleLight(HitRecord hitRecord) {
		float[][] sample = this.sampler.makeSamples(1, 2);
		LightGeometry lightSource = lightList.getRandomLight(this.sampler.makeSamples(1, 2));

		HitRecord lightHit = lightSource.sample(sample[0]);
		
		Vector3f lightDir = StaticVecmath.sub(lightHit.position, hitRecord.position);
		float d2 = lightDir.lengthSquared();
		lightDir.normalize();
		
		float cosLight;
		if (lightHit.normal != null)
			cosLight = Math.max(lightHit.normal.dot(StaticVecmath.negate(lightDir)), 0);
		else cosLight = 1; //for point lights
		
		float cosHit = hitRecord.normal.dot(lightDir);
		cosHit = Math.max(cosHit, 0.f);
		
		// Evaluate the BRDF, probability is saved in p of hitRecord
		Spectrum brdfValue = hitRecord.material.evaluateBRDF(hitRecord, hitRecord.w, lightDir);		 
		
		Spectrum s = new Spectrum(brdfValue);
		// Multiply with emission
		Spectrum emission = lightHit.material.evaluateEmission(lightHit, StaticVecmath.negate(lightDir));
		
		// Multiply with cosine of surface normal and incident direction
		emission.mult(cosHit);
		
		s.mult(emission);
		
		// russian roulette for shadow ray, probability for continuing ray
		//TODO: scale s.getLuminance() by the variance of the last 10 samples.
		float rrProbability = Math.min(1, s.getLuminance()*0.1f);
		if (bulletGenerator.nextFloat() > rrProbability)
			return new Spectrum();
		
		Ray shadowRay = new Ray(hitRecord.position, lightDir, 0, true);
		HitRecord shadowHit = root.intersect(shadowRay);
		if (shadowHit != null &&
				StaticVecmath.dist2(shadowHit.position, hitRecord.position) + 1e-5f < d2) //only if closer than light
			return new Spectrum();
	
		// adapt probability to hit exactly that light
		float probability = lightHit.p/lightList.size();

		// turn into directional probability
		float dirProbablity = probability * d2 /cosLight;
		s.mult(1f/(dirProbablity*(rrProbability)));
		return s;
	}

	@Override
	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}

	private class RussianRouletteIterator implements Iterator<Float>{

		int i = 0;
		private float[] floats;
		private float last;
		
		public RussianRouletteIterator(float... floats) {
			this.floats = floats;
			this.last = floats[floats.length - 1];
		}
		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public Float next() {
			if (i + 1 < floats.length) {
				float next = floats[i];
				i++;
				return next;
			} else
				return last;
		}

		@Override
		public void remove() {
			throw new RuntimeException("Not supported.");
		}
		
	}
}
