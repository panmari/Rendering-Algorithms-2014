package rt;

/*
 * Defines scene properties that need to be made accessible to the renderer. 
 */
public interface Scene {

	String getOutputFilename();
	
	Camera getCamera();
	Intersectable getIntersectable();
	LightList getLightList();
	Film getFilm();
	Tonemapper getTonemapper();
	int getSPP();
	
	IntegratorFactory getIntegratorFactory();
	SamplerFactory getSamplerFactory();

	void prepare();
}
