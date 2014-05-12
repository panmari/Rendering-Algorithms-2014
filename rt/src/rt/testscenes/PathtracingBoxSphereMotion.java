package rt.testscenes;

import javax.vecmath.*;

import rt.*;
import rt.intersectables.*;
import rt.tonemappers.*;
import rt.integrators.*;
import rt.lightsources.*;
import rt.materials.*;
import rt.materials.NoisyTexture.Type;
import rt.samplers.*;
import rt.cameras.*;
import rt.films.*;

public class PathtracingBoxSphereMotion extends Scene {
	
	public PathtracingBoxSphereMotion()
	{	
		outputFilename = new String("../output/testscenes/PathtracingBoxSphereMotion-mine");
				
		// Specify pixel sampler to be used
		samplerFactory = new RandomSamplerFactory();
		
		// Samples per pixel
		SPP = 128;
		outputFilename += String.format("_%d_SPP", SPP);
		
		// Make camera and film
		Vector3f eye = new Vector3f(-3.f,1.f,4.f);
		Vector3f lookAt = new Vector3f(0.f,1.f,0.f);
		Vector3f up = new Vector3f(0.f,1.f,0.f);
		float fov = 60.f;
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
		
		Sphere sphere = new Sphere();
		Matrix4f t = new Matrix4f();
		t.setIdentity();
		t.setScale(.5f);
		t.setTranslation(new Vector3f(-1.3f,-.25f,.7f));
		//Vector3f trans = new Vector3f(2,0,2);
		Instance i = new Instance(sphere, t);
		Vector3f trans = new Vector3f(.3f,0,.3f);
		Matrix4f motion = new Matrix4f();
		motion.rotY((float) Math.toRadians(20));;
		//motion.setTranslation(trans);
		AnimatedInstance sphereI = new AnimatedInstance(i, motion);
		
		sphereI.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		
		objects.add(sphereI);

		// Right, red wall
		Rectangle rectangle = new Rectangle(new Point3f(2.f, -.75f, 2.f), new Vector3f(0.f, 4.f, 0.f), new Vector3f(0.f, 0.f, -4.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.f, 0.f));
		objects.add(rectangle);
	
		// Bottom
		rectangle = new Rectangle(new Point3f(-2.f, -.75f, 2.f), new Vector3f(4.f, 0.f, 0.f), new Vector3f(0.f, 0.f, -4.f));
		Spectrum ext = new Spectrum(3.88f, 3.45f, 2.56f);
		Material g = new rt.materials.Glossy( 8.f, new Spectrum(0.131f, 0.12f, 0.144f), ext);
		rectangle.material = new NoisyTexture(g, Type.WOOD);
		objects.add(rectangle);

		// Top
		rectangle = new Rectangle(new Point3f(-2.f, 3.25f, 2.f), new Vector3f(0.f, 0.f, -4.f), new Vector3f(4.f, 0.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);
		
		// Left
		rectangle = new Rectangle(new Point3f(-2.f, -.75f, -2.f), new Vector3f(4.f, 0.f, 0.f), new Vector3f(0.f, 4.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);
		
		// Light source
		Point3f bottomLeft = new Point3f(-0.25f, 3.f, 0.25f);
		Vector3f right = new Vector3f(0.f, 0.f, -0.5f);
		Vector3f top = new Vector3f(0.5f, 0.f, 0.f);
		AreaLight rectangleLight = new AreaLight(bottomLeft, right, top, new Spectrum(100.f, 100.f, 100.f));
		objects.add(rectangleLight);
		
		// Connect objects to root
		root = objects;
				
		// List of lights
		lightList = new LightList();
		lightList.add(rectangleLight);
	}
	
}
