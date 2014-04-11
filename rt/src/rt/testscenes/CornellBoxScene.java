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

public class CornellBoxScene extends Scene {
	
	public CornellBoxScene()
	{	
		outputFilename = new String("../output/testscenes/CornellBoxScene-mine");
				
		// Specify pixel sampler to be used
		samplerFactory = new RandomSamplerFactory();
		
		// Samples per pixel
		SPP = 1024;
		outputFilename += String.format("_%d_SPP", SPP);
		
		// Make camera and film
		Vector3f eye = new Vector3f(278.f,273.f,-500.f);
		Vector3f lookAt = new Vector3f(278, 273, 0);
		Vector3f up = new Vector3f(0.f,1.f,0.f);
		float fov = (float) Math.toDegrees(0.95993109f);
		int width = 128;
		int height = 128;
		float aspect = (float)width/(float)height;
		camera = new PinholeCamera(eye, lookAt, up, fov, aspect, width, height);
		film = new BoxFilterFilm(width, height);						
		tonemapper = new ClampTonemapper();
		
		// Specify integrator to be used
		integratorFactory = new PathTracingIntegratorFactory();
		
		// List of objects
		IntersectableList objects = new IntersectableList();	
		
		Material gray = new Diffuse(new Spectrum(.5f));
		Rectangle floor = new Rectangle(new Point3f(0,0,0), new Point3f(552.8f,0,0), new Point3f(0,0,559.2f));
		floor.material = gray;
		Rectangle roof = new Rectangle(new Point3f(556, 548.8f, 0), new Point3f(556, 548.8f, 559.2f), new Point3f(0, 548.8f, 559.2f));
		roof.material = gray;
		Rectangle back = new Rectangle(new Point3f(549.6f, 0, 559.2f), new Point3f(0, 0, 559.2f), new Point3f(0, 548.8f, 559.2f));
		back.material = gray;
		Rectangle right = new Rectangle(new Point3f(0, 0, 559.2f), new Point3f(0, 0, 0), new Point3f(0, 548.8f, 0f));
		right.material = gray;
		Rectangle left = new Rectangle(new Point3f(552.8f, 0, 0f), new Point3f(549.6f, 0, 559.2f), new Point3f(556, 548.8f, 559.2f));
		Material red = new Diffuse(new Spectrum(.9f, .5f, .5f));
		right.material = red;

		objects.add(floor);
		objects.add(roof);
		objects.add(back);
		objects.add(left);
		objects.add(right);
		// Connect objects to root
		root = objects;
				
		// List of lights
		lightList = new LightList();
		//point light
		LightGeometry pl = new PointLight(new Vector3f(185, 538, 169), new Spectrum(412300, 341100, 298600));
		//are light
		LightGeometry al = new AreaLight(new Point3f(185, 538, 169), new Vector3f(80, 0, 0), 
				new Vector3f(0, 0, 80), new Spectrum(41.23f, 34.11f, 29.86f));
		lightList.add(al);
	}
	
}
