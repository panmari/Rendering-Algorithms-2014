package rt.integrators;

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
	
	public BidirectionalPathTracingIntegrator(Scene scene) {
		this.lightList = scene.getLightList();
		this.root = scene.getIntersectable();
		this.sampler = new RandomSampler();
		this.sampler.init(count++);
	}
	
	
	@Override
	public Spectrum integrate(Ray primaryRay) {
		PathNode eye = traceEyeRay(primaryRay);
		if (eye == null)
			return new Spectrum();
		PathNode light = traceLightRay();
		return connect(eye, light);
	}
	
	public Spectrum connect(PathNode eye, PathNode light) {
		Spectrum s = eye.L;
		Vector3f connection = StaticVecmath.sub(eye.h.position, light.h.position);
		float d2 = connection.lengthSquared();
		Vector3f normedConnection = new Vector3f();
		normedConnection.normalize(connection);

		s.mult(light.L);
		float cosLight = normedConnection.dot(light.h.normal);
		s.mult(cosLight/(d2));
		float cosSurface = StaticVecmath.negate(normedConnection).dot(eye.h.normal);
		s.mult(cosSurface);
		s.mult(eye.G);
		// shadow ray
		Ray r = new Ray(light.h.position, connection, 0, true);
		HitRecord shadowHit = root.intersect(r);
		if (shadowHit != null && shadowHit.t < 0.999f)
			return new Spectrum();
		else
			return s;
	}
	
	public PathNode traceEyeRay(Ray primaryRay) {
		Ray currentRay = primaryRay;
		HitRecord h = root.intersect(currentRay);
		if (h == null)
				return null;
		float G = h.normal.dot(h.w); // /StaticVecmath.dist2(currentRay.origin, h.position);
		
		float[][] sample = this.sampler.makeSamples(1, 2);
		ShadingSample next = h.material.getShadingSample(h, sample[0]);
		Spectrum L = next.brdf;
		L.mult(1);
		return new PathNode(h, G, L);
	}
	
	private PathNode traceLightRay() {
		LightGeometry lightSource = lightList.getRandomLight(this.sampler.makeSamples(1, 2));
		float[][] sample = this.sampler.makeSamples(1, 2);
		HitRecord lightHit = lightSource.sample(sample[0]);
		
		sample = this.sampler.makeSamples(1, 2);
		ShadingSample emission = lightHit.material.getEmissionSample(lightHit, sample[0]);
		float G = lightHit.normal.dot(emission.w)/lightHit.p;
		Spectrum L = emission.emission;
		L.mult(lightList.size()/lightHit.p); //emission.p only in subsequent hits
		return new PathNode(lightHit, G, L);
	}

	@Override
	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}
}
