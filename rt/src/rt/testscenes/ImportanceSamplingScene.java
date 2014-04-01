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

public class ImportanceSamplingScene extends Scene {
	
	public ImportanceSamplingScene()
	{	
		outputFilename = new String("../output/testscenes/ImportanceSampling-mine");
				
		// Specify pixel sampler to be used
		samplerFactory = new RandomSamplerFactory();
		
		// Samples per pixel
		SPP = 512;
		outputFilename += String.format("_%d_SPP", SPP);
		
		// Make camera and film
		Vector3f eye =  new Vector3f(-5f,1f,0.f);
		Vector3f lookAt =  new Vector3f(0.f,0.f,0.f);
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
		
				
		Rectangle rectangle = new Rectangle(new Point3f(2.f, -.75f, 4.f), new Vector3f(0.f, 4.f, 0.f), new Vector3f(0.f, 0.f, -8.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.2f, 0.2f));
		objects.add(rectangle);
	
		// Bottom
		rectangle = new Rectangle(new Point3f(-4.f, -.75f, 4.f), new Vector3f(6.f, 0.f, 0.f), new Vector3f(0.f, 0.f, -8.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);

		//GLOSSY RECTANGLES
		
		rectangle = new Rectangle(new Point3f(-2.f, -.6f, -1.95f), new Vector3f(-0.5f, -0.01f, 0.f), new Vector3f(0.f, 0.f, 3.9f));
		rectangle.material = new Glossy(300.f, new Spectrum(1.f, 1.f, 1.f), new Spectrum(10.f, 10.f, 10.f));
		objects.add(rectangle);

		rectangle = new Rectangle(new Point3f(-1.f, -.5f, -1.95f), new Vector3f(-0.5f, -0.07f, 0.f), new Vector3f(0.f, 0.f, 3.9f));
		rectangle.material = new Glossy(1000.f, new Spectrum(1.f, 1.f, 1.f), new Spectrum(1.f, 1.f, 1.f));
		objects.add(rectangle);
		
		rectangle = new Rectangle(new Point3f(0.f, -.3f, -1.95f), new Vector3f(-0.5f, -0.14f, 0.f), new Vector3f(0.f, 0.f, 3.9f));
		rectangle.material = new Glossy(3300.f, new Spectrum(1.f, 1.f, 1.f), new Spectrum(1.f, 1.f, 1.f));
		objects.add(rectangle);
		
		rectangle = new Rectangle(new Point3f(1.f, -.0f, -1.95f), new Vector3f(-0.5f, -0.24f, 0.f), new Vector3f(0.f, 0.f, 3.9f));
		rectangle.material = new Glossy(10000.f, new Spectrum(1.f, 1.f, 1.f), new Spectrum(1.f, 1.f, 1.f));
		objects.add(rectangle);

		
		lightList = new LightList();
		
		// Add area lights
		//Red
		Point3f bottomLeft = new Point3f(1.95f, 2.f, -2.2f);
		Vector3f right = new Vector3f(0.f, .1f, 0.f);
		Vector3f top = new Vector3f(0.f, 0.f, -.1f);
		AreaLight AreaLight = new AreaLight(bottomLeft, right, top, new Spectrum(0.015625f, 0.0015625f, 0.0015625f));
		AreaLight = new AreaLight(bottomLeft, right, top, new Spectrum(1.f, 0.1f, 0.1f));
		objects.add(AreaLight);
		lightList.add(AreaLight);
		
		// blue
		bottomLeft = new Point3f(1.95f, 2.f, -0.6f);
		right = new Vector3f(0.f, .2f, 0.f);
		top = new Vector3f(0.f, 0.f, -.2f);
		AreaLight = new AreaLight(bottomLeft, right, top, new Spectrum(0.01225f, 0.01625f, 0.1225f));
		AreaLight = new AreaLight(bottomLeft, right, top, new Spectrum(0.1f, 0.1f, 1.f));
		objects.add(AreaLight);
		lightList.add(AreaLight);
		
		// green
		bottomLeft = new Point3f(1.95f, 2.f, 1.1f);
		right = new Vector3f(0.f, .4f, 0.f);
		top = new Vector3f(0.f, 0.f, -.4f);
		AreaLight = new AreaLight(bottomLeft, right, top, new Spectrum(0.05f, .5f, .05f));
		AreaLight = new AreaLight(bottomLeft, right, top, new Spectrum(0.1f, 1.f, 0.1f));
		objects.add(AreaLight);
		lightList.add(AreaLight);
		
		// yellow
		bottomLeft = new Point3f(1.95f, 2.f, 3.f);
		right = new Vector3f(0.f, 0.8f, 0.f);
		top = new Vector3f(0.f, 0.f, -0.8f);
		AreaLight = new AreaLight(bottomLeft, right, top, new Spectrum(2.f, 2.f, 0.2f));
		objects.add(AreaLight);
		lightList.add(AreaLight);
		
		// above & bright
		bottomLeft = new Point3f(-0.5f, 3.f, 0.75f);
		right = new Vector3f(0.f, 0.f, -0.5f);
		top = new Vector3f(0.5f, 0.f, 0.f);
		AreaLight = new AreaLight(bottomLeft, right, top, new Spectrum(40.f, 40.f, 40.f));
		objects.add(AreaLight);
		lightList.add(AreaLight);
		// Connect objects to root
		root = objects;
	}
}
