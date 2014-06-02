package rt.testscenes;

import java.io.IOException;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import rt.*;
import rt.accelerators.BSPAccelerator;
import rt.cameras.PinholeCamera;
import rt.films.BoxFilterFilm;
import rt.integrators.*;
import rt.intersectables.*;
import rt.lightsources.*;
import rt.materials.*;
import rt.samplers.*;
import rt.tonemappers.ClampTonemapper;
import util.ColladaParser;

public class ColladaTestScene extends Scene {
	
	public ColladaTestScene()
	{
		// Output file name
		outputFilename = new String("../output/testscenes/ColladaTestScene");
		
		// Image width and height in pixels
		width = 640;
		height = 360;
		
		// Number of samples per pixel
		SPP = 4;
		
		// Specify which camera, film, and tonemapper to use
		Vector3f eye = new Vector3f(0.f, 0.f, 4.f);
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
		
		Material chessTexture = new Textured("../textures/chessboard.jpg", "../normalmaps/normal.gif");
		Material forestfloor = new Textured("../textures/egg.jpg", "../normalmaps/forestfloor.jpg");

		Material couch = new Textured("../textures/pink.jpg", "../normalmaps/couch.png");

		//Intersectable sphere = new BSPAccelerator(ColladaParser.parse("../obj/Heart.dae"));
		BSPAccelerator pokeball = null;
		Intersectable sphere = null;
		try {
			/*
			Mesh o = ObjReader.read("../obj/Pokeball.obj", 1);
			o.material = chessTexture;
			pokeball = new BSPAccelerator(o);
			*/
			Mesh m = ObjReader.read("../obj/Heart.obj", 1f);
			PerforatedMesh perforated = new PerforatedMesh(m, 0.5f);
			sphere = new BSPAccelerator(perforated);
			Matrix4f t = new Matrix4f();
			t.rotY((float) Math.toRadians(90));
			sphere = new Instance(sphere, t);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//sphere = pokeball;
		sphere = new Sphere();
		

		// Ground and back plane
		XYZGrid grid = new XYZGrid(new Spectrum(0.2f, 0.f, 0.f), new Spectrum(1.f, 1.f, 1.f), 0.1f, new Vector3f(0.f, 0.3f, 0.f));
		CSGPlane groundPlane = new CSGPlane(new Vector3f(0.f, 1.f, 0.f), 1.5f);
		groundPlane.material = couch;
		CSGPlane backPlane = new CSGPlane(new Vector3f(0.f, 0.f, 1.f), 3.15f);
		backPlane.material = grid;		
		
		// Collect objects in intersectable list
		IntersectableList intersectableList = new IntersectableList();
		intersectableList.add(sphere);	
		intersectableList.add(groundPlane);
		intersectableList.add(backPlane);
		
		// Set the root node for the scene
		root = intersectableList;
		
		// Light sources
		LightGeometry pointLight3 = new PointLight(new Vector3f(0.f, 5.f, 1.f), new Spectrum(100.f));
		lightList = new LightList();
		lightList.add(pointLight3);
	}
}