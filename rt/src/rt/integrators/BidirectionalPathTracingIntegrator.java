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
	private final int MAX_EYE_BOUNCES = 20;
	private final int MAX_LIGHT_BOUNCES = 20; // minimum here is 1, 0 will be treated as 1
	
	public BidirectionalPathTracingIntegrator(Scene scene) {
		this.lightList = scene.getLightList();
		this.root = scene.getIntersectable();
		this.sampler = new RandomSampler();
		this.sampler.init(count++);
		//this.lightFilm = new BoxFilterFilm(scene.width, scene.height);
	}
	
	
	@Override
	public Spectrum integrate(Ray primaryRay) {
		List<PathNode> lightPath = new ArrayList<>();
		PathNode lightNode = traceLightRay();
		lightPath.add(lightNode);
		for (int t = 1; t < MAX_LIGHT_BOUNCES; t++) {
			Vector3f lastDir = lightNode.next.w;
			Tuple3f lastOrigin = lightNode.h.position;
			Ray r = new Ray(lastOrigin, lastDir, t, true);
			lightNode = makePathNode(r);
			//TODO: russian roulette
			if (lightNode == null) {
				break;
			}
			lightPath.add(lightNode);
		}
		
		Spectrum eyeAlpha = new Spectrum(1);
		Spectrum outgoing = new Spectrum();
		Ray r = primaryRay;
		boolean segmentIsSpecular = false;
		for (int s = 0; s < MAX_EYE_BOUNCES; s++) {
			PathNode eye = makePathNode(r);
			if (eye == null)
				return outgoing;
			Spectrum emission = eye.h.material.evaluateEmission(eye.h, eye.h.w);
			if (emission != null) {
				if (eye.bounce == 0 || segmentIsSpecular)
					outgoing.add(emission);
				break;
			}
			Spectrum lightAlpha = new Spectrum(1);
			for (PathNode light: lightPath) {
				Spectrum contribution = connect(eye, light);
				contribution.mult(lightAlpha);
				contribution.mult(eyeAlpha);
				if (light.bounce == 0)
					lightAlpha.mult(light.next.emission);
				else
					lightAlpha.mult(light.next.brdf);
				lightAlpha.mult(light.Gp);
				contribution.mult(1f/(s+light.bounce+1));
				outgoing.add(contribution);
			}
			segmentIsSpecular = eye.next.isSpecular;
			r = new Ray(eye.h.position, eye.next.w, r.depth + 1, true);
			eyeAlpha.mult(eye.next.brdf);
			eyeAlpha.mult(eye.Gp);
		}
		return outgoing;
	}
	
	public Spectrum connect(PathNode eye, PathNode light) {
		// pointing from light to eye spot
		Vector3f connection = StaticVecmath.sub(eye.h.position, light.h.position);
		float connection_d2 = connection.lengthSquared();
		Vector3f normedConnection = new Vector3f();
		normedConnection.normalize(connection);

		Spectrum s = eye.h.material.evaluateBRDF(eye.h, eye.h.w, StaticVecmath.negate(normedConnection));
		Spectrum lightPathSpectrum;
		if (light.bounce == 0) {
			lightPathSpectrum = light.h.material.evaluateEmission(light.h, normedConnection);
			lightPathSpectrum.mult(1/light.h.p);
		} else {
			lightPathSpectrum = light.h.material.evaluateBRDF(light.h, eye.h.w, normedConnection);
		}
		s.mult(lightPathSpectrum);

		float cosLightPath;
		if (light.h.normal != null) {
			cosLightPath = light.h.normal.dot(normedConnection);
			if (cosLightPath <= 0)
				return new Spectrum(); // stay black if hit light from behind
		} else cosLightPath = 1; //for point lights
		float cosEyePath = StaticVecmath.negate(normedConnection).dot(eye.h.normal);
		float connectionG = cosLightPath*cosEyePath/connection_d2;
		s.mult(connectionG);

		// shadow ray
		Ray r = new Ray(light.h.position, connection, 0, true);
		HitRecord shadowHit = root.intersect(r);
		if (shadowHit != null && shadowHit.t < 0.99f && shadowHit.material.castsShadows())
			return new Spectrum();
		else
			return s;
	}
	
	public PathNode makePathNode(Ray r) {
		HitRecord h = root.intersect(r);
		if (h == null)
			return null;		
		float[][] sample = this.sampler.makeSamples(1, 2);
		ShadingSample next = h.material.getShadingSample(h, sample[0]);
		float Gp = 1/next.p;
		if (!next.isSpecular)
			Gp *= h.normal.dot(next.w);
		return new PathNode(h, Gp, next, r.depth);
	}
	
	private PathNode traceLightRay() {
		LightGeometry lightSource = lightList.getRandomLight(this.sampler.makeSamples(1, 2));
		float[][] sample = this.sampler.makeSamples(1, 2);
		HitRecord lightHit = lightSource.sample(sample[0]);
		lightHit.p *= 1f/lightList.size();
		sample = this.sampler.makeSamples(1, 2);
		ShadingSample emission = lightHit.material.getEmissionSample(lightHit, sample[0]);

		float Gp = 1/lightHit.p;
		return new PathNode(lightHit, Gp, emission, 0);
	}

	@Override
	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}
	
	protected Film getLightFilm() {
		return lightFilm;
	}
}
