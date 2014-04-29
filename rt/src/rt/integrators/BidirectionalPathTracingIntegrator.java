package rt.integrators;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.DirContext;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
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

public class BidirectionalPathTracingIntegrator implements Integrator {

	private LightList lightList;
	private Intersectable root;
	private RandomSampler sampler;
	private static int count = 0;
	private final int MAX_EYE_BOUNCES = 1;
	private final int MAX_LIGHT_BOUNCES = 1;
	
	public BidirectionalPathTracingIntegrator(Scene scene) {
		this.lightList = scene.getLightList();
		this.root = scene.getIntersectable();
		this.sampler = new RandomSampler();
		this.sampler.init(count++);
	}
	
	
	@Override
	public Spectrum integrate(Ray primaryRay) {
		List<PathNode> lightPath = new ArrayList<>();
		PathNode beforeLight = traceLightRay();
		lightPath.add(beforeLight);
		for (int t = 0; t < MAX_LIGHT_BOUNCES; t++) {
			Vector3f lastDir = beforeLight.next.w;
			Tuple3f lastOrigin = beforeLight.h.position;
			Ray r = new Ray(lastOrigin, lastDir, 0, true);
			beforeLight = makePathNode(r);
			//TODO: russian roulette
			if (beforeLight == null)
				break;
			lightPath.add(beforeLight);
		}
		PathNode eye = null;
		Spectrum alpha = new Spectrum(1);
		Spectrum outgoing = new Spectrum();
		for (int s = 0; s < MAX_EYE_BOUNCES; s++) {
			eye = makePathNode(primaryRay);
			if (eye == null)
				return outgoing;
			alpha.mult(eye.next.brdf);
			Spectrum currentContribution = connect(eye, light);
			outgoing.add(currentContribution);
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
		} else cosLightPath = 1; //for point lights
		float cosEyePath = StaticVecmath.negate(normedConnection).dot(eye.h.normal);
		float connectionG = cosLightPath*cosEyePath/connection_d2;
		s.mult(connectionG);

		// shadow ray
		Ray r = new Ray(light.h.position, connection, 0, true);
		HitRecord shadowHit = root.intersect(r);
		if (shadowHit != null && shadowHit.t < 0.99f)
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
		float Gp = h.normal.dot(next.w)/next.p;
		return new PathNode(h, Gp, next, 0);
	}
	
	private PathNode traceLightRay() {
		LightGeometry lightSource = lightList.getRandomLight(this.sampler.makeSamples(1, 2));
		float[][] sample = this.sampler.makeSamples(1, 2);
		HitRecord lightHit = lightSource.sample(sample[0]);
		lightHit.p *= 1f/lightList.size();
		sample = this.sampler.makeSamples(1, 2);
		ShadingSample emission = lightHit.material.getEmissionSample(lightHit, sample[0]);

		float cosLight;
		if (lightHit.normal != null) {
			cosLight = lightHit.normal.dot(emission.w);
		} else cosLight = 1; //for point lights
		
		float Gp = cosLight/emission.p;
		return new PathNode(lightHit, Gp, emission, 0);
	}

	@Override
	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}
}
