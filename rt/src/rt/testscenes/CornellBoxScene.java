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
		Material green = new Diffuse(new Spectrum(.3f, .5f, .3f));
		Material red = new Diffuse(new Spectrum(.9f, .5f, .5f));
		
		Rectangle floor = new Rectangle(new Point3f(552.8f,0,0), new Point3f(0,0,0), new Point3f(0,0,559.2f));
		floor.material = gray;
		Rectangle roof = new Rectangle(new Point3f(556, 548.8f, 0), new Point3f(556, 548.8f, 559.2f), new Point3f(0, 548.8f, 559.2f));
		roof.material = gray;
		Rectangle back = new Rectangle(new Point3f(549.6f, 0, 559.2f), new Point3f(0, 0, 559.2f), new Point3f(0, 548.8f, 559.2f));
		back.material = gray;
		Rectangle right = new Rectangle(new Point3f(0, 0, 559.2f), new Point3f(0, 0, 0), new Point3f(0, 548.8f, 0f));
		right.material = green;
		Rectangle left = new Rectangle(new Point3f(552.8f, 0, 0f), new Point3f(549.6f, 0, 559.2f), new Point3f(556, 548.8f, 559.2f));	
		left.material = red;

		objects.add(floor);
		objects.add(roof);
		objects.add(back);
		objects.add(left);
		objects.add(right);
		
		//instance(translate([185 83.5 169]) * rotate(-16.616, [0 1 0]), box([-82.5 -82.5 -82.5], [82.5 82.5 82.5], diffuse([.3 .5 .3])))
		//instance(translate([368 166 351]) * rotate(-72.766, [0 1 0]), box([-82.5 -168 -82.5], [82.5 165 82.5], diffuse([.5 .2 .2])))
		Intersectable unitBox = new UnitCube();
		
		Matrix4f t = new Matrix4f();
		t.setIdentity();
		t.setScale(82.5f);
		
		Matrix4f rot = new Matrix4f();
		rot.rotY((float) Math.toRadians(-16.616f));
		t.mul(rot);
		
		Matrix4f trans = new Matrix4f();
		trans.setIdentity();
		trans.setTranslation(new Vector3f(185, 83.5f, 169));
		t.mul(trans, t);
		Instance smallBox = new Instance(unitBox, t);
		smallBox.material = gray;
		objects.add(smallBox);
		
		t = new Matrix4f();
		t.setIdentity();
		t.setScale(82.5f);
		t.m11 = 166.5f;
		rot.rotY((float) Math.toRadians(-72.766f));
		t.mul(rot);
		
		trans = new Matrix4f();
		trans.setIdentity();
		trans.setTranslation(new Vector3f(368, 167.5f, 351));
		t.mul(trans, t);
		Instance bigBox = new Instance(unitBox, t);
		bigBox.material = gray;
		objects.add(bigBox);
		// Connect objects to root
		root = objects;
				
		// List of lights
		lightList = new LightList();
		//point light
		LightGeometry l;
		l = new PointLight(new Vector3f(278, 478, 279.5f), new Spectrum(412300, 341100, 298600));
		
		//area light
		Spectrum emission = new Spectrum(41.23f, 34.11f, 29.86f);
		emission.mult(50000);
		//l = new AreaLight(new Point3f(343, 548.6f, 227), new Vector3f(0, 0, 105), new Vector3f(-130, 0, 0), emission);
		lightList.add(l);
		objects.add(l);
	}
	
}
