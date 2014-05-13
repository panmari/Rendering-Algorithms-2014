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
	private final int MAX_LIGHT_BOUNCES = 2; // minimum here is 1, 0 will be treated as 1
	
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
		Ray r = primaryRay;
		HitRecord hBefore = root.intersect(r);
		if (hBefore == null)
			return new Spectrum();
		ShadingSample sampleBefore = hBefore.material.getShadingSample(hBefore, sampler.makeSamples(2, 2)[0]);
		float g = 1;
		Spectrum alpha = new Spectrum(1);
		generateNodes(MAX_EYE_BOUNCES, r, alpha, sampleBefore, hBefore, g, eyePath, false);
		
		List<PathNode> lightPath = new ArrayList<>();
		alpha = new Spectrum(1);
		LightGeometry light = lightList.getRandomLight(sampler.makeSamples(2, 2));
		HitRecord lightHit = light.sample(sampler.makeSamples(2, 2)[0]);
		alpha.mult(lightList.size()/(lightHit.p));
		sampleBefore = lightHit.material.getEmissionSample(lightHit, sampler.makeSamples(2, 2)[0]);
		r = new Ray(lightHit.position, sampleBefore.w, primaryRay.t, 0, true);
		generateNodes(MAX_LIGHT_BOUNCES, r, alpha, sampleBefore, lightHit, g, lightPath, true);
		
		for (PathNode eyeNode: eyePath) {
			for (PathNode lightNode: lightPath) {
				Spectrum contribution = connect(eyeNode, lightNode, r.t);
				outgoing.add(contribution);
			}
		}	
		return outgoing;
	}
	
	private void generateNodes(int MAX_BOUNCES, Ray r, Spectrum alpha, 
			ShadingSample sampleBefore, HitRecord hBefore, float g, List<PathNode> path, boolean light) {
		for (int bounces = 1; bounces < MAX_BOUNCES; bounces++) {			
			//make new ray, intersect with scene
			r = new Ray(hBefore.position, sampleBefore.w, r.t, bounces, true);
			HitRecord h = root.intersect(r);
			if (h == null) {
				PathNode node = new PathNode(hBefore, alpha, g, 0, sampleBefore.p, bounces);
				path.add(node);
				break;
			}
			ShadingSample sample = h.material.getShadingSample(h, sampler.makeSamples(2, 2)[0]);
			float p_L = h.material.getDirectionalProbability(h, sample.w);
			PathNode node = new PathNode(hBefore, alpha, g, p_L, sampleBefore.p, bounces);
			path.add(node);
			
			alpha = new Spectrum(alpha);
			//multiply brdf to alpha
			if (light && bounces == 1)
				alpha.mult(sampleBefore.emission);
			else
				alpha.mult(sampleBefore.brdf);
			// multiply geometry term to alpha
			float cosTerm = hBefore.normal.dot(sampleBefore.w);
			alpha.mult(cosTerm);
			alpha.mult(1/sampleBefore.p);	
			
			g = cosTerm;
			assert g >= 0: "Got " + g;
			g *= h.normal.dot(h.w);
			g = Math.max(g, 0);
			assert g >= 0: "Got " + g;
			g /= StaticVecmath.dist2(hBefore.position, h.position);
			hBefore = h;
			sampleBefore = sample;
		}
		
	}
	
	public Spectrum connect(PathNode eye, PathNode light, float time) {
		// pointing from light to eye spot$
		Vector3f connection = StaticVecmath.sub(eye.h.position, light.h.position);
		float connection_d2 = connection.lengthSquared();
		Vector3f normedConnection = new Vector3f();
		normedConnection.normalize(connection);

		Spectrum s = new Spectrum(1);
		Spectrum eyePathSpectrum = eye.h.material.evaluateBRDF(eye.h, eye.h.w, StaticVecmath.negate(normedConnection));
		s.mult(eyePathSpectrum);
		Spectrum lightPathSpectrum;
		if (light.bounce == 1) {
			lightPathSpectrum = light.h.material.evaluateEmission(light.h, normedConnection);
		} else {
			lightPathSpectrum = light.h.material.evaluateBRDF(light.h, eye.h.w, normedConnection);
		}
		s.mult(lightPathSpectrum);

		s.mult(eye.alpha);
		s.mult(light.alpha);
		
		float cosEyePath = Math.max(StaticVecmath.negate(normedConnection).dot(eye.h.normal), 0);
		assert cosEyePath >= 0;
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
	@Override
	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}
	
	protected Film getLightFilm() {
		return lightFilm;
	}
}
