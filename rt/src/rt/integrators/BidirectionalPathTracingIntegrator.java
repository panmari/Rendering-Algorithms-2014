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

public class BidirectionalPathTracingIntegrator implements Integrator {

	private LightList lightList;
	private Intersectable root;
	private RandomSampler sampler;
	private Random bulletGenerator;
	private StdHelper stdHelper;
	private final int MAX_BOUNCES = 10;
	private static int count = 0;
	
	public BidirectionalPathTracingIntegrator(Scene scene) {
		this.lightList = scene.getLightList();
		this.root = scene.getIntersectable();
		this.sampler = new RandomSampler();
		this.bulletGenerator = new Random(count);
		this.sampler.init(count++);
		this.stdHelper = new StdHelper(scene.getSPP());
	}
	
	
	@Override
	public Spectrum integrate(Ray primaryRay) {
		
	}
	
	public PathNode traceEyeRay(Ray primaryRay) {
		Ray currentRay = primaryRay;
		Spectrum outgoing = new Spectrum();
		Spectrum alpha = new Spectrum(1);
		RussianRouletteIterator rr = new RussianRouletteIterator(0,0,0,0,.5f);
		int bounce = 0;
		for(;bounce < MAX_BOUNCES ;bounce++) {
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
		stdHelper.update(outgoing.getLuminance(), bounce + 1);
		return outgoing;
	}
	
	private PathNode traceLightRay() {
		Spectrum alpha = new Spectrum(1);
		float[][] sample = this.sampler.makeSamples(1, 2);
		LightGeometry lightSource = lightList.getRandomLight(this.sampler.makeSamples(1, 2));
		
		HitRecord lightHit = lightSource.sample(sample[0]);
		
		sample = this.sampler.makeSamples(1, 2);
		ShadingSample emission = lightHit.material.getEmissionSample(lightHit, sample[0]);
		return new PathNode(lightHit.position, alpha, emission.brdf)
	}

	@Override
	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}
}
