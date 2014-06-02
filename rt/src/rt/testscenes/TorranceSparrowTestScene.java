package rt.testscenes;

import javax.vecmath.Vector3f;

import rt.*;
import rt.cameras.PinholeCamera;
import rt.films.BoxFilterFilm;
import rt.integrators.*;
import rt.intersectables.*;
import rt.intersectables.CSG.CSGPlane;
import rt.intersectables.CSG.CSGSolid;
import rt.intersectables.CSG.CSGSphere;
import rt.lightsources.*;
import rt.materials.*;
import rt.samplers.*;
import rt.tonemappers.ClampTonemapper;

public class TorranceSparrowTestScene extends Scene {
	
	public TorranceSparrowTestScene()
	{
		// Output file name
		outputFilename = new String("../output/testscenes/TorranceSparrowTestScene");
		
		// Image width and height in pixels
		width = 640;
		height = 360;
		
		// Number of samples per pixel
		SPP = 4;
		
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
		integratorFactory = new PointLightIntegratorFactory();
		samplerFactory = new OneSamplerFactory();		
		Material chessTexture = null;
		
		
		chessTexture = new Textured("../textures/chessboard.jpg");
		
		
		Material gold = new Glossy(8f, new Spectrum(0.25f,0.306f,1.426f), new Spectrum(3,2.88f,1.846f));

		CSGSolid sphere = new CSGSphere(gold);
		
		// Ground and back plane
		XYZGrid grid = new XYZGrid(new Spectrum(0.2f, 0.f, 0.f), new Spectrum(1.f, 1.f, 1.f), 0.1f, new Vector3f(0.f, 0.3f, 0.f));
		CSGPlane groundPlane = new CSGPlane(new Vector3f(0.f, 1.f, 0.f), 1.5f);
		groundPlane.material = chessTexture;
		CSGPlane backPlane = new CSGPlane(new Vector3f(0.f, 0.f, 1.f), 3.15f);
		backPlane.material = chessTexture;		
		
		// Collect objects in intersectable list
		IntersectableList intersectableList = new IntersectableList();
		intersectableList.add(sphere);	
		intersectableList.add(groundPlane);
		intersectableList.add(backPlane);
		
		// Set the root node for the scene
		root = intersectableList;
		
		// Light sources
		Vector3f lightPos = new Vector3f(eye);
		lightPos.add(new Vector3f(-1.f, 0.f, 0.f));
		LightGeometry pointLight1 = new PointLight(lightPos, new Spectrum(14.f, 14.f, 14.f));
		lightPos.add(new Vector3f(2.f, 0.f, 0.f));
		LightGeometry pointLight2 = new PointLight(lightPos, new Spectrum(14.f, 14.f, 14.f));
		LightGeometry pointLight3 = new PointLight(new Vector3f(0.f, 5.f, 1.f), new Spectrum(24.f, 24.f, 24.f));
		lightList = new LightList();
		lightList.add(pointLight1);
		lightList.add(pointLight2);
		lightList.add(pointLight3);
	}
}
