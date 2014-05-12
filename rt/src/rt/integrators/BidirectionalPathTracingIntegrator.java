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
		this.lightFilm = new BoxFilterFilm(scene.getFilm().getWidth(), scene.getFilm().getHeight());
	}
	
	@Override
	public Spectrum integrate(Ray primaryRay) {
		//make eye path
		//make first node
		for (int eyeBounce = 0; eyeBounce < MAX_EYE_BOUNCES; eyeBounce++) {
			Spectrum alphaE = new Spectrum(1);
		}
		
		for (int lightBounce = 0; lightBounce < MAX_LIGHT_BOUNCES; lightBounce++) {
			Spectrum alphaL = new Spectrum(1);
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
