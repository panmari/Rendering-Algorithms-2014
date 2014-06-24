package rt.basicscenes;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.IntersectableList;
import rt.LightGeometry;
import rt.LightList;
import rt.Scene;
import rt.Spectrum;
import rt.cameras.FixedCamera;
import rt.cameras.PinholeCamera;
import rt.films.BoxFilterFilm;
import rt.integrators.PointLightIntegratorFactory;
import rt.intersectables.Plane;
import rt.intersectables.Sphere;
import rt.lightsources.PointLight;
import rt.materials.Diffuse;
import rt.samplers.OneSamplerFactory;
import rt.samplers.RandomSampler;
import rt.samplers.RandomSamplerFactory;
import rt.tonemappers.ClampTonemapper;

/**
 * Ray traces a simple scene. An educational example to show how to use the {@link rt} framework.
 */
public class PlanesAndSpheres extends Scene {
		
	public PlanesAndSpheres()
	{
		// Output file name
		outputFilename = new String("../output/basicscenes/PlanesAndSpheres");
		
		// Image width and height in pixels
		width = 640;
		height = 360;
		
		// Number of samples per pixel
		SPP = 8;
		
		// Specify which camera, film, and tonemapper to use
		camera = new PinholeCamera(new Vector3f(0,0,5), new Vector3f(0,0,0), new Vector3f(0,1,0), 60, 16f/9f, 640, 360);
		film = new BoxFilterFilm(width, height);
		tonemapper = new ClampTonemapper();
		
		// Specify which integrator and sampler to use
		integratorFactory = new PointLightIntegratorFactory();
		samplerFactory = new RandomSamplerFactory();
		
		// Define the root object (an intersectable) of the scene
		// Two CSG planes and a dodecahedron
		rt.IntersectableList list = new IntersectableList();
		list.add(new Sphere(new Point3f(0,0,0), 1, new Diffuse()));
		list.add(new Sphere(new Point3f(2,1,0), 1, new Diffuse()));
		list.add(new Sphere(new Point3f(-3,0,0), 1, new Diffuse()));
		list.add(new Plane(new Vector3f(0,0,1), 4));
		list.add(new Plane(new Vector3f(0,1,0), 2));
		
		root = list;
		// Light sources
		LightGeometry pointLight = new PointLight(new Vector3f(0.f, 2.f, 0.f), new Spectrum(10.f, 10.f, 10.f));
		LightGeometry pointLight2 = new PointLight(new Vector3f(-3f, 2.f, 0.f), new Spectrum(10.f, 10.f, 10.f));

		lightList = new LightList();
		lightList.add(pointLight);
		lightList.add(pointLight2);
	}
	
}
