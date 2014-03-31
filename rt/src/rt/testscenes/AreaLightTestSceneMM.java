package rt.testscenes;

import javax.vecmath.*;

import rt.*;
import rt.intersectables.*;
import rt.tonemappers.*;
import rt.integrators.*;
import rt.lightsources.*;
import rt.materials.*;
import rt.samplers.*;
import rt.cameras.*;
import rt.films.*;

public class AreaLightTestSceneMM extends Scene {
	
	public AreaLightTestSceneMM()
	{	
		outputFilename = new String("../output/testscenes/AreaLightTestMM");
				
		// Specify pixel sampler to be used
		samplerFactory = new RandomSamplerFactory();
		
		// Samples per pixel
		SPP = 32;
		outputFilename += String.format("_%d_SPP", SPP);
		
		// Make camera and film
		Vector3f eye =  new Vector3f(-3.f,1.5f,4.f);
		Vector3f lookAt =  new Vector3f(0.f,1.f,0.f);
		Vector3f up = new Vector3f(0.f,1.f,0.f);
		float fov = 60.f;
		int width = 512;
		int height = 512;
		float aspect = (float)width/(float)height;
		camera = new PinholeCamera(eye, lookAt, up, fov, aspect, width, height);
		film = new BoxFilterFilm(width, height);						
		tonemapper = new ClampTonemapper();
		
		// Specify integrator to be used
		integratorFactory = new AreaLightIntegratorFactory();
		
		// List of objects
		IntersectableList objects = new IntersectableList();	
		//new Vector3f(-.4f,-.49f,1.2f), .25f
		CSGSolid sphere = new CSGSphere();
		Matrix4f t = new Matrix4f();
		t.setIdentity();
		t.setScale(0.25f);
		t.setTranslation(new Vector3f(-.4f,-.49f,1.2f));
		sphere = new CSGInstance(sphere, t);
		sphere.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(sphere);
				
		Rectangle rectangle = new Rectangle(new Point3f(2.f, -.75f, 2.f), new Vector3f(0.f, 4.f, 0.f), new Vector3f(0.f, 0.f, -4.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.2f, 0.2f));
		objects.add(rectangle);
	
		// Bottom
		rectangle = new Rectangle(new Point3f(-2.f, -.75f, 2.f), new Vector3f(4.f, 0.f, 0.f), new Vector3f(0.f, 0.f, -4.f));
		rectangle.material = new Diffuse(new Spectrum(.8f));
		//rectangle.material = new Textured("../textures/grass-texture.jpg");
		objects.add(rectangle);

		// Top
		rectangle = new Rectangle(new Point3f(-2.f, 3.25f, 2.f), new Vector3f(0.f, 0.f, -4.f), new Vector3f(4.f, 0.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);
		
		rectangle = new Rectangle(new Point3f(-2.f, -.75f, -2f), new Vector3f(4.f, 0.f, 0.f), new Vector3f(0.f, 4.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.2f, 0.8f, 0.2f));
		objects.add(rectangle);
		
		//BOX BACK
		rectangle = new Rectangle(new Point3f(0.5f, -.75f, 0.25f), new Vector3f(0.5f, 0.f, 0.f), new Vector3f(0.f, 2.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);		
		rectangle = new Rectangle(new Point3f(0.5f, -.75f, -0.25f), new Vector3f(0.f, 0.f, 0.5f), new Vector3f(0.f, 2.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);		
		rectangle = new Rectangle(new Point3f(1.0f, -.75f, -0.25f), new Vector3f(-0.5f, 0.f, 0.f), new Vector3f(0.f, 2.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);		
		rectangle = new Rectangle(new Point3f(1.0f, -.75f, 0.25f), new Vector3f(0.f, 0.f, -0.5f), new Vector3f(0.f, 2.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);		
		rectangle = new Rectangle(new Point3f(0.5f, 1.25f, 0.25f), new Vector3f(0.5f, 0.f, 0.f), new Vector3f(0.f, 0.f, -0.5f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);
		
		//BOX CENTER
		rectangle = new Rectangle(new Point3f(-0.5f, -.75f, 0.25f), new Vector3f(0.5f, 0.f, 0.f), new Vector3f(0.f, 2.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);		
		rectangle = new Rectangle(new Point3f(-0.5f, -.75f, -0.25f), new Vector3f(0.f, 0.f, 0.5f), new Vector3f(0.f, 2.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);		
		rectangle = new Rectangle(new Point3f(0.0f, -.75f, -0.25f), new Vector3f(-0.5f, 0.f, 0.f), new Vector3f(0.f, 2.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);		
		rectangle = new Rectangle(new Point3f(0.0f, -.75f, 0.25f), new Vector3f(0.f, 0.f, -0.5f), new Vector3f(0.f, 2.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);		
		rectangle = new Rectangle(new Point3f(-0.5f, 1.25f, 0.25f), new Vector3f(0.5f, 0.f, 0.f), new Vector3f(0.f, 0.f, -0.5f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);
		
		//BOX FRONT
		rectangle = new Rectangle(new Point3f(-1.5f, -.75f, 0.25f), new Vector3f(0.5f, 0.f, 0.f), new Vector3f(0.f, 2.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);		
		rectangle = new Rectangle(new Point3f(-1.5f, -.75f, -0.25f), new Vector3f(0.f, 0.f, 0.5f), new Vector3f(0.f, 2.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);		
		rectangle = new Rectangle(new Point3f(-1.0f, -.75f, -0.25f), new Vector3f(-0.5f, 0.f, 0.f), new Vector3f(0.f, 2.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);		
		rectangle = new Rectangle(new Point3f(-1.0f, -.75f, 0.25f), new Vector3f(0.f, 0.f, -0.5f), new Vector3f(0.f, 2.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);		
		rectangle = new Rectangle(new Point3f(-1.5f, 1.25f, 0.25f), new Vector3f(0.5f, 0.f, 0.f), new Vector3f(0.f, 0.f, -0.5f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);
		
		// Add area lights
		Point3f bottomLeft = new Point3f(1f, 3.f, 1.5f);
		Vector3f right = new Vector3f(0.f, 0.f, 0.25f);
		Vector3f top = new Vector3f(-0.25f, 0.f, 0.f);
		AreaLight rectangleLight = new AreaLight(bottomLeft, right, top, new Spectrum(40.f, 40.f, 40.f));
		objects.add(rectangleLight);
		
		bottomLeft = new Point3f(-0.5f, 3.f, -1f);
		right = new Vector3f(0.f, 0.f, 0.25f);
		top = new Vector3f(-0.25f, 0.f, 0.f);
		AreaLight rectangleLight2 = new AreaLight(bottomLeft, right, top, new Spectrum(40.f, 40.f, 40.f));
		objects.add(rectangleLight2);
		
		// Connect objects to root
		root = objects;
		
		// Add light sources
		
		// List of lights
		lightList = new LightList();
		lightList.add(rectangleLight);
		lightList.add(rectangleLight2);
	}
}
