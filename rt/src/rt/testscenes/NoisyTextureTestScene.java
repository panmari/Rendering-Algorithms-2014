package rt.testscenes;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.Intersectable;
import rt.IntersectableList;
import rt.LightGeometry;
import rt.LightList;
import rt.Material;
import rt.Scene;
import rt.Spectrum;
import rt.cameras.PinholeCamera;
import rt.films.BoxFilterFilm;
import rt.integrators.PointLightIntegratorFactory;
import rt.intersectables.CSGPlane;
import rt.intersectables.Sphere;
import rt.lightsources.PointLight;
import rt.materials.Glossy;
import rt.materials.NoisyTexture;
import rt.materials.Textured;
import rt.materials.XYZGrid;
import rt.materials.NoisyTexture.Type;
import rt.samplers.RandomSamplerFactory;
import rt.tonemappers.ClampTonemapper;

public class NoisyTextureTestScene extends Scene {
	
	public NoisyTextureTestScene()
	{
		// Output file name
		outputFilename = new String("../output/testscenes/NoisyTextureTestSceneNoBumpy");
		
		// Image width and height in pixels
		width = 640;
		height = 360;
		
		// Number of samples per pixel
		SPP = 128;
		
		// Specify which camera, film, and tonemapper to use
		Vector3f eye = new Vector3f(0.f, 0.f, 5.f);
		Vector3f lookAt = new Vector3f(0.f, -.5f, 0.f);
		Vector3f up = new Vector3f(0.f, 1.f, 0.f);
		float fov = 60.f;
		float aspect = 16.f/9.f;
		camera = new PinholeCamera(eye, lookAt, up, fov, aspect, width, height);
		film = new BoxFilterFilm(width, height);
		tonemapper = new ClampTonemapper();
		
		// Specify which integrator and sampler to use
		//integratorFactory = new PathTracingIntegratorFactory();
		integratorFactory = new PointLightIntegratorFactory();
		samplerFactory = new RandomSamplerFactory();		
		Material chessTexture = null;
		
		chessTexture = new Textured("../textures/grass-texture.jpg");
		
		// Add objects
		//some noisy sphere
		
		Material m = new Glossy( 8.f, new Spectrum(0.27f, 0.82f, 1.16f), new Spectrum(3.23f, 2.6f, 2.5f));
		Material noisyMaterial = new NoisyTexture(m, Type.SWIRLY_STRIPES);
		Intersectable noisySphere = new Sphere(new Point3f(0,0,0), 1f, noisyMaterial);
		// Ground and back plane
		XYZGrid grid = new XYZGrid(new Spectrum(0.2f, 0.f, 0.f), new Spectrum(1.f, 1.f, 1.f), 0.1f, new Vector3f(0.f, 0.3f, 0.f));
		CSGPlane groundPlane = new CSGPlane(new Vector3f(0.f, 1.f, 0.f), 1.5f);
		groundPlane.material = chessTexture;
		CSGPlane backPlane = new CSGPlane(new Vector3f(0.f, 0.f, 1.f), 3.15f);
		backPlane.material = grid;		
		
		// Collect objects in intersectable list
		IntersectableList intersectableList = new IntersectableList();
		intersectableList.add(noisySphere);	
		intersectableList.add(groundPlane);
		intersectableList.add(backPlane);
		
		// Set the root node for the scene
		root = intersectableList;
		
		// Light sources
		Vector3f lightPos = new Vector3f(eye);
		lightPos.add(new Vector3f(-1.f, 0.f, 0.f));
		LightGeometry pointLight1 = new PointLight(lightPos, new Spectrum(18.f, 18.f, 18.f));
		lightPos.add(new Vector3f(2.f, 0.f, 0.f));
		LightGeometry pointLight2 = new PointLight(lightPos, new Spectrum(14.f, 14.f, 14.f));
		LightGeometry pointLight3 = new PointLight(new Vector3f(2.f, 2.f, 2.f), new Spectrum(24.f, 24.f, 24.f));
		lightList = new LightList();
		lightList.add(pointLight1);
		lightList.add(pointLight2);
		lightList.add(pointLight3);
	}
}
