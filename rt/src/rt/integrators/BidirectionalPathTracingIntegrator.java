package rt.integrators;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.DirContext;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import rt.Film;
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
import rt.films.BoxFilterFilm;
import rt.samplers.RandomSampler;
import util.HistHelper;
import util.StaticVecmath;

public class BidirectionalPathTracingIntegrator implements Integrator {

	private LightList lightList;
	private Film lightFilm;
	private Intersectable root;
	private RandomSampler sampler;
	private static int count = 0;
	private final int MAX_EYE_BOUNCES = 10;
	private final int MAX_LIGHT_BOUNCES = 20; // minimum here is 1, 0 will be treated as 1
	
	public BidirectionalPathTracingIntegrator(Scene scene) {
		this.lightList = scene.getLightList();
		this.root = scene.getIntersectable();
		this.sampler = new RandomSampler();
		this.sampler.init(count++);
		this.lightFilm = new BoxFilterFilm(scene.getFilm().getWidth(), scene.getFilm().getHeight());
	}
	
	@Override
	public Spectrum integrate(Ray primaryRay) {
		Spectrum outgoing = new Spectrum();
		//make eye path
		//make first node
		List<PathNode> eyePath = new ArrayList<>();
		Spectrum alphaBefore = new Spectrum(1);
		Ray r = primaryRay;
		HitRecord lastHit = root.intersect(r);
		if (lastHit == null)
			return new Spectrum();
		ShadingSample sample = lastHit.material.getShadingSample(lastHit, sampler.makeSamples(2, 2)[0]);
		float g = 1;
		float cosBefore = 1;
		float dirProbBefore = 1;
		boolean segmentIsSpecular = false;
		for (int eyeBounce = 1; eyeBounce < MAX_EYE_BOUNCES; eyeBounce++) {
			Spectrum emission = lastHit.material.evaluateEmission(lastHit, lastHit.w);
			if (emission != null) {
				if (segmentIsSpecular)
					outgoing.add(emission);
				break;
			}

			Spectrum alpha = new Spectrum(alphaBefore);
			//multiply brdf to alpha
			alpha.mult(lastHit.material.evaluateBRDF(lastHit, lastHit.w, sample.w));
			// multiply geometry term to alpha
			alpha.mult(cosBefore);
			alpha.mult(1/dirProbBefore);
			
			//make new ray, intersect with scene
			r = new Ray(lastHit.position, sample.w, r.t);
			HitRecord h = root.intersect(r);
			if (h == null) {
				PathNode node = new PathNode(lastHit, alphaBefore, g, 0, sample.p, eyeBounce);
				eyePath.add(node);
				break;
			}
			ShadingSample nextSample = h.material.getShadingSample(h, sampler.makeSamples(2, 2)[0]);
			float p_L = h.material.getDirectionalProbability(h, nextSample.w);
			PathNode node = new PathNode(lastHit, alphaBefore, g, p_L, sample.p, eyeBounce);
			eyePath.add(node);
			g = lastHit.normal.dot(sample.w)*h.normal.dot(h.w);
			g /= StaticVecmath.dist2(lastHit.position, h.position);
			cosBefore = lastHit.normal.dot(sample.w);
			dirProbBefore = sample.p;
			lastHit = h;
			sample = nextSample;
			alphaBefore = alpha;
		}
		
		Spectrum alphaL = new Spectrum(1);
		LightGeometry light = lightList.getRandomLight(sampler.makeSamples(2, 2));
		HitRecord lightHit = light.sample(sampler.makeSamples(2, 2)[0]);
		alphaL.mult(lightList.size()/(lightHit.p));
		PathNode LightNode = new PathNode(lightHit, alphaL, 1, 1, 1, 0);
		sample = lightHit.material.getEmissionSample(lightHit, sampler.makeSamples(2, 2)[0]);
		
		for (PathNode eyeNode: eyePath) {
			Spectrum contribution = connect(eyeNode, LightNode, r.t);
			outgoing.add(contribution);
		}
		//for (int lightBounce = 0; lightBounce < MAX_LIGHT_BOUNCES; lightBounce++) {
			
			
		//}
		
		return outgoing;
	}

	public Spectrum connect(PathNode eye, PathNode light, float time) {
		// pointing from light to eye spot
		Vector3f connection = StaticVecmath.sub(eye.h.position, light.h.position);
		float connection_d2 = connection.lengthSquared();
		Vector3f normedConnection = new Vector3f();
		normedConnection.normalize(connection);

		Spectrum s = eye.h.material.evaluateBRDF(eye.h, eye.h.w, StaticVecmath.negate(normedConnection));
		Spectrum lightPathSpectrum;
		if (light.bounce == 0) {
			lightPathSpectrum = light.h.material.evaluateEmission(light.h, normedConnection);
		} else {
			lightPathSpectrum = light.h.material.evaluateBRDF(light.h, eye.h.w, normedConnection);
		}
		s.mult(lightPathSpectrum);

		s.mult(eye.alpha);
		s.mult(light.alpha);
		
		float cosEyePath = StaticVecmath.negate(normedConnection).dot(eye.h.normal);
		float cosLightPath;
		if (light.h.normal != null) {
			cosLightPath = light.h.normal.dot(normedConnection);
			if (cosLightPath <= 0)
				return new Spectrum(); // stay black if hit light from behind
		} else cosLightPath = 1; //for point lights
		float connectionG = cosLightPath*cosEyePath/connection_d2;
		s.mult(connectionG);

		// shadow ray
		Ray r = new Ray(light.h.position, connection, time, 0, true  );
		HitRecord shadowHit = root.intersect(r);
		if (shadowHit != null && shadowHit.t < 0.99f && shadowHit.material.castsShadows())
			return new Spectrum();
		else
			return s;
	}
	private void generateNodes(int MAX_BOUNCES, Ray r, Spectrum alpha, ShadingSample sample, HitRecord lastHit, List<PathNode> list) {
		float g = 1;
		float cosBefore = 1;
		float dirProbBefore = 1;
		for (int bounces = 0; bounces < MAX_BOUNCES; bounces++) {
			alpha = new Spectrum(alpha);
			//multiply brdf to alpha
			alpha.mult(lastHit.material.evaluateBRDF(lastHit, lastHit.w, sample.w));
			// multiply geometry term to alpha
			alpha.mult(cosBefore);
			alpha.mult(1/dirProbBefore);
			
			//make new ray, intersect with scene
			r = new Ray(lastHit.position, sample.w, r.t);
			HitRecord h = root.intersect(r);
			ShadingSample nextSample = h.material.getShadingSample(h, sampler.makeSamples(2, 2)[0]);
			float p_L = h.material.getDirectionalProbability(h, nextSample.w);
			PathNode node = new PathNode(lastHit, alpha, g, p_L, sample.p, bounces);
			list.add(node);
			g = lastHit.normal.dot(sample.w)*h.normal.dot(h.w);
			g /= StaticVecmath.dist2(lastHit.position, h.position);
			cosBefore = lastHit.normal.dot(sample.w);
			dirProbBefore = sample.p;
			lastHit = h;
			sample = nextSample;
		}
	}
	
	@Override
	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}
	
	protected Film getLightFilm() {
		return lightFilm;
	}
}
