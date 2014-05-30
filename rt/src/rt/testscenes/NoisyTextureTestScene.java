package rt.testscenes;

import javax.vecmath.Matrix4f;
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
import rt.cameras.ThinLensCamera;
import rt.films.BoxFilterFilm;
import rt.integrators.PathTracingIntegratorFactory;
import rt.integrators.PointLightIntegratorFactory;
import rt.intersectables.*;
import rt.lightsources.PointLight;
import rt.materials.Diffuse;
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
		outputFilename = new String("../output/testscenes/NoisyTextureTestSceneBlurPath");
		
		// Image width and height in pixels
		width = 640;
		height = 360;
		
		// Number of samples per pixel
		SPP = 2048;
		
		// Specify which camera, film, and tonemapper to use
		Vector3f eye = new Vector3f(0.f, 0.f, 5.f);
		Vector3f lookAt = new Vector3f(0.f, 1f, 0.f);
		Vector3f up = new Vector3f(0.f, 1.f, 0.f);
		float fov = 70.f;
		float aspect = 16.f/9.f;
		camera = new ThinLensCamera(eye, lookAt, up, fov, aspect, width, height, 5, 0.1f);
		film = new BoxFilterFilm(width, height);
		tonemapper = new ClampTonemapper();
		
		// Specify which integrator and sampler to use
		integratorFactory = new PointLightIntegratorFactory();
		integratorFactory = new PathTracingIntegratorFactory();
		samplerFactory = new RandomSamplerFactory();		
		Material chessTexture = null;
		
		chessTexture = new Textured("../textures/grass-texture.jpg");
		
		//some initialization
		Material glossy = new Glossy( 8.f, new Spectrum(0.27f, 0.82f, 1.16f), new Spectrum(3.23f, 2.6f, 2.5f));
		Material diffuse = new Diffuse(new Spectrum(1));
		
		Material silver = new rt.materials.Glossy( 8.f, new Spectrum(0.131f, 0.12f, 0.144f), new Spectrum(3.88f, 3.45f, 2.56f));
		Material gold = new rt.materials.Glossy( 8.f, new Spectrum(0.25f, 0.306f, 1.426f), new Spectrum(3.f, 2.88f, 1.846f));
		//glossy = diffuse;
		Intersectable sphere = new Sphere();
		
		Matrix4f t = new Matrix4f();
		t.setIdentity();
		Instance middleSphere = new Instance(sphere, t);
		middleSphere.material = new NoisyTexture(glossy, Type.CONTINENT);
		
		t = new Matrix4f();
		t.setIdentity();
		t.setTranslation(new Vector3f(-2.5f,0,0));
		Instance movingMiddleInst = new Instance(sphere, t);
		t = new Matrix4f();
		t.setTranslation(new Vector3f(0,1f,0));
		AnimatedInstance movingMiddle = new AnimatedInstance(movingMiddleInst, t);
		movingMiddle.material = glossy; //new NoisyTexture(glossy, Type.CONTINENT);
		
		t = new Matrix4f();
		t.setIdentity();
		t.setTranslation(new Vector3f(-3,0,3));
		Instance leftSphere = new Instance(sphere, t);
		leftSphere.material = new NoisyTexture(glossy, Type.ROUGH);
		
		t = new Matrix4f();
		t.setIdentity();
		t.setTranslation(new Vector3f(3,0,1.5f));
		Instance rightSphere = new Instance(sphere, t);
		rightSphere.material = new NoisyTexture(gold, Type.STRANGE);
		
		t = new Matrix4f();
		t.setIdentity();
		t.setTranslation(new Vector3f(3,3,-2));
		Instance upperRightSphere = new Instance(sphere, t);
		upperRightSphere.material = new NoisyTexture(silver, Type.EXPANDED);
		
		// Ground and back plane
		XYZGrid grid = new XYZGrid(new Spectrum(0.2f, 0.f, 0.f), new Spectrum(1.f, 1.f, 1.f), 0.1f, new Vector3f(0.f, 0.3f, 0.f));
		//CSGPlane groundPlane = new CSGPlane(new Vector3f(0.f, 1.f, 0.f), 1.5f);
		Rectangle groundPlane = new Rectangle(new Point3f(-9,-1.5f, -4), new Vector3f(0,0,9),new Vector3f(17,0,0));
		groundPlane.material = chessTexture;
		CSGPlane backPlane = new CSGPlane(new Vector3f(0.f, 0.f, 1.f), 10.15f);
		backPlane.material = grid;		
		
		// Collect objects in intersectable list
		IntersectableList intersectableList = new IntersectableList();
		intersectableList.add(middleSphere);	
		intersectableList.add(movingMiddle);
		intersectableList.add(groundPlane);
		intersectableList.add(backPlane);
		intersectableList.add(leftSphere);
		intersectableList.add(rightSphere);
		intersectableList.add(upperRightSphere);
		
		// Set the root node for the scene
		root = intersectableList;
		
		// Light sources
		Vector3f lightPos = new Vector3f(eye);
		lightPos.add(new Vector3f(-4.f, 4.f, -7.f));
		LightGeometry pointLight1 = new PointLight(lightPos, new Spectrum(50.f, 50.f, 50.f));
		lightPos.add(new Vector3f(2.f, 0.f, 0.f));
		LightGeometry pointLight2 = new PointLight(lightPos, new Spectrum(14.f, 14.f, 14.f));
		LightGeometry pointLight3 = new PointLight(new Vector3f(2.f, 2.f, 4.f), new Spectrum(80.f, 80.f, 80.f));
		lightList = new LightList();
		lightList.add(pointLight1);
		lightList.add(pointLight2);
		lightList.add(pointLight3);
		lightList.add(new PointLight(new Vector3f(-5, 3, 3), new Spectrum(35,35,35)));
	}
}
